package com.urban.carbon.geoserver.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * GeoServer 配置
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = GeoServerProperties.PREFIX)
public class GeoServerProperties {

    /**
     * 前缀
     */
    public static final String PREFIX = "spring.geo-server";

    /**
     * 是否启用
     */
    private String enabled;

    /**
     * GeoServer 基础 URL 地址
     */
    private String baseUrl;

    /**
     * 登录 GeoServer 的用户名
     */
    private String userName;

    /**
     * 登录 GeoServer 的密码
     */
    private String password;

    /**
     * GeoServer 中的工作空间名称
     */
    private String workspace;

    /**
     * 数据存储的基准名称
     */
    private String BaseStoreName;

    /**
     * WMS 服务的 URL 地址
     */
    private String wmsServiceBaseUrl;

    /**
     * REST 服务的 URL 地址
     */
    private String restServiceBaseUrl;

    /**
     * GeoServer 的地理坐标系
     */
    private String geoCode;

    /**
     * GeoServer 的投影坐标系
     */
    private String geoProjection;

}

