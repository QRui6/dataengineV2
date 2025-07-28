package com.urban.carbon.api.geoservice.response.data;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
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
     *
     */
    private String baseUrl;

    /**
     * 创建工作空间
     *
     * @param workspaceName 工作空间名称
     * @param storeName 存储名称
     * @param layerName 图层名称
     * @param allowFormatTypes 允许的格式
     * @param SRS 地理坐标系
     * @param projection 投影坐标系
     * @param baseUrl 基础URL
     */
    public void showInfo(String workspaceName, String storeName, String layerName,
                         List<String> allowFormatTypes, Integer SRS,
                         Integer projection, String baseUrl) {
        this.setWorkspaceName(workspaceName);
        this.setStoreName(storeName);
        this.setLayerName(layerName);
        this.setAllowFormatTypes(allowFormatTypes);
        this.setSRS("EPSG:" + SRS);
        this.setProjection("EPSG:" + projection);
        this.setBaseUrl(baseUrl);
    }
}
