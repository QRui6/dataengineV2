package com.urban.carbon.data.manager.facade;

import cn.hutool.core.lang.Assert;
import com.urban.carbon.api.data.manager.exception.DataErrorCode;
import com.urban.carbon.api.data.manager.exception.DataException;
import com.urban.carbon.api.data.manager.request.DataCreateRequest;
import com.urban.carbon.api.data.manager.response.data.DataInfo;
import com.urban.carbon.api.data.manager.service.DataFacadeService;
import com.urban.carbon.api.data.source.exception.DataSourceErrorCode;
import com.urban.carbon.api.data.source.exception.DataSourceException;
import com.urban.carbon.api.data.source.request.DataSourceQueryRequest;
import com.urban.carbon.api.data.source.response.data.DataSourceInfo;
import com.urban.carbon.api.data.source.service.DataSourceFacadeService;
import com.urban.carbon.base.response.OperateResponse;
import com.urban.carbon.data.manager.domain.entity.Data;
import com.urban.carbon.data.manager.domain.entity.DataConvertor;
import com.urban.carbon.data.manager.domain.service.DataService;
import com.urban.carbon.data.manager.domain.service.FileService;
import com.urban.carbon.rpc.facade.Facade;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(version = "1.0.0")
public class DataFacadeServiceImpl implements DataFacadeService {

    /**
     * 数据服务
     */
    private final DataService dataService;

    private final FileService fileService;

    /**
     * 数据源服务
     */
    private final DataSourceFacadeService dataSourceFacadeService;

    /**
     *
     * @param dataService 数据服务
     * @param dataSourceFacadeService 数据源服务
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
        // 调用 dataService 中提供的添加数据方法，创建数据 ( 调用内部方法 )
        Data data = dataService.initCreate(request, dataSourceInfo);
        Assert.notNull(data, () -> new DataException(
                DataErrorCode.DATA_CREATE_ERROR));
        // 封装操作结果
        OperateResponse<DataInfo> response = new OperateResponse<>();
        response.setSuccess(true);
        response.setData(DataConvertor.INSTANCE.mapToVo(data));
        return response;
    }
}
