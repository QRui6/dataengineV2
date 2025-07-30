package com.urban.carbon.data.source.facade;

import cn.hutool.core.lang.Assert;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.urban.carbon.api.data.manager.service.DataFacadeService;
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
import com.urban.carbon.data.source.domain.entity.convertor.DataSourceConvertor;
import com.urban.carbon.data.source.domain.service.DataSourceService;
import com.urban.carbon.rpc.facade.Facade;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;
import java.util.concurrent.*;

@DubboService(version = "1.0.0")
public class DataSourceFacadeServiceImpl implements DataSourceFacadeService {

    /**
     * 数据源服务
     */
    private final DataSourceService dataSourceService;

    /**
     * 数据服务
     */
    private final DataFacadeService dataFacadeService;

    /**
     * 构造函数
     *
     * @param dataSourceService 数据源服务
     * @param dataFacadeService 数据服务
     */
    public DataSourceFacadeServiceImpl(DataSourceService dataSourceService,
                                       DataFacadeService dataFacadeService) {
        this.dataSourceService = dataSourceService;
        this.dataFacadeService = dataFacadeService;
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
        if (!(request.getCondition() instanceof DataSourceIdsQueryCondition condition)) {
            throw new DataSourceException(DataSourceErrorCode.QUERY_CONDITION_NOT_SUPPORT);
        }
        List<DataSource> dsList = dataSourceService.findByIds(
                condition.getDataSourceIds(), request.getLoginId());
        List<DataSource> dataSources = getDataSources(condition.getDataSourceIds(), dsList);
        // 写删除数据源的操作记录
        return dataSourceService.getListOperateResponse(request.getLoginId(), dataSources);
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

    /**
     * 根据数据源ID列表获取可以成功删除的数据源列表
     *
     * @param dataSourceIds 数据源ID列表，用于判断是否需要使用线程池处理
     * @param dsList        待处理的数据源列表
     * @return 可以成功删除的数据源列表
     */
    private List<DataSource> getDataSources(List<Long> dataSourceIds, List<DataSource> dsList) {
        List<DataSource> dsSuccess;
        if (dataSourceIds.size() > 5) {
            // 创建自定义线程池
            ThreadFactory namedThreadFactory = (new ThreadFactoryBuilder())
                    .setNameFormat("delete-data-%d").build();
            // 创建线程池并执行并行处理
            try (ExecutorService pool = new ThreadPoolExecutor(
                    5, 5, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(), namedThreadFactory)) {
                // 过滤出可以删除的数据源：数据不存在且删除成功的数据源
                dsSuccess = dsList.stream()
                        .filter(ds -> {
                            try {
                                return pool.submit(() -> dataFacadeService.existsData(ds.getId()) == 0 &&
                                        dataSourceService.removeById(ds)).get();
                            } catch (InterruptedException | ExecutionException e) {
                                throw new RuntimeException(e);
                            }
                        }).toList();
            }
        } else {
            // 数据源数量小于等于5时，使用普通串行方式处理
            dsSuccess = dsList.stream()
                    .filter(ds -> dataFacadeService.existsData(ds.getId()) == 0 &&
                            dataSourceService.removeById(ds))
                    .toList();
        }
        return dsSuccess;
    }
}
