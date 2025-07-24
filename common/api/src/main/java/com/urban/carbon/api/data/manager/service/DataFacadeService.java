package com.urban.carbon.api.data.manager.service;

import com.urban.carbon.api.data.manager.request.DataCreateRequest;
import com.urban.carbon.api.data.manager.request.MergeRequest;
import com.urban.carbon.api.data.manager.request.UploadChunkRequest;
import com.urban.carbon.api.data.manager.response.data.DataInfo;
import com.urban.carbon.api.data.manager.response.data.UploadChunkInfo;
import com.urban.carbon.api.data.manager.response.data.UploadStatusInfo;
import com.urban.carbon.base.response.OperateResponse;
import com.urban.carbon.base.response.QueryResponse;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.io.OutputStream;

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

    /**
     * 下载文件
     *
     * @param filePath 文件路径
     * @param saveSoft 保存软件
     * @param outputStream 输出流
     */
    void downloadFile(String filePath, String saveSoft, OutputStream outputStream) throws IOException;

    /**
     * 查询数据
     *
     * @param dataId 数据ID
     * @param loginId 登录ID
     * @return 查询结果
     */
    QueryResponse<DataInfo> findById(@NotNull Long dataId, Long loginId);

    /**
     * 判断数据源下是否存在数据
     *
     * @param dsId 数据源ID
     * @return 存在返回1，不存在返回0
     */
    int existsData(Long dsId);
}
