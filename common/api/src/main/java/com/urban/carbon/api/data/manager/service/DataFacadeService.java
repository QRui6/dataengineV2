package com.urban.carbon.api.data.manager.service;

import com.urban.carbon.api.data.manager.request.DataCreateRequest;
import com.urban.carbon.api.data.manager.request.MergeRequest;
import com.urban.carbon.api.data.manager.request.UploadChunkRequest;
import com.urban.carbon.api.data.manager.response.data.DataInfo;
import com.urban.carbon.api.data.manager.response.data.UploadChunkInfo;
import com.urban.carbon.api.data.manager.response.data.UploadStatusInfo;
import com.urban.carbon.base.response.OperateResponse;
import com.urban.carbon.base.response.QueryResponse;

public interface DataFacadeService {

    /**
     * 创建数据
     *
     * @param request 创建数据请求
     * @return 创建数据结果
     */
    OperateResponse<DataInfo> initCreateData(DataCreateRequest request);

    /**
     * 上传分片
     *
     * @param request 上传分片请求
     * @return 上传分片结果
     */
    OperateResponse<UploadChunkInfo> uploadChunk(UploadChunkRequest request);

    /**
     * 合并分片
     *
     * @param request 合并分片请求
     * @return 合并分片结果
     */
    OperateResponse<UploadStatusInfo> mergeChunks(MergeRequest request);

    /**
     * 获取上传状态
     *
     * @param request 获取上传状态请求
     * @return 获取上传状态结果
     */
    QueryResponse<UploadStatusInfo> getUploadStatus(MergeRequest request);

    /**
     * 取消上传
     *
     * @param request 取消上传请求
     * @return 取消上传结果
     */
    OperateResponse<DataInfo> cancelUpload(MergeRequest request);
}
