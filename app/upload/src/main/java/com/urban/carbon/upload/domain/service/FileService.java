package com.urban.carbon.upload.domain.service;

import cn.hutool.core.lang.Assert;
import com.urban.carbon.api.upload.request.UploadChunkRequest;
import com.urban.carbon.api.upload.request.UploadInitRequest;
import com.urban.carbon.api.upload.response.data.UploadChunkInfo;
import com.urban.carbon.api.upload.response.data.UploadInitInfo;
import com.urban.carbon.api.upload.response.data.UploadStatusInfo;
import com.urban.carbon.file.exception.FileErrorCode;
import com.urban.carbon.file.exception.FileException;
import com.urban.carbon.file.strategy.FileStrategy;
import com.urban.carbon.file.strategy.FileStrategyFactory;
import com.urban.carbon.lock.DistributeLock;
import com.urban.carbon.upload.domain.entity.FileUploadTask;
import com.urban.carbon.upload.domain.entity.UploadCache;
import com.urban.carbon.upload.infrastructure.mapper.FileUploadChunkMapper;
import com.urban.carbon.upload.infrastructure.mapper.FileUploadTaskMapper;
import com.urban.carbon.upload.infrastructure.utils.FileHandler;
import com.urban.carbon.upload.infrastructure.utils.FileUploadCacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBitSet;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@Service
public class FileService {

    /**
     * 文件策略工厂
     */
    private final FileStrategyFactory fileStrategyFactory;

    /**
     * 文件缓存工具
     */
    private final FileUploadCacheUtils fileUploadCacheUtils;

    private final FileUploadTaskMapper fileUploadTaskMapper;

    private final FileUploadChunkMapper fileUploadChunkMapper;

    /**
     * 文件处理工具
     */
    private final FileHandler fileHandler;

    /**
     * 构造方法
     *
     * @param fileStrategyFactory 文件策略工厂
     */
    public FileService(FileStrategyFactory fileStrategyFactory, FileUploadCacheUtils fileUploadCacheUtils,
                       FileUploadTaskMapper fileUploadTaskMapper, FileUploadChunkMapper fileUploadChunkMapper,
                       FileHandler fileHandler) {
        this.fileStrategyFactory = fileStrategyFactory;
        this.fileUploadCacheUtils = fileUploadCacheUtils;
        this.fileUploadTaskMapper = fileUploadTaskMapper;
        this.fileUploadChunkMapper = fileUploadChunkMapper;
        this.fileHandler = fileHandler;
    }

    public UploadInitInfo initUpload(UploadInitRequest request) throws IOException {
        // TODO 这里需要插入数据库，用于记录上传信息
        FileUploadTask fileUploadTask = new FileUploadTask();
        fileUploadTask.createTask();
        UploadInitInfo uploadInitInfo = fileHandler.initUpload(request.getFileId(), request.getFileSize(),
                request.getSaveSoft(), request.getDataName(), request.getDataDesc());

    }

    @DistributeLock(scene = "UPLOAD-CHUNK",
            keyExpression = "#request.fileId + ':' + #request.chunkIndex")
    public UploadChunkInfo handleChunkUpload(UploadChunkRequest request) {
        // TODO 需要插入数据库，用于记录上传信息
        return fileHandler.handleChunkUpload(request.getFileId(), request.getChunkIndex(),
                request.getFileInputStream(), request.getHashMD5());
    }

    @DistributeLock(scene = "COMPLETE", keyExpression = "#request.fileId")
    public UploadStatusInfo completeUpload(String fileId) {
        // 考虑与数据库的交互问题
        return fileHandler.completeUpload(fileId);
    }

    public UploadStatusInfo getUploadStatus(String fileId) {
        UploadCache cache = fileUploadCacheUtils.getCacheData(fileId);
        Assert.notNull(cache, () -> new FileException(
                "File upload info not found", FileErrorCode.STATUS_GET_FAILED));
        RBitSet chunkBits = fileUploadCacheUtils.getChunkBits(fileId);
        return buildUploadStatusResponse(chunkBits, fileId, cache);
    }

    @DistributeLock(scene = "CANCEL_FILE", keyExpression = "#request.fileId")
    public UploadStatusInfo cancelUpload(String fileId) {
        // TODO 从文件映射表，chunk表格中的记录设置成CANCELED状态
        return fileHandler.cancelUpload(fileId);
    }

    @DistributeLock(scene = "DELETE_FILE", keyExpression = "#fileId")
    public Boolean deleteUploadFile(String fileId, String saveSoftType, String filePath) {
        // TODO 将文件映射表格中的 deleted 字段设置成 1
        return fileHandler.deleteUploadFile(fileId, saveSoftType, filePath);
    }

    /**
     * 流式下载文件，避免将整个文件写入本地磁盘。
     * 适用于大文件下载，降低服务器内存和磁盘压力。
     * 调用者必须确保输出流在使用完毕后正确关闭。
     *
     * @param fileId       文件唯一标识
     * @param outputStream 输出流，用于写回客户端
     * @throws IOException 下载过程中发生 I/O 异常
     */
    public void streamDownloadFile(String fileId, OutputStream outputStream)
            throws IOException {
        FileStrategy strategy = fileStrategyFactory.getStrategy(saveSoftType.name());
        Assert.notNull(strategy, () -> new IOException(
                "No strategy found for type: " + saveSoftType));
        try {
            strategy.downloadFile(filePath, outputStream);
        } catch (IOException e) {
            log.error("Stream download file {} failed!", fileId, e);
            throw e;
        }
    }
}
