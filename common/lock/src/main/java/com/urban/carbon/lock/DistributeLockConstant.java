package com.urban.carbon.lock;

/**
 * 分布式锁可能会用到的常量
 *
 * @author XuGaoran
 */
public class DistributeLockConstant {

    public static final String NONE_KEY = "NONE";

    public static final String DEFAULT_OWNER = "UCARBON";

    /**
     * 默认过期时间为 -1，表示不会过期
     */
    public static final int DEFAULT_EXPIRE_TIME = -1;

    /**
     * 默认是无限等待
     */
    public static final int DEFAULT_WAIT_TIME = Integer.MAX_VALUE;
}

