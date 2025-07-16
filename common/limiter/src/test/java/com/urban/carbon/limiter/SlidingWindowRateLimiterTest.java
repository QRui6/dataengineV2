package com.urban.carbon.limiter;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LimiterTestConfiguration.class})
@ActiveProfiles("test")
public class SlidingWindowRateLimiterTest {

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private SlidingWindowRateLimiter limiter;

    /**
     * testRedissonClient方法用于测试Redisson客户端是否正常工作。
     */
    @Test
    public void testRedissonClient() {
        redisson.getMap("testMap").put("key", "value");
        System.out.println(redisson.getMap("testMap").get("key"));
    }

    /**
     * testSlidingWindowRateLimiter方法用于测试滑动窗口限流器的功能。
     */
    @Test
    public void testSlidingWindowRateLimiter() {
        Boolean result = limiter.tryAcquire("testLock1", 3, 10);
        Assert.assertTrue(result);
        result = limiter.tryAcquire("testLock1", 3, 10);
        Assert.assertTrue(result);
        result = limiter.tryAcquire("testLock1", 3, 10);
        Assert.assertTrue(result);
        result = limiter.tryAcquire("testLock1", 3, 10);
        Assert.assertFalse(result);
    }
}

