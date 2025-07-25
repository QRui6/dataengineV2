package com.urban.carbon.admin.domain.service;

import com.alicp.jetcache.Cache;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.urban.carbon.admin.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserCacheDelayDeleteService {
    // ThreadFactory 是 Java 并发包中的一个接口，用于创建线程。通过自定义线程工厂，可以控制线程的名称、优先级等属性。
    // 设置线程名称格式，%d 是占位符，会被替换为线程的编号。例如：user-cache-delay-delete-pool-1。
    private static final ThreadFactory userCacheDelayProcessFactory = new ThreadFactoryBuilder()
            .setNameFormat("user-cache-delay-delete-pool-%d").build();

    // 这是一个接口，提供了调度任务的能力，例如延迟执行或周期性执行任务。
    // 将前面定义的线程工厂传递给线程池，确保线程池中的线程使用指定的名称格式。
    private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(
            10, userCacheDelayProcessFactory);

    /**
     * 延迟删除缓存中的数据
     *
     * @param idUserCache 用户信息的缓存列表
     * @param user 对应用户信息
     */
    public void delayedCacheDelete(Cache<String, User> idUserCache, User user) {
        // 调用调度线程池的 schedule 方法，安排一个延迟任务。
        scheduler.schedule(() -> {
            // 从缓存中移除指定的用户键值对。
            boolean idDeleteResult = idUserCache.remove(user.getId().toString());
            log.info("idUserCache removed, key = {} , result  = {}", user.getId(), idDeleteResult);
        }, 2, TimeUnit.SECONDS);
    }
}
