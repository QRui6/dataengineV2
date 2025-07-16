package com.urban.carbon.geoserver.config;

import com.urban.carbon.geoserver.GeoServerHttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * GeoServer 配置加载文件
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({GeoServerProperties.class, HttpClientProperties.class})
public class GeoServerConfiguration {

    /**
     * HttpClient 配置属性
     */
    private final HttpClientProperties httpClientProperties;

    /**
     * GeoServer 配置属性
     */
    private final GeoServerProperties geoServerProperties;

    /**
     * 构造函数，初始化 HttpClient 配置属性和 GeoServer 配置属性
     *
     * @param httpClientProperties HttpClient 配置属性
     * @param geoServerProperties  GeoServer 配置属性
     */
    public GeoServerConfiguration(HttpClientProperties httpClientProperties,
                                  GeoServerProperties geoServerProperties) {
        this.httpClientProperties = httpClientProperties;
        this.geoServerProperties = geoServerProperties;
    }

    /**
     * 显式修改 HttpClient 连接池参数，注意：如果未显式设置，应该有默认配置
     */
    @Bean
    @ConditionalOnMissingBean
    @Profile({"default"})
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        // 创建连接池管理对象，用于管理 HTTP 连接池的参数和行为
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        // 设置每个路由（host:port）的最大连接数，默认值由 httpClientProperties 提供
        manager.setDefaultMaxPerRoute(httpClientProperties.getDefaultMaxPerRoute());
        // 设置整个连接池的最大连接总数，默认值由 httpClientProperties 提供
        manager.setMaxTotal(httpClientProperties.getMaxTotal());
        manager.setConnectionConfigResolver(route -> ConnectionConfig.custom()
                // 设置连接在空闲多久后需要验证有效性，单位为毫秒，通过 TimeValue 转换为内部使用的纳秒单位
                .setValidateAfterInactivity(TimeValue.ofMilliseconds(
                        httpClientProperties.getValidateAfterInactivity()))
                // 与服务器连接超时时间，创建socket连接的超时时间
                .setConnectTimeout(Timeout.ofMilliseconds(httpClientProperties.getConnectTimeout()))
                // socket读取数据的超时时间，从服务器获取数据的超时时间
                .setSocketTimeout(Timeout.ofMilliseconds(httpClientProperties.getSocketTimeout()))
                .build());
        log.info("Http Client Connection Pool Create Success. Params: {}", httpClientProperties);
        return manager;

    }

    /**
     * 创建 HttpClientBuilder 对象，用于创建 HttpClient 实例
     *
     * @param poolingHttpClientConnectionManager 连接池管理对象
     * @return HttpClientBuilder 对象
     */
    @Bean
    @ConditionalOnMissingBean
    @Profile({"default"})
    public HttpClientBuilder httpClientBuilder(
            PoolingHttpClientConnectionManager poolingHttpClientConnectionManager) {
        // 设置访问的用户名与密码
        BasicCredentialsProvider provider = getBasicCredentialsProvider();
        // 构建 HTTP 客户端构建器
        HttpClientBuilder builder = HttpClientBuilder.create()
                // 设置连接池管理对象，并设置为共享模式（适用于多线程环境）
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setDefaultRequestConfig(requestConfig()) // 设置默认请求配置（超时等参数）
                .setConnectionManagerShared(true) // 允许连接池在多个 HttpClient 实例间共享
                // 自定义 Keep-Alive 策略：优先使用响应头中的 timeout 值，否则使用配置文件中的默认值
                .setKeepAliveStrategy((httpResponse, httpContext) -> {
                    // 获取响应头中的 "Keep-Alive" 字段
                    Header header;
                    try {
                        header = httpResponse.getHeader("Keep-Alive");
                        if (header != null) {
                            String value = header.getValue();
                            // 使用字符串分割的方式解析 timeout 参数
                            String[] pairs = value.split(",");
                            for (String pair : pairs) {
                                pair = pair.trim();
                                if (pair.toLowerCase().startsWith("timeout=")) {
                                    try {
                                        int timeout = Integer.parseInt(pair.substring("timeout=".length()));
                                        // 返回单位为毫秒的 Keep-Alive 超时时间
                                        return TimeValue.ofMilliseconds(timeout);
                                    } catch (NumberFormatException e) {
                                        log.error("Invalid Keep-Alive timeout value: {}", pair, e);
                                    }
                                }
                            }
                        }
                        // 默认 Keep-Alive 时间（毫秒），来自配置文件
                        return TimeValue.ofMilliseconds(httpClientProperties.getResponseTimeout());
                    } catch (ProtocolException e) {
                        throw new RuntimeException(e);
                    }
                })
                .setDefaultCredentialsProvider(provider);

        // TODO 每 配置文件中的默认时间 清空一次失效连接
        log.info("Http Client Builder Create Success.");
        return builder;
    }

    /**
     * 创建 GeoServerHttpUtils 对象，用于与 GeoServer 进行交互
     *
     * @param httpClientBuilder HttpClientBuilder 对象
     * @return GeoServerHttpUtils 对象
     */
    @Bean
    @ConditionalOnMissingBean
    @Profile({"default"})
    public GeoServerHttpUtils geoServerHttpUtils(HttpClientBuilder httpClientBuilder) {
        return new GeoServerHttpUtils(httpClientBuilder, geoServerProperties);
    }

    /**
     * 创建 RequestConfig 对象，用于设置 HTTP 请求的超时时间
     *
     * @return RequestConfig 对象
     */
    private RequestConfig requestConfig() {
        return RequestConfig.custom()
                // 从链接池获取连接的超时时间
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(
                        httpClientProperties.getConnectionRequestTimeout()))
                .build();
    }

    /**
     * 创建 BasicCredentialsProvider 对象，用于设置用户名和密码
     *
     * @return BasicCredentialsProvider 对象
     */
    private BasicCredentialsProvider getBasicCredentialsProvider() {
        BasicCredentialsProvider provider = new BasicCredentialsProvider();
        String username = geoServerProperties.getUserName();
        String password = geoServerProperties.getPassword();
        if (username != null && password != null) {
            try {
                URI uri = new URI(geoServerProperties.getBaseUrl());
                AuthScope authScope = new AuthScope(uri.getHost(), uri.getPort());
                Credentials credentials = new UsernamePasswordCredentials(username, password.toCharArray());
                provider.setCredentials(authScope, credentials);
            } catch (URISyntaxException e) {
                throw new RuntimeException("Invalid GeoServer base URL", e);
            }
        }
        return provider;
    }

}

