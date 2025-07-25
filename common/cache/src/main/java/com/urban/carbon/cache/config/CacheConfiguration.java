package com.urban.carbon.cache.config;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存配置
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Configuration
@EnableMethodCache(basePackages = "com.urban.carbon")
public class CacheConfiguration {

}

