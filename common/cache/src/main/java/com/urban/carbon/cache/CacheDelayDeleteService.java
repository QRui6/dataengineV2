package com.urban.carbon.cache;

import com.alicp.jetcache.Cache;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * CacheDelayDeleteService 类用于处理缓存的延迟删除逻辑。
 *
 * <p>提供一个调度线程池，用于在指定时间后从缓存中移除指定的键。</p>
 *
 * <p>使用示例：</p>
 * <pre>
 *     // 创建缓存实例（假设已注入或初始化）
 *     Cache<String, Object> userCache = ...;
 *
 *     // 调用延迟删除方法，2秒后删除 key 为 "user:123" 的缓存项
 *     cacheDelayDeleteService.delayedCacheDelete(userCache, "user:123");
 * </pre>
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Slf4j
@Service
public class CacheDelayDeleteService {

    // ThreadFactory 是 Java 并发包中的一个接口，用于创建线程。通过自定义线程工厂，可以控制线程的名称、优先级等属性。
    // 设置线程名称格式，%d 是占位符，会被替换为线程的编号。例如：cache-delay-delete-pool-1。
    private static final ThreadFactory cacheDelayProcessFactory = new ThreadFactoryBuilder()
            .setNameFormat("cache-delay-delete-pool-%d").build();

    // 这是一个接口，提供了调度任务的能力，例如延迟执行或周期性执行任务。
    // 将前面定义的线程工厂传递给线程池，确保线程池中的线程使用指定的名称格式。
    private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(
            10, cacheDelayProcessFactory);

    /**
     * 延迟删除缓存中的数据
     *
     * @param idCache 信息的缓存列表
     * @param id 键值
     */
    public void delayedCacheDelete(Cache<String, ?> idCache, String id) {
        // 调用调度线程池的 schedule 方法，安排一个延迟任务。
        scheduler.schedule(() -> {
            // 从缓存中移除指定的用户键值对。
            boolean idDeleteResult = idCache.remove(id);
            log.info("idCache removed, key = {} , result  = {}", id, idDeleteResult);
        }, 2, TimeUnit.SECONDS);
    }
}

