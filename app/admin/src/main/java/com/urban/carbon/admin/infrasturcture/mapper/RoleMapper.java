package com.urban.carbon.admin.infrasturcture.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.urban.carbon.admin.domain.entity.Role;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据id查询角色信息
     *
     * @param roleId 角色ID
     * @return 角色信息
     */
    Role findByRoleId(Long roleId);
}
