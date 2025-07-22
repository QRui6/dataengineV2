package com.urban.carbon.data.source.facade;

import cn.hutool.core.lang.Assert;
import com.urban.carbon.api.data.source.exception.DataSourceErrorCode;
import com.urban.carbon.api.data.source.exception.DataSourceException;
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
    public QueryResponse<DataSourceInfo> queryDataSource(DataSourceQueryRequest queryRequest) {
        DataSource dataSource = switch (queryRequest.getCondition()) {
            case DataSourceIdQueryCondition condition:
                yield dataSourceService.findById(
                        condition.getDataSourceId(), queryRequest.getLoginId());
            case DataSourceNameQueryCondition condition:
                yield dataSourceService.findByName(
                        condition.getDataSourceName(), queryRequest.getLoginId());
            default:
                throw new IllegalStateException(queryRequest.getCondition() + "'' is not supported");
        };
        Assert.notNull(dataSource, () -> new DataSourceException(
                DataSourceErrorCode.DATA_SOURCE_NOT_EXIST));
        QueryResponse<DataSourceInfo> response = new QueryResponse<>();
        response.setSuccess(true);
        response.setData(DataSourceConvertor.INSTANCE.mapToVo(dataSource));
        return response;
    }
}
