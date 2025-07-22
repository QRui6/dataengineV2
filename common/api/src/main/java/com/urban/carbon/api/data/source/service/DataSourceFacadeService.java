package com.urban.carbon.api.data.source.service;

import com.urban.carbon.api.data.source.request.DataSourceQueryRequest;
import com.urban.carbon.api.data.source.response.data.DataSourceInfo;
import com.urban.carbon.base.response.QueryResponse;

public interface DataSourceFacadeService {

    /**
     * 查询数据源
     *
     * @param queryRequest 查询参数
     * @return 查询结果
     */
    QueryResponse<DataSourceInfo> queryDataSource(DataSourceQueryRequest queryRequest);

}
