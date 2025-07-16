package com.urban.carbon.cache.config;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import com.urban.carbon.cache.CacheDelayDeleteService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
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

    /**
     * 创建缓存延迟删除服务Bean。
     *
     * @return 返回CacheDelayDeleteService实例
     */
    @Bean
    @ConditionalOnMissingBean
    public CacheDelayDeleteService cacheDelayDeleteService() {
        return new CacheDelayDeleteService();
    }
}

