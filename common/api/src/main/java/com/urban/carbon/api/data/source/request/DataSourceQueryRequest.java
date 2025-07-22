package com.urban.carbon.api.data.source.request;

import com.urban.carbon.base.request.BaseRequest;
import com.urban.carbon.base.request.QueryCondition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataSourceQueryRequest extends BaseRequest {

    private QueryCondition condition;

    /**
     * 构造方法
     *
     * @param dataSourceId 数据源ID
     */
    public DataSourceQueryRequest(Long dataSourceId) {
        this.condition = new DataSourceIdQueryCondition(dataSourceId);
    }

    /**
     * 构造方法
     *
     * @param dataSourceName 数据源名称
     */
    public DataSourceQueryRequest(String dataSourceName) {
        this.condition = new DataSourceNameQueryCondition(dataSourceName);
    }
}
