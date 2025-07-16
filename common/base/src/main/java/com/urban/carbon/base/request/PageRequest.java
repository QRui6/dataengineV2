package com.urban.carbon.base.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;

/**
 * 分页请求
 *
 * <p>用于封装分页查询的请求参数，继承自 BaseRequest，包含当前页码和每页记录数</p>
 *
 * <p>使用方法示例：</p>
 * <ul>
 *     <li>在 Controller 中接收分页参数：
 *         <pre>{@code @GetMapping("/list") public List<User> list(PageRequest pageRequest) { ... }}</pre>
 *     </li>
 *     <li>设置当前页和每页大小：
 *         <pre>{@code pageRequest.setCurrentPage(1); pageRequest.setPageSize(10);}</pre>
 *     </li>
 *     <li>作为业务层方法参数进行分页处理：
 *         <pre>{@code public Page<User> getUsers(PageRequest pageRequest) { ... }}</pre>
 *     </li>
 * </ul>
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest extends BaseRequest {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前页
     */
    private int currentPage;

    /**
     * 每页结果数
     */
    private int pageSize;
}

