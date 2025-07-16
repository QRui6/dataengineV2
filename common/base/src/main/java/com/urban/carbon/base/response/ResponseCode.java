package com.urban.carbon.base.response;

/**
 * ResponseCode 枚举用于表示不同类型的响应代码。
 *
 * <p>包含的响应代码有：</p>
 * <ul>
 *   <li>SUCCESS - 成功</li>
 *   <li>DUPLICATED - 重复</li>
 *   <li>ILLEGAL_ARGUMENT - 非法参数</li>
 *   <li>SYSTEM_ERROR - 系统错误</li>
 *   <li>BIZ_ERROR - 业务错误</li>
 * </ul>
 *
 * @author bjcug
 * @since 0.0.1
 */
public enum ResponseCode {
    /**
     * 成功
     */
    SUCCESS,

    /**
     * 重复
     */
    DUPLICATED,

    /**
     * 非法参数
     */
    ILLEGAL_ARGUMENT,

    /**
     * 系统错误
     */
    SYSTEM_ERROR,

    /**
     * 业务错误
     */
    BIZ_ERROR;
}

