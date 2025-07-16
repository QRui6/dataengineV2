package com.urban.carbon.base.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

/**
 * 查询响应类
 *
 * <p>用于封装查询操作的响应数据，继承自 BaseResponse，包含一个泛型字段表示查询结果数据</p>
 *
 * <p>使用方法示例：</p>
 * <ul>
 *     <li>定义具体查询响应类：
 *         <pre>{@code public class UserQueryResponse extends QueryResponse<UserVO> { ... }}</pre>
 *     </li>
 *     <li>创建并设置响应数据：
 *         <pre>{@code UserVO user = getUser(); QueryResponse<UserVO> response = new QueryResponse<>(); response.setData(user);}</pre>
 *     </li>
 *     <li>在 Controller 中作为接口返回类型：
 *         <pre>{@code @GetMapping("/user") public QueryResponse<UserVO> getUser() { ... }}</pre>
 *     </li>
 *     <li>结合成功/失败状态和消息返回给前端：
 *         <pre>{@code response.setSuccess(true); response.setResponseCode("200"); response.setResponseMessage("查询成功");}</pre>
 *     </li>
 * </ul>
 *
 * @param <T> 查询结果的数据类型
 * @author XuGaoran
 * @since 0.0.1
 */
@Getter
@Setter
@ToString
public class QueryResponse<T> extends BaseResponse {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 结果数据
     */
    T data;
}

