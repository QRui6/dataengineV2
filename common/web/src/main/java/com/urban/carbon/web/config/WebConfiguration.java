package com.urban.carbon.web.config;

import com.urban.carbon.web.filter.TokenFilter;
import com.urban.carbon.web.handler.GlobalExceptionHandler;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebConfiguration类用于配置Web相关的Bean。
 *
 * <p>功能包括：
 * - 注册全局异常处理器；
 * - 注册Token过滤器。</p>
 *
 * @author bjcug
 * @since 0.0.1
 */
@AutoConfiguration
@ConditionalOnWebApplication
public class WebConfiguration implements WebMvcConfigurer {

    /**
     * 创建全局异常处理器Bean。
     *
     * @return 全局异常处理器实例
     */
    @Bean
    @ConditionalOnMissingBean
    GlobalExceptionHandler globalWebExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    /**
     * 注册token过滤器
     *
     * @param redissonClient redisson客户端
     * @return token过滤器
     */
    @Bean
    public FilterRegistrationBean<TokenFilter> tokenFilter(RedissonClient redissonClient) {
        FilterRegistrationBean<TokenFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TokenFilter(redissonClient));
        // todo 需要修改成自己的网址
        registrationBean.addUrlPatterns("/trade/buy","/trade/newBuy","/trade/normalBuy");
        registrationBean.setOrder(10);

        return registrationBean;
    }

}

