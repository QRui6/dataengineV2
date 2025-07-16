package com.urban.carbon.limiter;

import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;

/**
 * 限流组件
 *
 * <p>该限流组件在概念上存在争议, 我们为该限流组件命名为 <strong>滑动窗口限流器</strong>,
 * 但实际上, 该限流器是漏桶的逻辑, 但是在 tryAcquire 方法的 limit 参数设置为 1 的时候,
 * 该限流器会从<strong>漏桶</strong>模式, 变成 <strong>滑动窗口</strong>模式.
 * </p>
 *
 * @author XuGaoran
 * @since 0.0.1
 */
public class SlidingWindowRateLimiter implements RateLimiter {

    /**
     * Redisson 客户端
     */
    private final RedissonClient redissonClient;
    public SlidingWindowRateLimiter(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 限流组件前缀
     */
    private static final String LIMIT_KEY_PREFIX = "ucarbon:limit:";

    /**
     * 限流组件的实现
     *
     * @param key 限流的key
     * @param limit 限流的数量
     * @param windowSize 窗口大小, 单位为秒
     * @return Boolean 是否可以通过, 可以为 True, 不可以为 False.
     */
    @Override
    public Boolean tryAcquire(String key, int limit, int windowSize) {
        // 使用 Redisson 携带的限流器
        RRateLimiter rRateLimiter = redissonClient.getRateLimiter(LIMIT_KEY_PREFIX + key);
        // 判断该限流器是否存在, 不存在就按照我们的参数进行设置
        if (!rRateLimiter.isExists()) {
            rRateLimiter.trySetRate(RateType.OVERALL, limit, windowSize, RateIntervalUnit.SECONDS);
        }
        // 使用该限流器进行限流
        return rRateLimiter.tryAcquire();
    }
}

