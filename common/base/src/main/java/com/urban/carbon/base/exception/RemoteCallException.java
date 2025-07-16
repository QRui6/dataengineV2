package com.urban.carbon.base.exception;

/**
 * 远程调用抛出的异常
 *
 * <p>使用方法示例：</p>
 * <ul>
 *     <li>直接抛出远程调用错误：
 *         <pre>{@code throw new RemoteCallException(RemoteCallErrorCode.REMOTE_CALL_FAILED);}</pre>
 *     </li>
 *     <li>抛出带有自定义错误信息的远程调用异常：
 *         <pre>{@code throw new RemoteCallException("远程服务调用失败", RemoteCallErrorCode.REMOTE_CALL_FAILED);}</pre>
 *     </li>
 *     <li>捕获其他异常并包装成 RemoteCallException 抛出：
 *         <pre>{@code try {
 *             // 远程调用逻辑
 *         } catch (IOException e) {
 *             throw new RemoteCallException("调用超时", e, RemoteCallErrorCode.REMOTE_TIMEOUT);
 *         }}</pre>
 *     </li>
 * </ul>
 *
 * @author XuGaoran
 * @since 0.0.1
 */
public class RemoteCallException extends SystemException{

    /**
     * 构造函数
     *
     * @param errorCode 错误码
     */
    public RemoteCallException(ErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 构造函数
     *
     * @param message 错误信息
     * @param errorCode 错误码
     */
    public RemoteCallException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    /**
     * 构造函数
     *
     * @param message 错误信息
     * @param cause 错误 cause
     * @param errorCode 错误码
     */
    public RemoteCallException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    /**
     * 构造函数
     *
     * @param cause 错误 cause
     * @param errorCode 错误码
     */
    public RemoteCallException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    /**
     * 构造函数
     *
     * @param message 错误信息
     * @param cause 错误 cause
     * @param enableSuppression 是否启用 suppression
     * @param writableStackTrace 是否可写 stack trace
     * @param errorCode 错误码
     */
    public RemoteCallException(String message, Throwable cause, boolean enableSuppression,
                               boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode);
    }
}

