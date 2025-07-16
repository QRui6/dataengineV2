package com.urban.carbon.base.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.List;

/**
 * 分页响应
 *
 * <p>用于封装分页查询结果的响应数据，继承自 MultiResponse，包含当前页、每页数量、总页数和总数等分页信息</p>
 *
 * <p>使用方法示例：</p>
 * <ul>
 *     <li>定义泛型分页响应类：
 *         <pre>{@code public class UserPageResponse extends PageResponse<UserVO> { ... }}</pre>
 *     </li>
 *     <li>创建并返回分页响应：
 *         <pre>{@code List<UserVO> users = getUserList(); return PageResponse.of(users, total, pageSize, currentPage);}</pre>
 *     </li>
 *     <li>在 Controller 中返回分页数据接口：
 *         <pre>{@code @GetMapping("/page") public PageResponse<UserVO> getUsers(PageRequest request) { ... }}</pre>
 *     </li>
 *     <li>设置完整的分页信息：
 *         <pre>{@code pageResponse.setCurrentPage(1); pageResponse.setPageSize(10); pageResponse.setTotal(100);}</pre>
 *     </li>
 * </ul>
 *
 * @param <T> 数据类型
 * @author XuGaoran
 * @since 0.0.1
 */
@Setter
@Getter
public class PageResponse<T> extends MultiResponse<T> {

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

    /**
     * 总页数
     */
    private int totalPage;

    /**
     * 总数
     */
    private int total;

    public static <T> PageResponse<T> of(List<T> datas, int total, int pageSize, int currentPage) {
        PageResponse<T> pageResponse = new PageResponse<>();
        pageResponse.setSuccess(true);
        pageResponse.setDatas(datas);
        pageResponse.setTotal(total);
        pageResponse.setPageSize(pageSize);
        pageResponse.setCurrentPage(currentPage);
        pageResponse.setTotalPage((pageSize + total - 1) / pageSize);
        return pageResponse;
    }
}

