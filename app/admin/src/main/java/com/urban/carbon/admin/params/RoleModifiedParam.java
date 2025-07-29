package com.urban.carbon.admin.params;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 角色修改参数
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Getter
@Setter
public class RoleModifiedParam {
    /**
     * 角色ID
     */
    private Long id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色权限  <code>UserPermissionEnum</code>
     * UserPermissionEnum.PERSONAL.name()
     * [ xx, xx, xx, xx ]
     * xx,xx,xx,xx
     */
    private List<String> rolePermission;

    /**
     * 角色是否激活
     */
    private Integer roleActive;

    /**
     * 角色描述
     */
    private String roleDesc;
}
