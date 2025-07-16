package com.urban.carbon.data.exception;

import com.urban.carbon.base.exception.ErrorCode;

/**
 * 数据库相关异常
 *
 * <p>使用方法示例：</p>
 * <pre>
 * // 抛出数据库插入失败异常
 * throw new BizException(RepoErrorCode.INSERT_FAILED);
 *
 * // 抛出带有自定义信息的未知错误异常
 * throw new BizException("发生未知数据库错误", RepoErrorCode.UNKNOWN_ERROR);
 *
 * // 在捕获异常时使用错误码
 * try {
 *     // 数据库操作逻辑
 * } catch (SomeException e) {
 *     throw new BizException(RepoErrorCode.UPDATE_FAILED, e);
 * }
 * </pre>
 *
 * @author XuGaoran
 * @since 0.0.1
 */
public enum RepoErrorCode implements ErrorCode {

    /**
     * 未知错误
     */
    UNKNOWN_ERROR("UNKNOWN_ERROR", "未知错误"),

    /**
     * 数据库插入失败
     */
    INSERT_FAILED("INSERT_FAILED", "数据库插入失败"),

    /**
     * 数据库更新失败
     */
    UPDATE_FAILED("UPDATE_FAILED", "数据库更新失败");

    private final String code;

    private final String message;

    RepoErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}

