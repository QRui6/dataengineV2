package com.urban.carbon.base.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 基础请求类
 *
 * <p>所有请求类的基类，包含通用字段 loginId，用于携带登录用户信息</p>
 *
 * <p>使用方法示例：</p>
 * <ul>
 *     <li>直接继承此类定义业务请求：
 *         <pre>{@code public class UserRequest extends BaseRequest { ... }}</pre>
 *     </li>
 *     <li>设置和获取登录用户ID：
 *         <pre>{@code request.setLoginId(123L);}</pre>
 *     </li>
 *     <li>在 Controller 中作为接口参数接收：
 *         <pre>{@code @PostMapping("/save") public void save(@RequestBody UserRequest request) { ... }}</pre>
 *     </li>
 * </ul>
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Getter
@Setter
public class BaseRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 登录用户ID 可有可无
     */
    private Long loginId;
}

