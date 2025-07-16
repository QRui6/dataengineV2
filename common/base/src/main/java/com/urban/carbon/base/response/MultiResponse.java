package com.urban.carbon.base.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * MultiResponse类用于封装包含多个数据项的响应对象。
 *
 * <p>该类继承自BaseResponse，用于表示远程调用返回的多数据响应。</p>
 *
 * <p>使用方法示例：</p>
 * <ul>
 *     <li>定义泛型响应类：
 *         <pre>{@code public class UserMultiResponse extends MultiResponse<UserVO> { ... }}</pre>
 *     </li>
 *     <li>创建并返回数据列表响应：
 *         <pre>{@code List<UserVO> users = getUserList(); return MultiResponse.of(users);}</pre>
 *     </li>
 *     <li>在 Controller 中返回批量数据接口：
 *         <pre>{@code @GetMapping("/list") public MultiResponse<UserVO> listUsers() { ... }}</pre>
 *     </li>
 *     <li>设置自定义响应状态和消息：
 *         <pre>{@code multiResponse.setSuccess(true); multiResponse.setResponseCode("200"); multiResponse.setResponseMessage("查询成功");}</pre>
 *     </li>
 * </ul>
 *
 * @param <T> 数据项类型
 * @author XuGaoran
 * @since 0.0.1
 */
@Getter
@Setter
public class MultiResponse<T> extends BaseResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据集合，用于存储多个响应数据项。
     */
    private List<T> datas;

    /**
     * 创建一个包含指定数据的MultiResponse实例。
     *
     * @param datas 数据集合
     * @param <T>   数据项类型
     * @return 返回初始化后的MultiResponse实例
     */
    public static <T> MultiResponse<T> of(List<T> datas) {
        MultiResponse<T> multiResponse = new MultiResponse<>();
        multiResponse.setSuccess(true);
        multiResponse.setDatas(datas);
        return multiResponse;
    }
}

