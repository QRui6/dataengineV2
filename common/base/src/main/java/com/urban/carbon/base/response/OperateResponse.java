package com.urban.carbon.base.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * 操作响应类
 *
 * <p>用于封装单个操作结果的响应数据，继承自 BaseResponse，包含一个泛型数据字段</p>
 *
 * <p>使用方法示例：</p>
 * <ul>
 *     <li>定义具体响应类：
 *         <pre>{@code public class UserOperateResponse extends OperateResponse<UserVO> { ... }}</pre>
 *     </li>
 *     <li>创建并设置响应数据：
 *         <pre>{@code UserVO user = getUser(); return OperateResponse.of(user);}</pre>
 *     </li>
 *     <li>在 Controller 中返回操作结果：
 *         <pre>{@code @PostMapping("/save") public OperateResponse<UserVO> saveUser(@RequestBody UserRequest request) { ... }}</pre>
 *     </li>
 *     <li>设置成功或失败状态及消息：
 *         <pre>{@code response.setSuccess(true); response.setResponseCode("200"); response.setResponseMessage("操作成功");}</pre>
 *     </li>
 * </ul>
 *
 * @param <T> 数据类型
 * @author XuGaoran
 * @since 0.0.1
 */
@Getter
@Setter
public class OperateResponse<T> extends BaseResponse {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据
     */
    T data;
}

