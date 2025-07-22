package com.urban.carbon.data.manager.domain.service;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.urban.carbon.api.data.manager.constants.DataOperateType;
import com.urban.carbon.api.data.manager.exception.DataErrorCode;
import com.urban.carbon.api.data.manager.exception.DataException;
import com.urban.carbon.api.data.manager.request.DataCreateRequest;
import com.urban.carbon.api.data.manager.request.UploadInitRequest;
import com.urban.carbon.api.data.manager.response.data.UploadInitInfo;
import com.urban.carbon.api.data.source.response.data.DataSourceInfo;
import com.urban.carbon.base.utils.RandomNameGenerator;
import com.urban.carbon.data.manager.domain.entity.Data;
import com.urban.carbon.data.manager.infrastructure.mapper.DataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DataService extends ServiceImpl<DataMapper, Data> {

    private final FileService fileService;

    private final DataOperateStreamService dataOperateStreamService;

    public DataService(FileService fileService, DataOperateStreamService dataOperateStreamService) {
        this.dataOperateStreamService = dataOperateStreamService;
        this.fileService = fileService;
    }

    /**
     * 创建数据
     *
     * @param request 创建数据请求
     * @param dataSourceInfo 数据源信息
     * @return 创建的数据
     */
    public Data initCreate(DataCreateRequest request, DataSourceInfo dataSourceInfo) {
        String fileId = RandomNameGenerator.generateRandomFileName(32, request.getDataType());
        // 初始化上传，调用 fileService 初始化方法 ( 调用内部方法 )
        UploadInitRequest initRequest = new UploadInitRequest();
        initRequest.createRequest(fileId, request.getFileSize(), request.getDataName(),
                request.getDataType(), request.getDataDesc());
        UploadInitInfo uploadInitInfo = fileService.initUpload(initRequest);
        Assert.notNull(uploadInitInfo, () -> new DataException(DataErrorCode.DATA_CREATE_ERROR));
        // 创建数据实体类
        Data data = new Data();
        data.initCreate(fileId, request.getLoginId(), dataSourceInfo.getId(), request.getDataName(),
                request.getDataDesc(), request.getDataType(), uploadInitInfo.getChunkSize(),
                uploadInitInfo.getTotalChunks(), uploadInitInfo.getSaveSoft(), uploadInitInfo.getStatus(),
                dataSourceInfo.getDsName());
        // 插入记录
        if (this.save(data)) {
            // 插入操作记录
            Long insertStream = dataOperateStreamService.insertStream(
                    data, request.getLoginId(), DataOperateType.CREATE);
            Assert.notNull(insertStream, () -> new DataException(DataErrorCode.DATA_OPERATE_STREAM_FAIL));
            return data;
        } else {
            log.error("Data Create Failed!");
            return null;
        }
    }

}
