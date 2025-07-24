package com.urban.carbon.api.data.manager.request;

import com.urban.carbon.api.data.manager.request.condition.DataDSIdQueryCondition;
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
public class DataPageQueryRequest extends PageRequest {

    /**
     * 查询条件
     */
    private QueryCondition condition;

    /**
     * 通过 数据源 ID 与 数据 ID 进行查询
     *
     * @param dsId  数据源ID
     * @param page  页码
     * @param pageSize 每页数量
     * @param loginId 登录用户ID
     */
    public DataPageQueryRequest(Long dsId, Integer page, Integer pageSize, Long loginId) {
        super(page, pageSize);
        this.condition = new DataDSIdQueryCondition(dsId);
        this.setLoginId(loginId);
    }
}
