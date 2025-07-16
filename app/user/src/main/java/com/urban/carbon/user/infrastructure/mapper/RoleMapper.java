package com.urban.carbon.user.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.urban.carbon.user.domain.entity.Role;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据角色名称查询角色
     *
     * @param roleName 角色名称
     * @return 角色
     */
    Role findByRoleName(String roleName);
}
