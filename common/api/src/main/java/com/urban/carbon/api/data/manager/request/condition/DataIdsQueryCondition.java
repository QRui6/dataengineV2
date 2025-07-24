package com.urban.carbon.api.data.manager.request.condition;

import com.urban.carbon.base.request.QueryCondition;
import lombok.*;

import java.io.Serial;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DataIdsQueryCondition implements QueryCondition {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据源IDs
     */
    private List<Long> ids;

}
