package com.urban.carbon.data.manager.domain.service;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.urban.carbon.api.data.manager.constants.FileUploadStatus;
import com.urban.carbon.api.data.manager.constants.SaveSoftType;
import com.urban.carbon.api.data.manager.request.UploadChunkRequest;
import com.urban.carbon.api.data.manager.request.UploadInitRequest;
import com.urban.carbon.api.data.manager.response.data.UploadChunkInfo;
import com.urban.carbon.api.data.manager.response.data.UploadInitInfo;
import com.urban.carbon.api.data.manager.response.data.UploadStatusInfo;
import com.urban.carbon.base.exception.BizException;
import com.urban.carbon.data.manager.domain.entity.Data;
import com.urban.carbon.data.manager.domain.entity.FileUploadChunk;
import com.urban.carbon.file.configuration.properties.FileProperties;
import com.urban.carbon.file.exception.FileErrorCode;
import com.urban.carbon.file.exception.FileException;
import com.urban.carbon.file.strategy.FileStrategy;
import com.urban.carbon.file.strategy.FileStrategyFactory;
import com.urban.carbon.lock.DistributeLock;
import com.urban.carbon.data.manager.infrastructure.mapper.FileUploadChunkMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FileService extends ServiceImpl<FileUploadChunkMapper, FileUploadChunk> {

    /**
     * 临时文件存储路径
     */
    private static final String TEMP_PATH = System.getProperty("java.io.tmpdir");

    /**
     * 缓存文件分片大小
     */
    private static final int BUFFER_SIZE = 4096;

    /**
     * 文件策略工厂
     */
    private final FileStrategyFactory fileStrategyFactory;

    /**
     * 文件属性
     */
    private final FileProperties fileProperties;

    /**
     * 文件分片上传记录表
     */
    private final FileUploadChunkMapper fileUploadChunkMapper;

    /**
     * Redis 客户端
     */
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 构造方法
     *
     * @param fileStrategyFactory 文件策略工厂
     */
    public FileService(FileStrategyFactory fileStrategyFactory, FileProperties fileProperties,
                       FileUploadChunkMapper fileUploadChunkMapper, StringRedisTemplate stringRedisTemplate) {
        this.fileStrategyFactory = fileStrategyFactory;
        this.fileProperties = fileProperties;
        this.fileUploadChunkMapper = fileUploadChunkMapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 初始化上传
     *
     * @param request 初始化上传请求
     * @return 初始化上传响应
     */
    public UploadInitInfo initUpload(UploadInitRequest request) {
        String fileId = request.getFileId();
        Long fileSize = request.getFileSize();
        // 参数校验
        Assert.notNull(fileId, () -> new FileException("File Id Can't be null!",
                FileErrorCode.FILE_NOT_FOUND));
        Assert.isTrue(fileSize < parseFileSize(fileProperties.getTotalFileMaxSize()),
                () -> new FileException("File size too large!", FileErrorCode.FILE_UPLOAD_FAILED));
        // 分片大小与策略
        long chunkSize = parseFileSize(fileProperties.getSliceSize());
        SaveSoftType saveSoft = calculateSaveSoft(request.getSaveSoft(), fileSize);
        // 初始化临时文件
        openSpaceForTmpFile(fileId, fileSize);
        // 初始化缓存数据
        int totalChunks = (int) Math.ceil((double) fileSize / chunkSize);
        // 构造响应
        UploadInitInfo response = new UploadInitInfo();
        response.extracted(fileId, chunkSize, totalChunks, saveSoft);
        return response;
    }

    /**
     * 处理分片上传的业务方法
     * 该方法负责接收上传的文件分片请求，并处理该请求，包括验证、保存分片等操作
     * 使用分布式锁注解，以防止同一文件分片的并发上传
     * 锁的场景是"UPLOAD-CHUNK"，锁的键是文件ID和分片索引的组合
     *
     * @param request 包含分片上传所需信息的请求对象，包括文件ID、分片索引、分片大小等
     * @return 返回一个包含上传分片信息的对象，用于记录上传状态
     */
    @DistributeLock(scene = "UPLOAD-CHUNK",
            keyExpression = "#request.fileId + ':' + #request.chunkIndex")
    public UploadChunkInfo handleChunkUpload(UploadChunkRequest request) {
        // 提取请求中的文件ID
        String fileId = request.getFileId();
        // 提取请求中的分片索引
        Integer chunkIndex = request.getChunkIndex();
        // 提取请求中的分片大小
        Long chunkSize = request.getChunkSize();
        // 提取请求中的文件总大小
        Long fileSize = request.getFileSize();
        // 提取请求中的MD5哈希值，用于验证数据完整性
        String hashMD5 = request.getHashMD5();
        // 上传分片，并获取结果chunk
        FileUploadChunk chunk = getUploadChunk(request.getFileInputStream(), fileId, chunkIndex,
                chunkSize, fileSize, hashMD5);
        // 创建一个对象来存储上传分片的信息
        UploadChunkInfo info = new UploadChunkInfo();
        // 查询当前文件块的记录
        List<FileUploadChunk> chunks = fileUploadChunkMapper.findByFileId(
                chunk.getFileId());
        boolean uploadResult = this.saveOrUpdate(chunk);
        // 计算进度
        info.setProgress(chunks.size() * 100.0 / request.getTotalChunks());
        // 将数据库中的信息转换成 UploadChunkInfo
        info.extracted(chunk.getFileId(), chunk.getChunkIndex(), uploadResult,
                FileUploadStatus.valueOf(chunk.getStatus()));
        // 返回包含上传分片信息的对象
        return info;
    }

    /**
     * 检查所有文件块是否都已上传完成
     *
     * @param uploadId    文件上传的唯一标识符，用于跟踪特定的文件上传过程
     * @param totalChunks 预期的文件块总数，用于判断上传是否完成
     * @return 如果所有文件块都已上传完成，则返回true；否则返回false
     */
    public List<Integer> allCompleted(String uploadId, Integer totalChunks) {
        return this.fileUploadChunkMapper.allCompleted(uploadId, totalChunks);
    }

    /**
     * 合并所有已上传的文件块
     * <p>
     * 此方法首先检查所有文件块是否已成功上传，然后构建上传状态信息，并将文件上传到指定位置
     * 之后，删除临时文件，并返回上传状态信息
     *
     * @param uploadId 文件上传的唯一标识符
     * @param data     包含文件上传相关信息的数据对象，包括总块数、文件名、描述、文件大小等
     * @return 返回一个包含文件上传状态信息的UploadStatusInfo对象
     */
    @DistributeLock(scene = "COMPLETE", keyExpression = "#uploadId")
    public UploadStatusInfo mergeChunks(String uploadId, Data data) {
        // 检查所有文件块是否已成功上传
        List<Integer> successChunks = this.allCompleted(uploadId, data.getTotalChunks());
        Integer totalChunks = data.getTotalChunks();
        // 初始化上传状态信息对象
        UploadStatusInfo response = new UploadStatusInfo();
        // 将保存方式字符串转换为枚举类型
        SaveSoftType saveSoftType = SaveSoftType.valueOf(data.getSaveSoft());
        // 文件存储路径
        String filePath = null;
        // 检查所有文件块是否已成功上传
        if (successChunks.size() == totalChunks) {
            // 将文件上传到指定位置
            filePath = uploadFileToDataStore(uploadId, saveSoftType);
            // 删除临时文件
            Boolean deleteResult = deleteTmpFile(uploadId);
            Assert.isTrue(deleteResult, () -> new FileException("Delete Tmp File Failed!",
                    FileErrorCode.FILE_UPLOAD_FAILED));
        }
        // 构建上传状态信息
        response.buildResponse(uploadId, data.getTotalChunks(), data.getName(), data.getDescription(),
                data.getFileSize(), saveSoftType, data.getType(), successChunks, filePath, false);
        // 返回上传状态信息
        return response;
    }

    /**
     * 获取文件上传状态信息
     *
     * @param uploadId 上传标识符，用于跟踪上传过程
     * @param data     文件上传相关数据，包括总块数、文件名、描述等
     * @return 返回上传状态信息对象，包含上传详细状态
     */
    public UploadStatusInfo getUploadStatus(@NotBlank String uploadId, Data data) {
        // 检查所有文件块是否已成功上传
        List<Integer> successChunks = this.allCompleted(uploadId, data.getTotalChunks());
        // 初始化上传状态信息对象
        UploadStatusInfo response = new UploadStatusInfo();
        // 将保存方式字符串转换为枚举类型
        SaveSoftType saveSoftType = SaveSoftType.valueOf(data.getSaveSoft());
        // 构建上传状态信息
        response.buildResponse(uploadId, data.getTotalChunks(), data.getName(), data.getDescription(),
                data.getFileSize(), saveSoftType, data.getType(), successChunks, data.getFilePath(),
                !data.getStatus().equals(FileUploadStatus.CANCELED.name()));
        return response;
    }

    private void waitForLockRelease(String uploadId) {
        // 匹配上传或者完成操作两个场景
        String pattern = "*" + uploadId + "*";
        int retryCount = 0;
        int maxRetries = 5;
        long waitTime = 200;

        while (true) {
            Set<String> keys = stringRedisTemplate.keys(pattern);
            if (!keys.isEmpty()) {
                if (++retryCount >= maxRetries) {
                    throw new FileException("等待锁释放超时，请稍后重试",
                            FileErrorCode.FILE_DELETE_FAILED);
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(waitTime);
                } catch (InterruptedException e) {
                    log.warn("Wait for lock...");
                }
            } else {
                break;
            }
        }
    }


    /**
     * 取消文件上传
     * 此方法用于取消一个正在进行的文件上传任务它首先从数据库中清除与上传ID相关的所有数据，
     * 然后删除相关的临时文件如果任何一个步骤失败，将抛出异常
     *
     * @param uploadId 上传任务的唯一标识符
     * @return 如果上传任务取消成功，则返回true
     * @throws FileException 如果文件删除失败，则抛出文件异常
     */
    public Boolean cancelUpload(String uploadId) {
        // TODO 这里我们可以扫描Redis中是否存在锁，来解决脏数据的问题
        waitForLockRelease(uploadId);
        // 清空表格中关于 uploadId 的数据
        Boolean clearResult = this.fileUploadChunkMapper.clear(uploadId);
        // 确保数据清除成功，否则抛出文件删除失败异常
        Assert.isTrue(clearResult, () -> new FileException(FileErrorCode.FILE_DELETE_FAILED));
        // 删除临时文件
        Boolean deleteResult = deleteTmpFile(uploadId);
        // 确保临时文件删除成功，否则抛出文件删除失败异常
        Assert.isTrue(deleteResult, () -> new FileException(FileErrorCode.FILE_DELETE_FAILED));
        // 返回成功
        return true;
    }


    /**
     * 下载文件到指定的输出流
     *
     * @param filePath 文件路径，表示需要下载的文件位置
     * @param saveSoft 保存软件的标识，用于选择合适的文件策略
     * @param response 输出流，用于接收下载的文件数据
     * @throws IOException 当文件下载过程中发生I/O错误时抛出此异常
     */
    public void downloadFile(String filePath, String saveSoft, OutputStream response) throws IOException {
        // 根据保存软件的标识获取对应的文件策略
        FileStrategy strategy = fileStrategyFactory.getStrategy(saveSoft);
        // 使用获取到的策略执行文件下载操作
        strategy.downloadFile(filePath, response);
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
     */
    private void openSpaceForTmpFile(String fileId, long fileSize) {
        try {
            Path path = Paths.get(TEMP_PATH, fileId);
            RandomAccessFile raf = new RandomAccessFile(path.toString(), "rw");
            raf.setLength(fileSize);
            raf.close();
        } catch (IOException e) {
            throw new FileException(FileErrorCode.FILE_UPLOAD_FAILED);
        }
    }


    /**
     * 将数据块存储到文件系统中
     * <p>
     * 该方法负责将一个给定的数据块（通过InputStream表示）写入到文件系统中的指定位置
     * 它主要用于处理大文件上传时的分块存储，每个数据块对应文件的一个部分
     *
     * @param fileId      文件ID，用于标识存储文件的唯一性
     * @param startLoc    数据块在文件中的起始位置
     * @param endLoc      数据块在文件中的结束位置
     * @param inputStream 输入流，包含要存储的数据块内容
     */
    private String storeChunkToFileSystem(
            String fileId, long startLoc, long endLoc,
            InputStream inputStream, String hashMD5) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new FileException(FileErrorCode.MD5_TOOL_INIT_ERROR);
        }
        // 使用 try-with-resources 确保 RandomAccessFile 在操作完成后能够被正确关闭
        try (RandomAccessFile raf = new RandomAccessFile(
                Paths.get(TEMP_PATH, fileId).toString(), "rw")) {
            // 移动到合适的位置
            raf.seek(startLoc);
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            long remaining = endLoc - startLoc;
            while (remaining > 0 && (bytesRead = inputStream.read(
                    buffer, 0, (int) Math.min(BUFFER_SIZE, remaining))) != -1) {
                // 将内容写入文件对应的位置
                raf.write(buffer, 0, bytesRead);
                // md5 更新
                md5.update(buffer, 0, bytesRead);
                remaining -= bytesRead;
            }
            if (remaining != 0) {
                throw new IOException("Can't write all Data In File: " + remaining + " B");
            }
            // md5 校验
            byte[] digest = md5.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return hashMD5.contentEquals(sb) ? hashMD5 : null;
        } catch (IOException e) {
            log.error("Error writing file: {}", e.getMessage(), e);
            return null;
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
    private Boolean deleteTmpFile(String fileId) {
        Path path = Paths.get(TEMP_PATH, fileId);
        if (Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                log.warn("Delete Tmp File Failed: {}", fileId, e);
                throw new FileException(e, FileErrorCode.FILE_DELETE_FAILED);
            }
        }
        return true;
    }

    /**
     * 获取或创建并更新文件上传分片信息
     * 此方法首先尝试从数据库中获取现有的上传分片信息如果不存在，则上传新的分片并记录上传结果
     * 如果上传失败，将状态标记为FAILED；如果上传成功，将状态标记为COMPLETED对于已经存在但上传失败的分片，
     * 将重新尝试上传，并更新其状态和重试次数
     *
     * @param inputStream 文件输入流，用于上传分片数据
     * @param fileId      文件ID，用于标识特定的文件
     * @param chunkIndex  分片索引，用于标识文件的分片位置
     * @param chunkSize   分片大小，表示每个分片的数据量
     * @param fileSize    文件大小，用于在上传新分片时参考
     * @param hashMD5     分片的MD5哈希值，用于校验分片数据的完整性
     * @return 返回更新后的分片信息对象
     */
    private FileUploadChunk getUploadChunk(
            InputStream inputStream, String fileId, Integer chunkIndex, Long chunkSize,
            Long fileSize, String hashMD5) {
        // 从 数据库 获取分片信息
        FileUploadChunk chunk = fileUploadChunkMapper.findByFileIdAndIndex(fileId, chunkIndex);
        if (chunk == null) {
            // 上传新的分片并获取结果
            String result = uploadChunk(inputStream, chunkIndex, chunkSize, fileSize, fileId, hashMD5);
            chunk = new FileUploadChunk();
            chunk.recordChunk(fileId, chunkIndex, chunkSize, FileUploadStatus.FAILED.name(),
                    hashMD5, 0);
            if (result == null) {
                chunk.setStatus(FileUploadStatus.FAILED.name());
            } else {
                chunk.setStatus(FileUploadStatus.COMPLETED.name());
            }
        } else {
            // 对于上传失败的分片，尝试重新上传
            if (Objects.equals(chunk.getStatus(), FileUploadStatus.FAILED.name())) {
                String result = uploadChunk(inputStream, chunkIndex, chunkSize, fileSize, fileId, hashMD5);
                if (result == null) {
                    // 上传失败，增加重试次数
                    chunk.setRetryCount(chunk.getRetryCount() + 1);
                } else {
                    // 上传成功，更新分片状态为完成
                    chunk.setStatus(FileUploadStatus.COMPLETED.name());
                }
            }
        }
        // 返回更新后的分片信息
        return chunk;
    }

    /**
     * 上传文件块到文件系统
     * <p>
     * 本方法负责将一个文件块上传到文件系统中它根据当前块的索引和块大小计算出文件块的起始和结束位置，
     * 然后调用另一个方法将文件块存储到文件系统中这个过程涉及到文件块的定位和处理，确保每个块被正确地放置
     *
     * @param inputStream 文件块的输入流，用于读取文件块数据
     * @param chunkIndex  当前文件块的索引，表示这是第几个块
     * @param chunkSize   每个文件块的大小，用于计算文件块的起始和结束位置
     * @param fileSize    文件的总大小，用于确定最后一个文件块的结束位置
     * @param fileId      文件的唯一标识符，用于在文件系统中标识和存储文件
     * @param hashMD5     文件块的MD5哈希值，用于校验文件块的完整性
     * @return 返回存储结果，通常是文件块在文件系统中的位置或状态
     */
    private String uploadChunk(InputStream inputStream, Integer chunkIndex, Long chunkSize,
                               Long fileSize, String fileId, String hashMD5) {
        // 计算当前文件块的起始位置
        long startLoc = chunkIndex * chunkSize;
        // 计算当前文件块的结束位置，确保不超过文件总大小
        long endLoc = Math.min(startLoc + chunkSize, fileSize);
        // 调用方法将文件块存储到文件系统中
        return storeChunkToFileSystem(fileId, startLoc, endLoc, inputStream, hashMD5);
    }
}
