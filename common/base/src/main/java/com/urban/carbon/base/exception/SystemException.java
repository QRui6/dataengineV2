package com.urban.carbon.base.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 系统异常
 *
 * <p>此类作为系统中所有运行时异常的基类，支持通过错误码定义异常类型，并可携带额外的错误信息和异常原因</p>
 *
 * <p>使用方法示例：</p>
 * <ul>
 *     <li>直接抛出带有错误码的系统异常：
 *         <pre>{@code throw new SystemException(SystemErrorCode.SYSTEM_BUSY);}</pre>
 *     </li>
 *     <li>抛出带有自定义错误信息的异常：
 *         <pre>{@code throw new SystemException("服务暂时不可用", SystemErrorCode.SERVICE_UNAVAILABLE);}</pre>
 *     </li>
 *     <li>包装其他异常并抛出：
 *         <pre>{@code try {
 *             // 可能抛出异常的操作
 *         } catch (IOException e) {
 *             throw new SystemException("IO 操作失败", e, SystemErrorCode.IO_ERROR);
 *         }}</pre>
 *     </li>
 *     <li>使用详细构造函数控制异常行为：
 *         <pre>{@code throw new SystemException("超时", cause, true, true, SystemErrorCode.TIMEOUT);}</pre>
 *     </li>
 * </ul>
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Getter
@Setter
public class SystemException extends RuntimeException {

    /**
     * 错误码
     */
    private ErrorCode errorCode;

    /**
     * 构造函数
     *
     * @param errorCode 错误码
     */
    public SystemException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     *
     * @param message 错误信息
     * @param errorCode 错误码
     */
    public SystemException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     *
     * @param message 错误信息
     * @param cause 错误 cause
     * @param errorCode 错误码
     */
    public SystemException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     *
     * @param cause 错误 cause
     * @param errorCode 错误码
     */
    public SystemException(Throwable cause, ErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     *
     * @param message 错误信息
     * @param cause 错误 cause
     * @param enableSuppression 是否启用 suppression
     * @param writableStackTrace 是否可写堆栈信息
     * @param errorCode 错误码
     */
    public SystemException(String message, Throwable cause, boolean enableSuppression,
                           boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
    }
}

