package com.urban.carbon.api.upload.service;

import com.urban.carbon.api.upload.request.UploadChunkRequest;
import com.urban.carbon.api.upload.request.UploadInitRequest;
import com.urban.carbon.api.upload.response.data.UploadChunkInfo;
import com.urban.carbon.api.upload.response.data.UploadInitInfo;
import com.urban.carbon.api.upload.response.data.UploadStatusInfo;

import java.io.IOException;

public interface FileFacadeService {

    /**
     * 初始化上传
     *
     * @param request 初始化上传参数
     * @return 初始化上传结果
     */
    UploadInitInfo initUpload(UploadInitRequest request);

    /**
     * 上传分片
     *
     * @param request 上传分片参数
     * @return 上传分片结果
     */
    UploadChunkInfo handleChunkUpload(UploadChunkRequest request);

    /**
     * 完成上传
     *
     * @param fileId 文件ID
     * @return 完成上传结果
     */
    UploadStatusInfo completeUpload(String fileId);

    /**
     * 获取上传状态
     *
     * @param fileId 文件ID
     * @return 上传状态
     */
    UploadStatusInfo getUploadStatus(String fileId);

    /**
     * 取消上传
     *
     * @param fileId 文件ID
     * @return 取消上传结果
     */
    UploadStatusInfo cancelUpload(String fileId);

    /**
     * 删除上传文件
     *
     * @param fileId 文件ID
     * @return 删除上传文件结果
     */
    Boolean deleteUploadFile(String fileId, String saveSoftType, String filePath);
}
