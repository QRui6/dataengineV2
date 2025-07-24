package com.urban.carbon.api.data.source.request;

import com.urban.carbon.api.data.source.request.condition.DataSourceNameQueryCondition;
import com.urban.carbon.base.request.PageRequest;
import com.urban.carbon.base.request.QueryCondition;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class DataSourcePageQueryRequest extends PageRequest {

    /**
     * 查询条件
     */
    private QueryCondition condition;

    /**
     * 构造方法 —— 带参数的构造方法
     *
     * @param page 页码
     * @param pageSize 每页大小
     * @param dsName 数据源名称
     */
    public DataSourcePageQueryRequest(Integer page, Integer pageSize, String dsName, Long loginId) {
        super(page, pageSize);
        this.condition = new DataSourceNameQueryCondition(dsName);
        this.setLoginId(loginId);
    }

    /**
     * 构造方法 —— 不指定查询条件
     *
     * @param page 页码
     * @param pageSize 每页大小
     */
    public DataSourcePageQueryRequest(Integer page, Integer pageSize, Long loginId) {
        super(page, pageSize);
        this.condition = null;
        this.setLoginId(loginId);
    }
}
