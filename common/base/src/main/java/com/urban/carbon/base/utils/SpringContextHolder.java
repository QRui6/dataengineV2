package com.urban.carbon.base.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;

/**
 * Spring 容器上下文持有者工具类。
 *
 * <p>该类实现了 {@link ApplicationContextAware} 接口，用于在应用启动时自动注入 Spring 容器上下文。
 * 通过此类可以在非 Spring 管理的类中获取 Spring 容器中的 Bean，例如工具类、拦截器、监听器等场景。</p>
 *
 * <p><strong>使用方式示例：</strong></p>
 * <pre>{@code
 * MyService myService = SpringContextHolder.getBean(MyService.class);
 * }</pre>
 *
 * @author XuGaoran
 * @since 0.0.1
 */
public class SpringContextHolder implements ApplicationContextAware {

    /**
     * Spring 应用上下文对象，用于获取容器中的 Bean。
     */
    private static ApplicationContext applicationContext;

    /**
     * 设置 Spring 应用上下文。
     *
     * <p>此方法由 Spring 框架在初始化该 Bean 时自动调用，注入当前应用的上下文对象。</p>
     *
     * @param applicationContext Spring 应用上下文，不能为空
     * @throws BeansException 如果设置上下文过程中发生异常
     */
    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }

    /**
     * 根据 Bean 名称从 Spring 容器中获取 Bean 实例。
     *
     * @param name 要获取的 Bean 的名称
     * @return 对应名称的 Bean 实例
     * @throws BeansException 如果找不到对应的 Bean 或获取失败
     */
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    /**
     * 根据 Bean 类型从 Spring 容器中获取 Bean 实例。
     *
     * <p>适用于已知 Bean 类型的情况，避免手动类型转换。</p>
     *
     * @param beanClass 要获取的 Bean 的类型
     * @param <T>       泛型参数，表示 Bean 的具体类型
     * @return 匹配类型的 Bean 实例
     * @throws BeansException 如果找不到对应的 Bean 或获取失败
     */
    public static <T> T getBean(Class<T> beanClass) {
        return applicationContext.getBean(beanClass);
    }
}

