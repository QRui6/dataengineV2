package com.urban.carbon.base.configuration;

import com.urban.carbon.base.utils.SpringContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring基础配置类。
 *
 * <p>该类用于注册Spring上下文相关的Bean，主要用于注入 {@link SpringContextHolder}，
 * 以便在非Spring管理的类中获取Spring容器中的Bean实例。</p>
 *
 * <p><strong>使用方式：</strong></p>
 * <pre>{@code
 * @Autowired
 * private SpringContextHolder springContextHolder;
 * }</pre>
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Configuration
public class BaseConfiguration {

    /**
     * 注册 SpringContextHolder Bean 到Spring容器中。
     *
     * <p>{@link SpringContextHolder} 是一个工具类，用于持有Spring应用上下文，
     * 可以在静态方法或非Spring管理的类中方便地获取Spring容器中的Bean。</p>
     *
     * @return 返回一个新的 SpringContextHolder 实例
     */
    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }
}

