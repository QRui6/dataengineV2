package com.urban.carbon.base.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 响应基类
 *
 * <p>所有响应类的基类，用于封装请求处理结果的基本结构，包含是否成功、响应码和响应消息</p>
 *
 * <p>使用方法示例：</p>
 * <ul>
 *     <li>直接继承此类定义业务响应：
 *         <pre>{@code public class UserResponse extends BaseResponse { ... }}</pre>
 *     </li>
 *     <li>设置请求成功响应：
 *         <pre>{@code response.setSuccess(true); response.setResponseCode("200"); response.setResponseMessage("操作成功");}</pre>
 *     </li>
 *     <li>设置请求失败响应：
 *         <pre>{@code response.setSuccess(false); response.setResponseCode("500"); response.setResponseMessage("系统异常");}</pre>
 *     </li>
 *     <li>在 Controller 中作为返回值返回给前端：
 *         <pre>{@code @GetMapping("/user") public UserResponse getUser() { ... }}</pre>
 *     </li>
 * </ul>
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Getter
@Setter
@ToString
public class BaseResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 这里需要注意, 有四种写法, 但是下面这种是较为合适的一种方法
     * 同时小的 boolean 默认值为 false 和 Boolean 默认值为 None, 使用后面的方法方便判断异常
     * 表示不是结果或者业务操作是否成功, 表示这次请求有没有成功, 
     * 判断成功会使用success与response code组合的方式来判断请求是否成功
     */
    private Boolean success;

    /**
     *  响应码
     */
    private String responseCode;

    /**
     * 响应消息
     */
    private String responseMessage;


}

