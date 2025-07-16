package com.urban.carbon.user.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.urban.carbon.api.user.constants.UserPermissionEnum;
import com.urban.carbon.data.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 角色实体
 *
 * @author XuGaoran
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
     * @param roleName 角色名称
     * @param permissions 角色权限
     * @param roleDesc 角色描述
     * @param roleActive 角色活跃状态
     */
    public void create(String roleName, String permissions,
                       String roleDesc, Integer roleActive) {
        // 设置角色名称
        this.setRoleName(roleName);
        // 设置角色活跃状态
        this.setRoleActive(roleActive);
        // 设置角色权限
        this.setRolePermission(permissions);
        // 设置角色描述
        this.setRoleDesc(roleDesc);
        // 初始化删除状态为未删除
        this.setDeleted(0);
        // 初始化乐观锁版本号
        this.setLockVersion(0);
        // 设置创建时间为当前时间
        this.setGmtCreate(new Date());
        // 设置修改时间为当前时间
        this.setGmtModified(new Date());
    }

    /**
     * 修改角色信息的方法
     *
     * @param id 角色的唯一标识
     * @param roleName 角色名称
     * @param roleActive 角色活跃状态，null表示默认活跃
     * @param rolePermission 角色权限列表，为空表示无特殊权限
     * @param roleDesc 角色描述
     */
    public void modifyRole(Long id, String roleName, Integer roleActive,
                           List<UserPermissionEnum> rolePermission, String roleDesc) {
        // 设置角色的唯一标识
        this.setId(id);
        // 设置角色的名称
        this.setRoleName(roleName);
        // 如果角色活跃状态为null，则默认设置为活跃（值为1）
        if (roleActive == null) {
            this.setRoleActive(1);
        }
        // 如果角色权限列表不为空，则将其以逗号分隔的字符串形式保存
        this.setRolePermission(String.join(",",
                rolePermission.stream().map(UserPermissionEnum::name).toList()));
        // 设置角色的描述信息
        this.setRoleDesc(roleDesc);
    }
}
