package com.urban.carbon.limiter;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * LimiterTestConfiguration类用于配置限流相关的Bean。
 *
 * <p>功能包括：
 * - 根据配置初始化Redisson客户端；
 * - 在RedissonClient存在时创建滑动窗口限流器。</p>
 *
 * @author bjcug
 * @since 0.0.1
 */
@AutoConfiguration
public class LimiterTestConfiguration {
    /**
     * Redis地址配置项。
     */
    @Value("${redis.address}")
    private String redisAddress;

    /**
     * Redis密码配置项。
     */
    @Value("${redis.password}")
    private String redisPassword;

    /**
     * 创建Redisson客户端Bean。
     *
     * @return 返回RedissonClient实例
     */
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    RedissonClient redisson() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(redisAddress).setPassword(redisPassword);
        return Redisson.create(config);
    }

    /**
     * 创建滑动窗口限流器Bean。
     *
     * @param redisson Redisson客户端实例
     * @return 返回SlidingWindowRateLimiter实例
     */
    @Bean
    @ConditionalOnBean(RedissonClient.class)
    public SlidingWindowRateLimiter slidingWindowRateLimiter(RedissonClient redisson) {
        return new SlidingWindowRateLimiter(redisson);
    }
}

