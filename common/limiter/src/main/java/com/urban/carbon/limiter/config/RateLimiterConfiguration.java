package com.urban.carbon.limiter.config;

import com.urban.carbon.limiter.SlidingWindowRateLimiter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 限流器组件注册到 spring 容器中
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Configuration
public class RateLimiterConfiguration {

    /**
     * 创建并返回一个SlidingWindowRateLimiter实例。
     *
     * @param redisson Redisson客户端实例
     * @return SlidingWindowRateLimiter实例
     */
    @Bean
    public SlidingWindowRateLimiter slidingWindowRateLimiter(RedissonClient redisson) {
        return new SlidingWindowRateLimiter(redisson);
    }
}

