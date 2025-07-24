package com.urban.carbon.data.source.facade;

import cn.hutool.core.lang.Assert;
import com.urban.carbon.api.data.source.exception.DataSourceErrorCode;
import com.urban.carbon.api.data.source.exception.DataSourceException;
import com.urban.carbon.api.data.source.request.DataSourcePageQueryRequest;
import com.urban.carbon.api.data.source.request.condition.DataSourceIdQueryCondition;
import com.urban.carbon.api.data.source.request.DataSourceModifiedRequest;
import com.urban.carbon.api.data.source.request.condition.DataSourceIdsQueryCondition;
import com.urban.carbon.api.data.source.request.condition.DataSourceNameQueryCondition;
import com.urban.carbon.api.data.source.request.DataSourceQueryRequest;
import com.urban.carbon.api.data.source.response.data.DataSourceInfo;
import com.urban.carbon.api.data.source.service.DataSourceFacadeService;
import com.urban.carbon.base.response.OperateResponse;
import com.urban.carbon.base.response.PageResponse;
import com.urban.carbon.base.response.QueryResponse;
import com.urban.carbon.data.source.domain.entity.DataSource;
import com.urban.carbon.data.source.domain.entity.DataSourceConvertor;
import com.urban.carbon.data.source.domain.service.DataSourceService;
import com.urban.carbon.rpc.facade.Facade;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

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
    @Facade
    public OperateResponse<DataSourceInfo> createDataSource(DataSourceModifiedRequest request) {
        return dataSourceService.createDataSource(request.getDsName(), request.getDsName(),
                request.getLoginId());
    }

    @Override
    public QueryResponse<DataSourceInfo> queryDataSource(DataSourceQueryRequest queryRequest) {
        if (!(queryRequest.getCondition() instanceof DataSourceIdQueryCondition)) {
            // 不支持的查询条件类型，抛出异常
            throw new DataSourceException(DataSourceErrorCode.QUERY_CONDITION_NOT_SUPPORT);
        }
        // 根据数据源ID查询条件进行查询处理
        DataSource dataSource = dataSourceService.findById(
                ((DataSourceIdQueryCondition) queryRequest.getCondition()).getDataSourceId(),
                queryRequest.getLoginId());
        Assert.notNull(dataSource, () -> new DataSourceException(
                DataSourceErrorCode.DATA_SOURCE_NOT_EXIST));
        QueryResponse<DataSourceInfo> response = new QueryResponse<>();
        response.setSuccess(true);
        response.setData(DataSourceConvertor.INSTANCE.mapToVo(dataSource));
        return response;
    }

    @Override
    @Facade
    public OperateResponse<DataSourceInfo> modifyDataSource(DataSourceModifiedRequest request) {
        return dataSourceService.modifyDataSource(request.getId(), request.getDsName(),
                request.getDsDesc(), request.getLoginId());
    }

    @Override
    public OperateResponse<List<Long>> deleteDataSources(DataSourceQueryRequest request) {
        if (!(request.getCondition() instanceof DataSourceIdsQueryCondition)) {
            throw new DataSourceException(DataSourceErrorCode.QUERY_CONDITION_NOT_SUPPORT);
        }
        return dataSourceService.deleteDataSourceByIds(
                ((DataSourceIdsQueryCondition) request.getCondition()).getDataSourceIds(), request.getLoginId());
    }

    @Override
    public PageResponse<DataSourceInfo> pageQuery(DataSourcePageQueryRequest request) {
        return switch (request.getCondition()) {
            case null -> dataSourceService.pageQuery(request.getCurrentPage(), request.getPageSize(),
                    request.getLoginId());
            case DataSourceNameQueryCondition dsNameQueryCondition -> dataSourceService.pageQueryByName(
                    request.getCurrentPage(), request.getPageSize(),
                    dsNameQueryCondition.getDataSourceName(),
                    request.getLoginId());
            default -> throw new DataSourceException(DataSourceErrorCode.QUERY_CONDITION_NOT_SUPPORT);
        };
    }
}
