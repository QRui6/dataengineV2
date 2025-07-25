package com.urban.carbon.admin.params;

import lombok.Data;

/**
 * 用户查询参数
 */
@Data
public class UserQueryParam {

    /**
     * 当前页码
     */
    private Integer page = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 角色
     */
    private String role;

    /**
     * 用户状态
     */
    private String state;

    /**
     * 用户名
     */
    private String userName;
}
