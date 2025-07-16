package com.urban.carbon.upload.domain.service;

import cn.hutool.core.lang.Assert;
import com.urban.carbon.api.upload.constants.SaveSoftType;
import com.urban.carbon.api.upload.request.UploadChunkRequest;
import com.urban.carbon.api.upload.request.UploadInitRequest;
import com.urban.carbon.api.upload.response.data.UploadChunkInfo;
import com.urban.carbon.api.upload.response.data.UploadInitInfo;
import com.urban.carbon.api.upload.response.data.UploadStatusInfo;
import com.urban.carbon.base.exception.BizException;
import com.urban.carbon.cache.constant.CacheConstant;
import com.urban.carbon.file.configuration.properties.FileProperties;
import com.urban.carbon.file.exception.FileErrorCode;
import com.urban.carbon.file.strategy.FileStrategy;
import com.urban.carbon.file.strategy.FileStrategyFactory;
import com.urban.carbon.lock.DistributeLock;
import com.urban.carbon.upload.domain.entity.UploadCache;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBitSet;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

@Slf4j
@Service
public class FileService {
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
     * Redis客户端
     */
    private final RedissonClient redissonClient;

    /**
     * 构造方法
     * @param fileProperties 文件属性
     * @param fileStrategyFactory 文件策略工厂
     */
    public FileService(FileProperties fileProperties, RedissonClient redissonClient,
                                 FileStrategyFactory fileStrategyFactory) {
        this.fileProperties = fileProperties;
        this.redissonClient = redissonClient;
        this.fileStrategyFactory = fileStrategyFactory;
    }

    public UploadInitInfo initUpload(UploadInitRequest request) throws IOException {
        String fileId = request.getFileId();
        long fileSize = request.getFileSize();
        long threshold = parseFileSize(fileProperties.getThreshold());
        // 校验参数
        Assert.notNull(fileId, () -> new BizException(
                "File Id Can't be null! ", FileErrorCode.FILE_NOT_FOUND));
        long maxFileSize = parseFileSize(fileProperties.getTotalFileMaxSize());
        Assert.isTrue(fileSize < maxFileSize, () -> new BizException(
                "File size greater than max file size! ", FileErrorCode.FILE_UPLOAD_FAILED));
        // 1. 检查文件大小
        long chunkSize = parseFileSize(fileProperties.getSliceSize());
        int totalChunks = (int) Math.ceil((double) fileSize / chunkSize);
        // 1.1 设置保存文件的方式，通过枚举值来表示不同的保存策略
        SaveSoftType saveSoft;
        if (request.getSaveSoft() == null) {
            if (request.getFileSize() < threshold * 1024 * 1024) {
                saveSoft = SaveSoftType.MINIO;
            } else {
                saveSoft = SaveSoftType.HDFS;
            }
        } else {
            saveSoft = request.getSaveSoft();
        }
        // 2. 创建空文件占位
        openSpaceForTmpFile(fileId, fileSize);
        // 3. 缓存总分片数
        String fileKey = CacheConstant.FILE_CACHE_KEY_PREFIX + fileId;
        RBitSet bitSet = redissonClient.getBitSet(fileKey + ":bits");
        bitSet.set(new BitSet(totalChunks * 2));
        // 4. 缓存文件信息
        UploadCache cacheData = new UploadCache();
        cacheData.createCache(request, chunkSize, (long) totalChunks, fileSize, saveSoft);
        RBucket<UploadCache> bucket = redissonClient.getBucket(fileKey + ":info");
        bucket.set(cacheData);
        // 5. 返回初始化上传响应
        UploadInitInfo response = new UploadInitInfo();
        response.extracted(fileId, chunkSize, totalChunks, saveSoft);
        return response;
    }

