package com.urban.carbon.api.data.source.request.condition;

import com.urban.carbon.base.request.QueryCondition;
import lombok.*;

import java.io.Serial;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceIdsQueryCondition implements QueryCondition {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据源ID列表
     */
    private List<Long> dataSourceIds;
}
