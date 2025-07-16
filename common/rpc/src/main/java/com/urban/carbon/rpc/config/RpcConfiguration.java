package com.urban.carbon.rpc.config;

import com.urban.carbon.rpc.facade.FacadeAspect;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 将rpc服务注册成一个bean, 并开启dubbo
 */
@EnableDubbo
@Configuration
public class RpcConfiguration {
    /**
     * 创建FacadeAspect的Bean，仅在没有其他同类型Bean时生效。
     *
     * @return 返回FacadeAspect实例
     */
    @Bean
    @ConditionalOnMissingBean
    public FacadeAspect facadeAspect() {
        return new FacadeAspect();
    }
}

