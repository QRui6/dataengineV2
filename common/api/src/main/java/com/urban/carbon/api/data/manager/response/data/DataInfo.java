package com.urban.carbon.api.data.manager.response.data;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DataInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据源名称
     */
    private String dataSourceName;

    /**
     * 数据名称
     */
    private String dataName;

    /**
     * 数据描述
     */
    private String dataDesc;
}
