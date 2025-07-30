package com.urban.carbon.api.data.source.request;

import com.urban.carbon.base.request.BaseRequest;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DataSourceModifiedRequest extends BaseRequest {

    /**
     * 数据源名称
     */
    private String dsName;

    /**
     * 数据源描述
     */
    private String dsDesc;

    /**
     * 数据源ID
     */
    private Long id;

}
