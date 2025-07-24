package com.urban.carbon.api.data.source.service;

import com.urban.carbon.api.data.source.request.DataSourceModifiedRequest;
import com.urban.carbon.api.data.source.request.DataSourcePageQueryRequest;
import com.urban.carbon.api.data.source.request.DataSourceQueryRequest;
import com.urban.carbon.api.data.source.response.data.DataSourceInfo;
import com.urban.carbon.base.response.OperateResponse;
import com.urban.carbon.base.response.PageResponse;
import com.urban.carbon.base.response.QueryResponse;

import java.util.List;

public interface DataSourceFacadeService {

    /**
     * 创建数据源
     *
     * @param request 创建数据源参数
     * @return 创建结果
     */
    OperateResponse<DataSourceInfo> createDataSource(DataSourceModifiedRequest request);

    /**
     * 查询数据源
     *
     * @param queryRequest 查询参数
     * @return 查询结果
     */
    QueryResponse<DataSourceInfo> queryDataSource(DataSourceQueryRequest queryRequest);

    /**
     * 修改数据源
     *
     * @param request 修改数据源参数
     * @return 修改结果
     */
    OperateResponse<DataSourceInfo> modifyDataSource(DataSourceModifiedRequest request);

    /**
     * 删除数据源
     *
     * @param request 删除数据源参数
     * @return 删除结果
     */
    OperateResponse<List<Long>> deleteDataSources(DataSourceQueryRequest request);

    /**
     * 分页查询数据源
     *
     * @param request 分页查询参数
     * @return 分页查询结果
     */
    PageResponse<DataSourceInfo> pageQuery(DataSourcePageQueryRequest request);
}
