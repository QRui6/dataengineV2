package com.urban.carbon.data.manager.facade;

import cn.hutool.core.lang.Assert;
import com.urban.carbon.api.data.manager.constants.FileUploadStatus;
import com.urban.carbon.api.data.manager.exception.DataErrorCode;
import com.urban.carbon.api.data.manager.exception.DataException;
import com.urban.carbon.api.data.manager.request.DataCreateRequest;
import com.urban.carbon.api.data.manager.request.MergeRequest;
import com.urban.carbon.api.data.manager.request.UploadChunkRequest;
import com.urban.carbon.api.data.manager.request.UploadInitRequest;
import com.urban.carbon.api.data.manager.response.data.DataInfo;
import com.urban.carbon.api.data.manager.response.data.UploadChunkInfo;
import com.urban.carbon.api.data.manager.response.data.UploadInitInfo;
import com.urban.carbon.api.data.manager.response.data.UploadStatusInfo;
import com.urban.carbon.api.data.manager.service.DataFacadeService;
import com.urban.carbon.api.data.source.request.DataSourceQueryRequest;
import com.urban.carbon.api.data.source.response.data.DataSourceInfo;
import com.urban.carbon.api.data.source.service.DataSourceFacadeService;
import com.urban.carbon.base.response.OperateResponse;
import com.urban.carbon.base.response.QueryResponse;
import com.urban.carbon.base.utils.RandomNameGenerator;
import com.urban.carbon.data.manager.domain.entity.Data;
import com.urban.carbon.data.manager.domain.entity.DataConvertor;
import com.urban.carbon.data.manager.domain.service.DataService;
import com.urban.carbon.data.manager.domain.service.FileService;
import com.urban.carbon.rpc.facade.Facade;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStream;

@DubboService(version = "1.0.0")
public class DataFacadeServiceImpl implements DataFacadeService {

    /**
     * 数据服务
     */
    private final DataService dataService;

    /**
     * 文件服务
     */
    private final FileService fileService;

    /**
     * 数据源服务
     */
    private final DataSourceFacadeService dataSourceFacadeService;

    /**
     * 构造方法
     *
     * @param dataSourceFacadeService 数据源服务
     * @param dataService             数据服务
     * @param fileService             文件服务
     */
    public DataFacadeServiceImpl(DataService dataService, FileService fileService,
                                 DataSourceFacadeService dataSourceFacadeService) {
        this.dataService = dataService;
        this.fileService = fileService;
        this.dataSourceFacadeService = dataSourceFacadeService;
    }

    @Override
    @Facade
    public OperateResponse<DataInfo> initCreateData(DataCreateRequest request) {
        // 调用 dataSource 来查询用户指定的数据源是否存在 ( RPC 远程调用 )
        DataSourceQueryRequest queryRequest = new DataSourceQueryRequest(request.getDataSourceId(), request.getLoginId());
        DataSourceInfo dataSourceInfo = dataSourceFacadeService.queryDataSource(queryRequest).getData();
        String fileId = RandomNameGenerator.generateRandomFileName(16, request.getDataType());
        // 初始化上传，调用 fileService 初始化方法
        UploadInitRequest initRequest = new UploadInitRequest();
        initRequest.createRequest(fileId, request.getFileSize(), request.getDataName(),
                request.getDataType(), request.getDataDesc());
        UploadInitInfo uploadInitInfo = fileService.initUpload(initRequest);
        Assert.notNull(uploadInitInfo, () -> new DataException(DataErrorCode.DATA_CREATE_ERROR));
        // 调用 dataService 中提供的添加数据方法，创建数据 ( 调用内部方法 )
        Data data = dataService.initCreate(
                request.getDataName(), request.getDataDesc(), request.getDataType(),
                request.getFileSize(), request.getDataSourceId(), request.getLoginId(),
                dataSourceInfo.getDsName(), uploadInitInfo);
        Assert.notNull(data, () -> new DataException(
                DataErrorCode.DATA_CREATE_ERROR));
        // 封装操作结果
        OperateResponse<DataInfo> response = new OperateResponse<>();
        response.setSuccess(true);
        response.setData(DataConvertor.INSTANCE.mapToVo(data));
        return response;
    }

