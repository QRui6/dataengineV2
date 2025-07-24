package com.urban.carbon.api.data.manager.request;

import com.urban.carbon.api.data.manager.request.condition.DataIdQueryCondition;
import com.urban.carbon.api.data.manager.request.condition.DataIdsQueryCondition;
import com.urban.carbon.base.request.BaseRequest;
import com.urban.carbon.base.request.QueryCondition;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DataQueryRequest extends BaseRequest {

    private QueryCondition condition;

    public DataQueryRequest(List<Long> ids) {
        this.condition = new DataIdsQueryCondition(ids);
    }

    public DataQueryRequest(Long id) {
        this.condition = new DataIdQueryCondition(id);
    }
}
