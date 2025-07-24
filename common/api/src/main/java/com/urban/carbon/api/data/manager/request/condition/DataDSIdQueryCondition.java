package com.urban.carbon.api.data.manager.request.condition;

import com.urban.carbon.base.request.QueryCondition;
import lombok.*;

import java.io.Serial;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DataDSIdQueryCondition implements QueryCondition {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据源 ID
     */
    private Long dsId;
}