    @DistributeLock(scene = "UPLOAD-CHUNK",
            keyExpression = "#request.fileId + ':' + #request.chunkIndex")
    public UploadChunkInfo handleChunkUpload(UploadChunkRequest request) {
        // 基础变量准备
        String fileId = request.getFileId();
        Long chunkIndex = request.getChunkIndex();
        String fileKey = CacheConstant.FILE_CACHE_KEY_PREFIX + fileId;
        // 缓存获取
        RBitSet chunkBits = redissonClient.getBitSet(fileKey + ":bits");
        UploadCache cacheData = (UploadCache) redissonClient.getBucket(fileKey + ":info").get();
        Assert.notNull(cacheData, () -> new BizException(
                "Please Init file upload info", FileErrorCode.FILE_UPLOAD_FAILED));
        long chunkSize = cacheData.getChunkSize();
        long fileSize = cacheData.getFileSize();
        // 检查
        Long totalChunks = cacheData.getTotalChunks();
        Assert.isTrue(chunkIndex >= 0 && chunkIndex < totalChunks,
                () -> new BizException(FileErrorCode.INVALID_CHUNK_INDEX));
        // 创建返回
        UploadChunkInfo response = new UploadChunkInfo();
        // 幂等判断
        if (getChunkStatus(chunkBits, chunkIndex) < 2) {
            // 写入本地磁盘
            long startLoc = chunkIndex * chunkSize;
            long endLoc = Math.min(startLoc + chunkSize, fileSize);
            try {
                this.setChunkStatus(chunkBits, chunkIndex, 1);
                storeChunkToFileSystem(fileId, startLoc, endLoc, request.getFileInputStream());
                this.setChunkStatus(chunkBits, chunkIndex, 3);
            } catch (IOException e) {
                log.warn("Upload Failed，rollback slice {} status!", chunkIndex);
                this.setChunkStatus(chunkBits, chunkIndex, 2);
                throw new BizException(e, FileErrorCode.FILE_UPLOAD_FAILED);
            }
        }
        response.extracted(fileId, chunkIndex, true);
        return response;
    }

    @DistributeLock(scene = "COMPLETE", keyExpression = "#request.fileId")
    public UploadStatusInfo completeUpload(String fileId) {
        // 从 Redis 获取 bitset，查看是否所有的分片都已经上传完毕
        String fileKey = CacheConstant.FILE_CACHE_KEY_PREFIX + fileId;
        RBitSet chunkBits = redissonClient.getBitSet(fileKey + ":bits");
        // 获取缓存数据
        RBucket<UploadCache> totalChunksBucket = redissonClient.getBucket(fileKey + ":info");
        UploadCache cache = totalChunksBucket.get();
        //  检查
        Assert.notNull(cache.getTotalChunks(), () -> new BizException(
                "File Info Can't find, please init first", FileErrorCode.FILE_UPLOAD_FAILED));
        // 创建返回
        UploadStatusInfo response = new UploadStatusInfo();
        List<Integer> uploadedChunks = gerUploadedChunkList(chunkBits.asBitSet());
        response.buildResponse(fileId, cache.getTotalChunks(), cache.getDataName(),
                cache.getDataDesc(), cache.getFileSize(), cache.getSaveSoft(),
                cache.getDataType(), uploadedChunks, null, false);
        //  检查如果没有全部创建成功
        if (chunkBits.cardinality() != cache.getTotalChunks()) {
            log.warn("File Upload not completed! Please try again later!");
        } else {
            // 写入结果
            response.setFilePath(uploadFileToDataStore(fileId, cache.getSaveSoft()));
            // 删除临时文件
            deleteTmpFile(fileId);
            // 清空缓存
            chunkBits.delete();
            totalChunksBucket.delete();
        }
        return response;
    }

    public UploadStatusInfo getUploadStatus(String fileId) {
        // 构造文件在缓存中的键
        String fileKey = CacheConstant.FILE_CACHE_KEY_PREFIX + fileId;
        // 获取文件上传缓存信息的桶，用于存储文件上传的元数据
        RBitSet chunkBits = redissonClient.getBitSet(fileKey + ":bits");
        RBucket<UploadCache> bucket = redissonClient.getBucket(fileKey + ":info");
        UploadCache cache = bucket.get();
        // 如果缓存信息或总分块数为空，则抛出异常，表示未找到文件上传信息
        if (cache == null || cache.getTotalChunks() == null) {
            throw new BizException("File upload info not found", FileErrorCode.STATUS_GET_FAILED);
        }
        // 格式化并返回上传进度
        UploadStatusInfo resp = new UploadStatusInfo();
        resp.buildResponse(fileId, cache.getTotalChunks(), cache.getDataName(),
                cache.getDataDesc(), cache.getFileSize(), cache.getSaveSoft(),
                cache.getDataType(), gerUploadedChunkList(chunkBits.asBitSet()),
                null, false);
        return resp;
    }

