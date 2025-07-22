package com.urban.carbon.upload.infrastructure.utils;

import cn.hutool.core.lang.Assert;
import com.urban.carbon.api.upload.constants.SaveSoftType;
import com.urban.carbon.api.upload.response.data.UploadChunkInfo;
import com.urban.carbon.api.upload.response.data.UploadInitInfo;
import com.urban.carbon.api.upload.response.data.UploadStatusInfo;
import com.urban.carbon.base.exception.BizException;
import com.urban.carbon.file.configuration.properties.FileProperties;
import com.urban.carbon.file.exception.FileErrorCode;
import com.urban.carbon.file.exception.FileException;
import com.urban.carbon.file.strategy.FileStrategy;
import com.urban.carbon.file.strategy.FileStrategyFactory;
import com.urban.carbon.upload.domain.entity.UploadCache;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBitSet;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class FileHandler {

    /**
     * 临时文件存储路径
     */
    private static final String TEMP_PATH = System.getProperty("java.io.tmpdir");

    /**
     * 缓存文件分片大小
     */
    private static final int BUFFER_SIZE = 4096;

    /**
     * 文件属性
     */
    private final FileProperties fileProperties;

    /**
     * 文件策略工厂
     */
    private final FileStrategyFactory fileStrategyFactory;

    /**
     * 文件缓存工具
     */
    private final FileUploadCacheUtils fileUploadCacheUtils;

    /**
     * 构造方法
     *
     * @param fileProperties      文件属性
     * @param fileStrategyFactory 文件策略工厂
     */
    public FileHandler(FileProperties fileProperties, FileStrategyFactory fileStrategyFactory,
                       FileUploadCacheUtils fileUploadCacheUtils) {
        this.fileProperties = fileProperties;
        this.fileStrategyFactory = fileStrategyFactory;
        this.fileUploadCacheUtils = fileUploadCacheUtils;
    }

    /**
     * 初始化上传
     *
     * @param fileId 文件ID
     * @param fileSize 文件大小
     * @param saveSoft 保存软件
     * @param dataName 数据名称
     * @param dataDesc 数据描述
     * @return 初始化上传响应
     * @throws IOException 初始化上传时发生IO异常
     */
    public UploadInitInfo initUpload(String fileId, Long fileSize, SaveSoftType saveSoft,
                                     String dataName, String dataDesc) throws IOException {
        // 参数校验
        Assert.notNull(fileId, () -> new FileException("File Id Can't be null!",
                FileErrorCode.FILE_NOT_FOUND));
        Assert.isTrue(fileSize < parseFileSize(fileProperties.getTotalFileMaxSize()),
                () -> new FileException("File size too large!", FileErrorCode.FILE_UPLOAD_FAILED));
        // 分片大小与策略
        long chunkSize = parseFileSize(fileProperties.getSliceSize());
        saveSoft = calculateSaveSoft(saveSoft, fileSize);
        // 初始化临时文件
        openSpaceForTmpFile(fileId, fileSize);
        // 初始化缓存数据
        int totalChunks = (int) Math.ceil((double) fileSize / chunkSize);
        fileUploadCacheUtils.initCache(fileId, dataName, dataDesc, chunkSize,
                (long) totalChunks, fileSize, saveSoft);
        // 构造响应
        UploadInitInfo response = new UploadInitInfo();
        response.extracted(fileId, chunkSize, totalChunks, saveSoft);
        return response;
    }

    public UploadStatusInfo completeUpload(String fileId) {
        UploadCache cache = fileUploadCacheUtils.getCacheData(fileId);
        Assert.notNull(cache, () -> new FileException(
                "File Info Not Found", FileErrorCode.FILE_UPLOAD_FAILED));
        RBitSet chunkBits = fileUploadCacheUtils.getChunkBits(fileId);
        UploadStatusInfo response = buildUploadStatusResponse(chunkBits, fileId, cache);
        // 1. 检查是否存在失败的分片
        List<Integer> failedChunks = fileUploadCacheUtils.getFailedChunks(chunkBits.asBitSet());
        if (!failedChunks.isEmpty()) {
            response.setFailedChunks(failedChunks);
            log.warn("Found failed chunks: {}, upload can't be completed.", failedChunks);
            return response;
        }
        // 2. 等待所有分片上传完成（最多尝试 maxRetries 次）
        int maxRetries = 5;
        int retryCount = 0;
        while (chunkBits.cardinality() < cache.getTotalChunks() && retryCount++ < maxRetries) {
            try {
                Thread.sleep(1000); // 等待 1 秒
                chunkBits = fileUploadCacheUtils.getChunkBits(fileId); // 重新获取状态
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BizException(e, FileErrorCode.FILE_UPLOAD_FAILED);
            }
        }
        if (chunkBits.cardinality() != cache.getTotalChunks()) {
            log.warn("File Upload not completed within timeout! Please try again later!");
            return response;
        }
        // 3. 所有分片上传成功，开始合并文件并上传至存储
        try {
            String filePath = uploadFileToDataStore(fileId, cache.getSaveSoft());
            response.setFilePath(filePath);
            deleteTmpFile(fileId);
            fileUploadCacheUtils.deleteCache(fileId);
        } catch (Exception e) {
            log.error("Error occurred during finalizing upload for file {}", fileId, e);
            throw new BizException(e, FileErrorCode.FILE_UPLOAD_FAILED);
        }
        return response;
    }

    /**
     * 处理分片上传
     *
     * @param fileId 文件ID
     * @param chunkIndex 分片索引
     * @param fileInputStream 文件输入流
     * @param hashMD5 文件MD5
     * @return 处理分片上传的响应
     */
    public UploadChunkInfo handleChunkUpload(String fileId, Integer chunkIndex,
                                             InputStream fileInputStream, String hashMD5) {
        // 参数校验
        Assert.notNull(fileId, () -> new FileException("File Id Can't be null!",
                FileErrorCode.FILE_NOT_FOUND));
        // 获取缓存数据
        UploadCache cacheData = fileUploadCacheUtils.getCacheData(fileId);
        Assert.notNull(cacheData, () -> new FileException(
                "Please Init file upload info", FileErrorCode.FILE_UPLOAD_FAILED));
        Assert.isTrue(chunkIndex >= 0 && chunkIndex < cacheData.getTotalChunks(),
                () -> new FileException(FileErrorCode.INVALID_CHUNK_INDEX));
        Long chunkSize = cacheData.getChunkSize();
        Long totalChunks = cacheData.getTotalChunks();
        Assert.isTrue(chunkIndex >= 0 && chunkIndex < totalChunks,
                () -> new FileException(FileErrorCode.INVALID_CHUNK_INDEX));
        RBitSet chunkBits = fileUploadCacheUtils.getChunkBits(fileId);
        int status = fileUploadCacheUtils.getChunkStatus(chunkBits.asBitSet(), chunkIndex);
        UploadChunkInfo response = new UploadChunkInfo();
        if (status < 2) {
            long startLoc = chunkIndex * chunkSize;
            long endLoc = Math.min(startLoc + chunkSize, cacheData.getFileSize());
            try {
                fileUploadCacheUtils.setChunkStatus(chunkBits, chunkIndex, 1);
                storeChunkToFileSystem(fileId, startLoc, endLoc, fileInputStream, hashMD5);
                fileUploadCacheUtils.setChunkStatus(chunkBits, chunkIndex, 3);
            } catch (IOException e) {
                log.warn("Upload Failed，rollback slice {} status!", chunkIndex);
                fileUploadCacheUtils.setChunkStatus(chunkBits, chunkIndex, 2);
                throw new BizException(e, FileErrorCode.FILE_UPLOAD_FAILED);
            }
        }
        response.extracted(fileId, chunkIndex, true);
        return response;
    }

    /**
     * 取消上传
     *
     * @param fileId 文件ID
     * @return 取消上传的响应
     */
    public UploadStatusInfo cancelUpload(String fileId) {
        UploadCache cache = fileUploadCacheUtils.getCacheData(fileId);
        Assert.notNull(cache, () -> new FileException(
                "File upload info not found", FileErrorCode.FILE_UPLOAD_FAILED));
        RBitSet chunkBits = fileUploadCacheUtils.getChunkBits(fileId);
        List<Integer> uploadedChunks = fileUploadCacheUtils.getUploadedChunks(chunkBits.asBitSet());
        deleteTmpFile(fileId);
        fileUploadCacheUtils.deleteCache(fileId);
        UploadStatusInfo response = new UploadStatusInfo();
        response.buildResponse(fileId, cache.getTotalChunks(), cache.getDataName(),
                cache.getDataDesc(), cache.getFileSize(), cache.getSaveSoft(),
                cache.getDataType(), uploadedChunks, null, true);
        return response;
    }

    public Boolean deleteUploadFile(String fileId, String saveSoft, String filePath) {
        try {
            FileStrategy strategy = fileStrategyFactory.getStrategy(saveSoft);
            Assert.notNull(strategy, () -> new FileException(
                    "No strategy found for type: " + saveSoft,
                    FileErrorCode.FILE_DELETE_FAILED));
            boolean result = strategy.deleteFile(filePath);
            Assert.isTrue(result, () -> new FileException(
                    "Failed to delete file with path: " + filePath,
                    FileErrorCode.FILE_DELETE_FAILED));
            log.info("File deleted successfully: {}", fileId);
            fileUploadCacheUtils.deleteCache(fileId);
            return true;
        } catch (FileException e) {
            log.warn("Delete file {} failed due to business error.", fileId, e);
            throw e;
        } catch (Exception e) {
            log.error("Delete file {} failed due to unexpected exception.", fileId, e);
            throw new FileException(e, FileErrorCode.FILE_DELETE_FAILED);
        }
    }

    /**
     * 构建上传状态响应信息
     *
     * <p>该方法根据给定的文件ID和缓存信息，查询已上传分片列表，
     * 并构造一个包含完整上传状态的 {@link UploadStatusInfo} 对象。</p>
     *
     * @param fileId 文件唯一标识
     * @param cache  上传缓存信息，包含文件元数据和配置参数
     * @return 包含上传状态的响应对象 {@link UploadStatusInfo}
     */
    private UploadStatusInfo buildUploadStatusResponse(
            RBitSet chunkBits, String fileId, UploadCache cache) {
        List<Integer> uploadedChunks = fileUploadCacheUtils.getUploadedChunks(chunkBits.asBitSet());
        UploadStatusInfo response = new UploadStatusInfo();
        response.buildResponse(fileId, cache.getTotalChunks(), cache.getDataName(),
                cache.getDataDesc(), cache.getFileSize(), cache.getSaveSoft(),
                cache.getDataType(), uploadedChunks, null, false);
        return response;
    }

    /**
     * 计算保存软件
     *
     * @param saveSoft 保存软件
     * @param fileSize 文件大小
     * @return 保存软件
     */
    private SaveSoftType calculateSaveSoft(SaveSoftType saveSoft, long fileSize) {
        long threshold = parseFileSize(fileProperties.getThreshold());
        return Objects.requireNonNullElseGet(saveSoft,
                () -> fileSize < threshold * 1024 * 1024 ? SaveSoftType.MINIO : SaveSoftType.HDFS);
    }

    /**
     * 将文件大小字符串转换为字节数（支持 KB/MB/GB）
     *
     * @param fileSizeStr 文件大小字符串
     * @return 文件大小字节数
     */
    private long parseFileSize(String fileSizeStr) {
        if (fileSizeStr == null || fileSizeStr.isEmpty()) {
            return 0L;
        }
        String dealFileSizeStr = fileSizeStr.trim().toUpperCase();
        long fileSize = Long.parseLong(dealFileSizeStr.substring(
                0, dealFileSizeStr.length() - 2));
        if (dealFileSizeStr.endsWith("KB")) {
            return fileSize * 1024;
        } else if (dealFileSizeStr.endsWith("MB")) {
            return fileSize * 1024 * 1024;
        } else if (dealFileSizeStr.endsWith("GB")) {
            return fileSize * 1024 * 1024 * 1024;
        } else {
            return Long.parseLong(dealFileSizeStr); // 默认为字节
        }
    }

    /**
     * 创建临时文件并分配空间
     *
     * @param fileId   文件ID
     * @param fileSize 文件大小
     * @throws IOException 创建临时文件时发生IO异常
     */
    private void openSpaceForTmpFile(String fileId, long fileSize) throws IOException {
        Path path = Paths.get(TEMP_PATH, fileId);
        RandomAccessFile raf = new RandomAccessFile(path.toString(), "rw");
        raf.setLength(fileSize);
        raf.close();
    }


    /**
     * 将数据块存储到文件系统中<p>
     * 该方法负责将一个给定的数据块（通过InputStream表示）写入到文件系统中的指定位置
     * 它主要用于处理大文件上传时的分块存储，每个数据块对应文件的一个部分
     *
     * @param fileId      文件ID，用于标识存储文件的唯一性
     * @param startLoc    数据块在文件中的起始位置
     * @param endLoc      数据块在文件中的结束位置
     * @param inputStream 输入流，包含要存储的数据块内容
     * @throws IOException 如果在读写文件过程中发生I/O错误
     */
    private void storeChunkToFileSystem(
            String fileId, long startLoc, long endLoc,
            InputStream inputStream, String hashMD5) throws IOException {
        // 使用 try-with-resources 确保 RandomAccessFile 在操作完成后能够被正确关闭
        try (RandomAccessFile raf = new RandomAccessFile(
                Paths.get(TEMP_PATH, fileId).toString(), "rw")) {
            raf.seek(startLoc);
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            long remaining = endLoc - startLoc;
            while (remaining > 0 && (bytesRead = inputStream.read(
                    buffer, 0, (int) Math.min(BUFFER_SIZE, remaining))) != -1) {
                raf.write(buffer, 0, bytesRead);
                remaining -= bytesRead;
            }
            if (remaining != 0) {
                throw new IOException("Can't write all Data In File: " + remaining + " B");
            }
        }
    }

    /**
     * 上传文件到数据存储系统
     *
     * @param fileId       文件ID
     * @param saveSoftType 保存数据库的类型
     * @return 上传成功后的文件路径
     */
    private String uploadFileToDataStore(String fileId, SaveSoftType saveSoftType) {
        // 获取临时文件路径
        Path tmpFilePath = Paths.get(TEMP_PATH, fileId);
        // 上传文件
        FileStrategy strategy = fileStrategyFactory.getStrategy(saveSoftType.name());
        String dstPath = strategy.uploadFile(tmpFilePath);
        // 上传文件
        Assert.notNull(dstPath, () -> new BizException(
                "Temp File Failed send to Save Soft", FileErrorCode.FILE_UPLOAD_FAILED));
        return dstPath;
    }

    /**
     * 删除临时文件
     *
     * @param fileId 文件ID
     */
    private void deleteTmpFile(String fileId) {
        Path path = Paths.get(TEMP_PATH, fileId);
        if (Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                log.warn("Delete Tmp File Failed: {}", fileId, e);
                throw new FileException(e, FileErrorCode.FILE_DELETE_FAILED);
            }
        }
    }
}
