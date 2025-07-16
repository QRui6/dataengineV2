package com.urban.carbon.limiter;

/**
 * 限流组件的接口, 这里是别的模块可以统一调用的内容, 同时也可以存在多种不同的限流器实现
 *
 * @author XuGaoran
 * @since 0.0.1
 */
public interface RateLimiter {
    /**
     * 判断一个key是否可以通过
     *
     * @param key 限流的key
     * @param limit 限流的数量
     * @param windowSize 窗口大小, 单位为秒
     * @return 返回是否可以插入
     */
    Boolean tryAcquire(String key, int limit, int windowSize);
}

