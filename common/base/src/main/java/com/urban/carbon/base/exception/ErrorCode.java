package com.urban.carbon.base.exception;

/**
 * 错误码接口
 *
 * <p>实现该接口的枚举或类应定义错误码和对应的错误信息，用于统一管理业务异常中的错误信息</p>
 *
 * <p>使用方法示例：</p>
 * <pre>
 * public enum CommonErrorCode implements ErrorCode {
 *     INTERNAL_SERVER_ERROR("500", "内部服务器错误"),
 *     RESOURCE_NOT_FOUND("404", "资源不存在");
 *
 *     private final String code;
 *     private final String message;
 *
 *     CommonErrorCode(String code, String message) {
 *         this.code = code;
 *         this.message = message;
 *     }
 *
 *     {@literal @}Override
 *     public String getCode() {
 *         return code;
 *     }
 *
 *     {@literal @}Override
 *     public String getMessage() {
 *         return message;
 *     }
 * }
 * </pre>
 *
 * @author XuGaoran
 * @since 0.0.1
 */
public interface ErrorCode {
    /**
     * 错误码
     *
     * @return 错误码
     */
    String getCode();

    /**
     * 错误信息
     *
     * @return 错误信息
     */
    String getMessage();
}

