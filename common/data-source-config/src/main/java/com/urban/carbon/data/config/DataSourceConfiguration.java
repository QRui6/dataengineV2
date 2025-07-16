package com.urban.carbon.data.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.urban.carbon.data.handler.DataObjectHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis 配置类
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Configuration
public class DataSourceConfiguration {

    /**
     * 配置数据自动填充方法
     *
     * @return 自定义自动填充方法
     */
    @Bean
    public DataObjectHandler myDataObjectHandler() {
        // 返回自定义自动填充方法
        return new DataObjectHandler();
    }

    /**
     * 配置 Mybatis 拦截器
     *
     * @return 返回 Mybatis 拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        // 创建拦截器
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 防全表更新与删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        // 返回拦截器
        return interceptor;
    }
}

