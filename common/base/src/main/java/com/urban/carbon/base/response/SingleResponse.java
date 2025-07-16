package com.urban.carbon.base.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * 单个响应
 *
 * <p>用于封装单个数据项的响应对象，继承自 BaseResponse，包含一个泛型字段表示具体的响应数据</p>
 *
 * <p>使用方法示例：</p>
 * <ul>
 *     <li>定义具体响应类：
 *         <pre>{@code public class UserSingleResponse extends SingleResponse<UserVO> { ... }}</pre>
 *     </li>
 *     <li>创建成功响应：
 *         <pre>{@code UserVO user = getUser(); return SingleResponse.of(user);}</pre>
 *     </li>
 *     <li>创建失败响应：
 *         <pre>{@code return SingleResponse.fail("500", "用户不存在");}</pre>
 *     </li>
 *     <li>在 Controller 中返回单个结果接口：
 *         <pre>{@code @GetMapping("/user") public SingleResponse<UserVO> getUser() { ... }}</pre>
 *     </li>
 *     <li>手动设置数据和响应状态：
 *         <pre>{@code singleResponse.setData(user); singleResponse.setSuccess(true);}</pre>
 *     </li>
 * </ul>
 *
 * @param <T> 响应的数据类型
 * @author XuGaoran
 * @since 0.0.1
 */
@Setter
@Getter
public class SingleResponse<T> extends BaseResponse {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 响应的数据内容
     */
    private T data;

    /**
     * 创建一个成功的SingleResponse实例。
     *
     * @param data 响应的数据内容
     * @param <T>  数据类型
     * @return 成功的SingleResponse实例
     */
    public static <T> SingleResponse<T> of(T data) {
        SingleResponse<T> singleResponse = new SingleResponse<>();
        singleResponse.setSuccess(true);
        singleResponse.setData(data);
        return singleResponse;
    }

    /**
     * 创建一个失败的SingleResponse实例。
     *
     * @param errorCode 错误代码
     * @param errorMessage 错误信息
     * @param <T> 数据类型
     * @return 失败的SingleResponse实例
     */
    public static <T> SingleResponse<T> fail(String errorCode, String errorMessage) {
        SingleResponse<T> singleResponse = new SingleResponse<>();
        singleResponse.setSuccess(false);
        singleResponse.setResponseCode(errorCode);
        singleResponse.setResponseMessage(errorMessage);
        return singleResponse;
    }

}

