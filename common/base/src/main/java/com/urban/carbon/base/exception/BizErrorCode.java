package com.urban.carbon.base.exception;

/**
 * 业务错误码定义枚举类。
 *
 * <p>该枚举定义了系统中通用的业务错误码及对应的描述信息，
 * 主要用于统一异常处理和日志记录。</p>
 *
 * <p><strong>使用方式示例：</strong></p>
 * <pre>{@code
 * throw new BizException(BizErrorCode.HTTP_CLIENT_ERROR);
 * }</pre>
 *
 * @author XuGaoran
 * @since 0.0.1
 */
public enum BizErrorCode implements ErrorCode {
    /**
     * HTTP 客户端错误。
     * 表示由客户端请求导致的异常，如参数错误、权限不足等。
     */
    HTTP_CLIENT_ERROR("HTTP_CLIENT_ERROR", "HTTP 客户端错误"),

    /**
     * HTTP 服务端错误。
     * 表示服务器内部发生异常或无法处理的错误。
     */
    HTTP_SERVER_ERROR("HTTP_SERVER_ERROR", "HTTP 服务端错误"),

    /**
     * 不允许重复发送通知。
     *
     * <p>使用场景说明：</p>
     * <ul>
     *   <li>在 AuthController 中，用于限制 1 分钟内一个手机号只能注册一次</li>
     * </ul>
     */
    SEND_REGISTER_DUPLICATED("SEND_NOTICE_DUPLICATED", "1分钟内不允许相同手机号多次注册"),

    /**
     * 通知保存失败。
     * 表示通知消息在持久化过程中出现异常。
     */
    NOTICE_SAVE_FAILED("NOTICE_SAVE_FAILED", "通知保存失败"),

    /**
     * 状态机转换失败。
     * 表示状态流转不符合预期流程，通常发生在状态变更操作时。
     */
    STATE_MACHINE_TRANSITION_FAILED("STATE_MACHINE_TRANSITION_FAILED", "状态机转换失败"),

    /**
     * 重复请求。
     * 用于标识当前请求已经被处理过，防止重复提交或执行。
     */
    DUPLICATED("DUPLICATED", "重复请求"),

    /**
     * 远程调用返回结果为空。
     * 表示远程服务调用成功但返回值为 null。
     */
    REMOTE_CALL_RESPONSE_IS_NULL("REMOTE_CALL_RESPONSE_IS_NULL", "远程调用返回结果为空"),

    /**
     * 远程调用返回结果失败。
     * 表示远程服务调用逻辑上失败（如返回 success=false）。
     */
    REMOTE_CALL_RESPONSE_IS_FAILED("REMOTE_CALL_RESPONSE_IS_FAILED", "远程调用返回结果失败");

    /**
     * 错误码字段。
     * 对应具体的错误标识字符串。
     */
    private final String code;

    /**
     * 错误信息字段。
     * 对应错误码的可读性描述。
     */
    private final String message;

    BizErrorCode(String code, String message) {
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

