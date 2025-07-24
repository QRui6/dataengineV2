package com.urban.carbon.api.data.source.request.condition;

import com.urban.carbon.base.request.QueryCondition;
import lombok.*;

import java.io.Serial;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DataSourceIdQueryCondition implements QueryCondition {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据源id
     */
    private Long dataSourceId;
}
