package com.urban.carbon.base.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 业务异常
 *
 * <p>使用方法示例：</p>
 * <ul>
 *     <li>抛出无附加信息的业务异常：
 *         <pre>{@code throw new BizException(ErrorCode.INTERNAL_SERVER_ERROR);}</pre>
 *     </li>
 *     <li>抛出带有自定义错误信息的业务异常：
 *         <pre>{@code throw new BizException("资源不存在", ErrorCode.RESOURCE_NOT_FOUND);}</pre>
 *     </li>
 *     <li>抛出带有原因和错误码的业务异常：
 *         <pre>{@code throw new BizException(ErrorCode.DATABASE_ERROR, cause);}</pre>
 *     </li>
 *     <li>抛出带有详细信息、原因、错误码的业务异常：
 *         <pre>{@code throw new BizException("操作失败", cause, ErrorCode.OPERATION_FAILED);}</pre>
 *     </li>
 * </ul>
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Getter
@Setter
public class BizException extends RuntimeException {

    /**
     * 错误码
     */
    private ErrorCode errorCode;

    /**
     * 构造函数
     *
     * @param errorCode 错误码
     */
    public BizException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     *
     * @param message   错误信息
     * @param errorCode 错误码
     */
    public BizException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     *
     * @param message   错误信息
     * @param cause     错误 cause
     * @param errorCode 错误码
     */
    public BizException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     *
     * @param cause     错误 cause
     * @param errorCode 错误码
     */
    public BizException(Throwable cause, ErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     *
     * @param message             错误信息
     * @param cause               错误 cause
     * @param enableSuppression   是否启用 suppression
     * @param writableStackTrace  是否可写 stack trace
     * @param errorCode 错误码
     */
    public BizException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
    }
}

