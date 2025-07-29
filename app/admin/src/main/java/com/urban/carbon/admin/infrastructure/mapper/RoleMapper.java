package com.urban.carbon.admin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.urban.carbon.admin.domain.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色Mapper
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据id查询角色信息
     *
     * @param roleId 角色ID
     * @return 角色信息
     */
    Role findByRoleId(Long roleId);

    /**
     * 根据角色名称查询角色信息
     *
     * @param roleName 角色名称
     * @return 角色信息
     */
    Role findByRoleName(String roleName);
}