    @Override
    @Facade
    @Transactional(rollbackFor = Exception.class)
    public OperateResponse<UploadChunkInfo> uploadChunk(UploadChunkRequest request) {
        Data data = dataService.findByFileId(request.getFileId());
        Assert.notNull(data, () -> new DataException(DataErrorCode.DATA_NOT_FOUND));
        Assert.isTrue(data.getStatus().equals(FileUploadStatus.INITIALIZED.name()),
                () -> new DataException(DataErrorCode.DATA_ALREADY_UPLOADED));
        request.setChunkSize(data.getChunkSize());
        request.setFileSize(data.getFileSize());
        request.setTotalChunks(data.getTotalChunks());
        OperateResponse<UploadChunkInfo> response = new OperateResponse<>();
        UploadChunkInfo chunkInfo = fileService.handleChunkUpload(request);
        response.setSuccess(true);
        response.setData(chunkInfo);
        return response;
    }

    @Override
    @Facade
    @Transactional(rollbackFor = Exception.class)
    public OperateResponse<UploadStatusInfo> mergeChunks(MergeRequest request) {
        String uploadId = request.getUploadId();
        // 查询是否存在该数据
        Data data = dataService.findByFileId(uploadId);
        Assert.notNull(data, () -> new DataException(DataErrorCode.DATA_NOT_FOUND));
        OperateResponse<UploadStatusInfo> response = new OperateResponse<>();
        UploadStatusInfo statusInfo = fileService.mergeChunks(uploadId, data);
        if (dataService.uploadDataStatus(statusInfo.getFilePath(), data, request.getLoginId())) {
            response.setSuccess(true);
            response.setData(statusInfo);
        } else {
            response.setSuccess(false);
        }
        return response;
    }

    @Override
    public QueryResponse<UploadStatusInfo> getUploadStatus(MergeRequest request) {
        String uploadId = request.getUploadId();
        // 查询是否存在该数据
        Data data = dataService.findByFileId(uploadId);
        Assert.notNull(data, () -> new DataException(DataErrorCode.DATA_NOT_FOUND));
        QueryResponse<UploadStatusInfo> response = new QueryResponse<>();
        // 获取文件上传状态
        UploadStatusInfo statusInfo = fileService.getUploadStatus(request.getUploadId(), data);
        // 设置响应值
        response.setSuccess(true);
        response.setData(statusInfo);
        return response;
    }

    @Override
    @Facade
    @Transactional(rollbackFor = Exception.class)
    public OperateResponse<DataInfo> cancelUpload(MergeRequest request) {
        String uploadId = request.getUploadId();
        // 查询是否存在该数据
        Data data = dataService.findByFileId(uploadId);
        Assert.notNull(data, () -> new DataException(DataErrorCode.DATA_NOT_FOUND));
        OperateResponse<DataInfo> response = new OperateResponse<>();
        // 首先，将 data 中的信息进行修改，将 status 修改为 CANCELED，阻止之后的访问继续上传接口
        // 随后，需要使用 fileService 的服务，清除所有 chunk，同时删除临时目录
        if (dataService.cancelUploadData(data, request.getLoginId()) &&
                fileService.cancelUpload(uploadId)) {
            response.setSuccess(true);
            response.setData(DataConvertor.INSTANCE.mapToVo(data));
        } else {
            response.setSuccess(false);
        }
        return response;
    }

    @Override
    public void downloadFile(String filePath, String saveSoft, OutputStream outputStream) throws IOException {
        fileService.downloadFile(filePath, saveSoft, outputStream);
    }

    @Override
    public QueryResponse<DataInfo> findById(Long dataId, Long loginId) {
        Data data = dataService.findById(dataId, loginId);
        Assert.notNull(data, () -> new DataException(DataErrorCode.DATA_NOT_FOUND));
        QueryResponse<DataInfo> response = new QueryResponse<>();
        response.setSuccess(true);
        response.setData(DataConvertor.INSTANCE.mapToVo(data));
        return response;
    }

    @Override
    public int existsData(Long dsId) {
        // TODO 实现查询是否存在数据的方法
        return 0;
    }

}
