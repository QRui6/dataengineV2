package com.urban.carbon.api.geoservice.response.data;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GeoServiceInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 服务ID
     */
    private Long id;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务描述
     */
    private String serviceDesc;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 工作空间名称
     */
    private String workspaceName;

    /**
     * 存储名称
     */
    private String storeName;

    /**
     * 图层名称
     */
    private String layerName;

    /**
     * 服务状态
     */
    private Integer started;

    /**
     * 服务基础URL
     */
    private String serviceBaseURL;

    /**
     * 允许的格式
     */
    private List<String> allowFormatTypes;

    /**
     * 地理坐标系
     */
    private String SRS;

    /**
     * 投影坐标系
     */
    private String projection;

    /**
     * 服务ID
     */
    private String serviceUrl;
}
