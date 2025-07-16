package com.urban.carbon.lock;

/**
 * DistributeLockException类用于表示分布式锁相关的异常。
 *
 * <p>该异常继承自RuntimeException，用于在获取或释放分布式锁时发生错误时抛出。</p>
 *
 * @author bjcug
 * @since 0.0.1
 */
public class DistributeLockException extends RuntimeException {
    public DistributeLockException() {
    }

    public DistributeLockException(String message) {
        super(message);
    }

    public DistributeLockException(String message, Throwable cause) {
        super(message, cause);
    }

    public DistributeLockException(Throwable cause) {
        super(cause);
    }

    public DistributeLockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

