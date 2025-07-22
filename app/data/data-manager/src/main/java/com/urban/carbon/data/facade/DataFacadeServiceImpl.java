package com.urban.carbon.data.facade;

import com.urban.carbon.api.data.manager.request.DataCreateRequest;
import com.urban.carbon.api.data.manager.response.data.DataInfo;
import com.urban.carbon.api.data.manager.service.DataFacadeService;
import com.urban.carbon.base.response.OperateResponse;
import com.urban.carbon.data.domain.service.DataService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(version = "1.0.0")
public class DataFacadeServiceImpl implements DataFacadeService {

    private final DataService dataService;

    public DataFacadeServiceImpl(DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public OperateResponse<DataInfo> createData(DataCreateRequest request) {
        // 调用 dataSource 来查询用户指定的数据源是否存在

        // 调用 dataService 中提供的添加数据方法，创建数据

        // 封装操作结果

        return null;
    }
}
