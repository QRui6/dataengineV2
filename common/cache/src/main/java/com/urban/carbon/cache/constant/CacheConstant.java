package com.urban.carbon.cache.constant;

/**
 * CacheConstant类用于定义缓存相关的常量。
 *
 * <p>包含缓存key的分隔符以及各类缓存前缀。</p>
 *
 * <p>使用示例：</p>
 * <pre>
 *     String userCacheKey = CacheConstant.USER_CACHE_KEY_PREFIX + CacheConstant.CACHE_KEY_SEPARATOR + "12345";
 *     String fileCacheKey = CacheConstant.FILE_CACHE_KEY_PREFIX + CacheConstant.CACHE_KEY_SEPARATOR + "file123";
 * </pre>
 *
 * @author XuGaoran
 * @since 0.0.1
 */
public class CacheConstant {

    /**
     * 缓存key的分隔符
     */
    public static final String CACHE_KEY_SEPARATOR = ":";

    /**
     * 用户缓存前缀
     */
    public static final String USER_CACHE_KEY_PREFIX = "user:cache:id:";

    /**
     * 文件缓存前缀
     */
    public static final String FILE_CACHE_KEY_PREFIX = "file:cache:id:";

    /**
     * 分块缓存前缀
     */
    public static final String CHUNK_CACHE_KEY_PREFIX = "chunk:cache:id";
}

