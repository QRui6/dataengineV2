package com.urban.carbon.data.source.facade;

import com.urban.carbon.api.data.source.request.DataSourceIdQueryCondition;
import com.urban.carbon.api.data.source.request.DataSourceNameQueryCondition;
import com.urban.carbon.api.data.source.request.DataSourceQueryRequest;
import com.urban.carbon.api.data.source.response.data.DataSourceInfo;
import com.urban.carbon.api.data.source.service.DataSourceFacadeService;
import com.urban.carbon.base.response.QueryResponse;
import com.urban.carbon.data.source.domain.entity.DataSource;
import com.urban.carbon.data.source.domain.entity.DataSourceConvertor;
import com.urban.carbon.data.source.domain.service.DataSourceService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(version = "1.0.0")
public class DataSourceFacadeServiceImpl implements DataSourceFacadeService {

    /**
     * 数据源服务
     */
    private final DataSourceService dataSourceService;

    public DataSourceFacadeServiceImpl(DataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }

    @Override
    public QueryResponse<DataSourceInfo> queryDataSource(DataSourceQueryRequest request) {
        DataSource dataSource = switch (request.getCondition()) {
            case DataSourceIdQueryCondition condition:
                yield dataSourceService.findById(
                        condition.getDataSourceId(), request.getLoginId());
            case DataSourceNameQueryCondition condition:
                yield dataSourceService.findByName(
                        condition.getDataSourceName(), request.getLoginId());
            default:
                throw new IllegalStateException(request.getCondition() + "'' is not supported");
        };
        QueryResponse<DataSourceInfo> response = new QueryResponse<>();
        response.setSuccess(true);
        response.setData(DataSourceConvertor.INSTANCE.mapToVo(dataSource));
        return response;
    }
}
