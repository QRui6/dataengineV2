package com.urban.carbon.admin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.urban.carbon.api.admin.constants.UserPermissionEnum;
import com.urban.carbon.data.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.StringJoiner;

/**
 * 角色实体
 *
 * @author bjcug
 */
@Getter
@Setter
@TableName(value = "public.roles")
public class Role extends BaseEntity {

    /**
     * 角色的名称
     */
    private String roleName;

    /**
     * 用户的权限
     */
    private String rolePermission;

    /**
     * 对于角色的描述
     */
    private String roleDesc;

    /**
     * 是否激活
     */
    private Integer roleActive;

    /**
     * 创建角色信息
     *
     * @param roleName       角色名称
     * @param rolePermission 角色权限
     * @param roleDesc       角色描述
     * @param roleActive     角色活跃状态
     */
    public void create(String roleName, List<String> rolePermission,
                       String roleDesc, Integer roleActive) {
        // 设置角色名称
        this.setRoleName(roleName);
        // 设置角色活跃状态
        this.setRoleActive(roleActive);
        // 设置角色权限
        // 默认插入 PERSONAL 权限
        if (rolePermission == null || rolePermission.isEmpty()) {
            rolePermission = List.of(UserPermissionEnum.PERSONAL.name());
        } else if (!rolePermission.contains(UserPermissionEnum.PERSONAL.name())) {
            rolePermission.add(UserPermissionEnum.PERSONAL.name());
        }
        StringJoiner joiner = new StringJoiner(",");
        for (String permission : rolePermission) {
            joiner.add(permission);
        }
        this.setRolePermission(joiner.toString());
        // 设置角色描述
        this.setRoleDesc(roleDesc);
    }

    /**
     * 修改角色信息的方法
     *
     * @param roleName       角色名称
     * @param roleActive     角色活跃状态，null表示默认活跃
     * @param rolePermission 角色权限列表，为空表示无特殊权限
     * @param roleDesc       角色描述
     */
    public void modifyRole(String roleName, Integer roleActive,
                           List<String> rolePermission, String roleDesc) {
        // 设置角色的名称
        this.setRoleName(roleName);
        // 如果角色活跃状态为null，则默认设置为活跃（值为1）
        if (roleActive == null) {
            this.setRoleActive(1);
        }
        // 如果角色权限列表不为空，则将其以逗号分隔的字符串形式保存
        if (rolePermission != null && !rolePermission.isEmpty()) {
            this.setRolePermission(String.join(",", rolePermission));
        }
        // 设置角色的描述信息
        this.setRoleDesc(roleDesc);
    }
}