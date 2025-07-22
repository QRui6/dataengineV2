package com.urban.carbon.api.data.source.request;

import com.urban.carbon.base.request.QueryCondition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceNameQueryCondition implements QueryCondition {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据源名称
     */
    private String dataSourceName;
}
