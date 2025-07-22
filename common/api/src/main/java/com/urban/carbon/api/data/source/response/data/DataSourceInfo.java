package com.urban.carbon.api.data.source.response.data;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据源id
     */
    private Long id;

    /**
     * 数据源名称
     */
    private String dsName;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 数据源描述
     */
    private String dsDesc;
}
