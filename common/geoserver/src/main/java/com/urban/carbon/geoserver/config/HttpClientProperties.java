package com.urban.carbon.geoserver.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Http 客户端配置
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@ConfigurationProperties(prefix = HttpClientProperties.PREFIX)
public class HttpClientProperties {

    /**
     * 前缀
     */
    public static final String PREFIX = "spring.http-pool";

    /**
     * 单一路由（IP或主机）允许的最大并发连接数，默认值为 2。
     */
    private int defaultMaxPerRoute = 2;

    /**
     * HTTP客户端总的最大连接数，默认值为 20。
     */
    private int maxTotal = 20;

    /**
     * 连接空闲后验证连接有效性的间隔时间（毫秒），默认值为 2000ms。
     */
    private long validateAfterInactivity = 2000;

    /**
     * 建立连接的最大超时时间（毫秒），默认值为 2000ms。
     */
    private long connectTimeout = 2000;

    /**
     * 从连接池获取连接的最大等待时间（毫秒），默认值为 20000ms。
     */
    private long connectionRequestTimeout = 20000;

    /**
     * 数据传输的最大超时时间（毫秒），默认值为 20000ms。
     */
    private long socketTimeout = 20000;

    /**
     * 响应超时时间（毫秒），默认值为 30000ms。
     */
    private long responseTimeout = 30000;

}