    @DistributeLock(scene = "CANCEL_FILE", keyExpression = "#request.fileId")
    public UploadStatusInfo cancelUpload(String fileId) {
        // 构造文件缓存键
        String fileKey = CacheConstant.FILE_CACHE_KEY_PREFIX + fileId;
        // 获取存储已上传分片信息的 BitSet
        RBitSet chunkBits = redissonClient.getBitSet(fileKey + ":bits");
        // 获取存储上传缓存信息的 Bucket
        RBucket<UploadCache> bucket = redissonClient.getBucket(fileKey + ":info");
        // 从缓存中获取上传信息
        UploadCache cache = bucket.get();
        // 如果上传信息或总分片数为空，则抛出异常
        if (cache == null || cache.getTotalChunks() == null) {
            throw new BizException("File upload info not found", FileErrorCode.FILE_UPLOAD_FAILED);
        }
        // 删除本地临时文件
        deleteTmpFile(fileId);
        // 构造响应
        UploadStatusInfo response = new UploadStatusInfo();
        // 获取已上传的分片列表
        List<Integer> uploadedChunks = gerUploadedChunkList(chunkBits.asBitSet());
        // 填充响应对象
        response.buildResponse(fileId, cache.getTotalChunks(), cache.getDataName(),
                cache.getDataDesc(), cache.getFileSize(), cache.getSaveSoft(),
                cache.getDataType(), uploadedChunks, null, true);
        // 清理 Redis 缓存
        chunkBits.delete();
        bucket.delete();
        // 返回上传状态信息
        return response;
    }

    @DistributeLock(scene = "DELETE_FILE", keyExpression = "#fileId")
    public Boolean deleteUploadFile(String fileId, String saveSoftType, String filePath) {
        return fileStrategyFactory.getStrategy(saveSoftType).deleteFile(filePath);
    }

    /**
     * 获取指定分片的处理状态
     * 通过检查分片对应的两位标志位来确定分片的状态
     *
     * @param chunkBits  存储分片状态的位集合
     * @param chunkIndex 分片的索引
     * @return 分片的状态码，由两位标志位组合而成
     */
    private int getChunkStatus(RBitSet chunkBits, Long chunkIndex) {
        // 获取第 chunkIndex 个分片的状态
        boolean bit0 = chunkBits.get(chunkIndex * 2);
        boolean bit1 = chunkBits.get(chunkIndex * 2 + 1);
        return (bit1 ? 2 : 0) | (bit0 ? 1 : 0); // 合并两个 bit 得到状态码
    }

    /**
     * 设置指定分片的状态标志位
     * 每个分片使用两个位来表示状态：0(00)、1(01)、2(10)
     * 0-未上传，1-正在上传，2-上传失败，3-上传成功
     *
     * @param chunkBits  存储分片状态的位集合
     * @param chunkIndex 分片的索引
     * @param status     状态码，取值为 0~2
     */
    private void setChunkStatus(RBitSet chunkBits, Long chunkIndex, int status) {
        // 校验状态码是否合法
        if (status < 0 || status > 3) {
            throw new IllegalArgumentException("Status must be between 0 and 3");
        }
        // 设置第一位（bit0）
        chunkBits.set(chunkIndex * 2, (status & 1) == 1);
        // 设置第二位（bit1）
        chunkBits.set(chunkIndex * 2 + 1, (status & 2) == 2);
    }

    /**
     * 获取已上传的块列表
     * 该方法通过检查块状态位图来确定哪些块已经被上传
     *
     * @param chunkBits 块状态位图, 用于记录每个块的状态
     * @return 返回一个包含所有已上传块索引的列表
     */
    private List<Integer> gerUploadedChunkList(BitSet chunkBits) {
        // 创建一个列表, 用于存储已上传的块的索引
        List<Integer> uploadedChunks = new ArrayList<>();
        // 遍历块状态位图的每一个块
        for (int idx = 0; idx < chunkBits.size(); idx += 2) {
            // 检查当前块的状态是否为已上传（状态码为3）
            if (chunkBits.get(idx) && chunkBits.get(idx + 1)) {
                // 如果块已上传, 则将其索引添加到已上传块列表中
                uploadedChunks.add(idx / 2);
            }
        }
        // 返回已上传块的列表
        return uploadedChunks;
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
            InputStream inputStream) throws IOException {
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
                log.warn("Delete Tmp File Failed!", e);
            }
        }
    }
}
