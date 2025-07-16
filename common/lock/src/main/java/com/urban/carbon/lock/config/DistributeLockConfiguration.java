package com.urban.carbon.lock.config;

import com.urban.carbon.lock.DistributeLockAspect;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 分布式锁配置
 *
 * @author XuGaoran
 */
@Configuration
public class DistributeLockConfiguration {

    /**
     * 如果没有 META-INF.spring.org.springframework.boot.autoconfigure.AutoConfiguration.imports
     * 下面引入 RedissonClient 会出现报错
     *
     * @param redisson redisson客户端，在cache模块中被注入
     * @return 返回分布式锁的切面逻辑
     */
    @Bean
    @ConditionalOnMissingBean
    public DistributeLockAspect distributeLockAspect(RedissonClient redisson) {
        return new DistributeLockAspect(redisson);
    }
}

