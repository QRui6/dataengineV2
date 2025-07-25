package com.urban.carbon.admin.infrasturcture.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.urban.carbon.admin.domain.entity.User;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据角色id查询用户
     *
     * @param roleId 角色id
     * @return 用户列表
     */
    List<User> findByRoleId(Long roleId);

    /**
     * 根据id查询用户
     *
     * @param id id
     * @return 返回查询到的结果
     */
    User findById(long id);

    /**
     * 判断数据库中是否存在该昵称
     *
     * @param nickName 昵称
     * @return true表示存在，false表示不存在
     */
    User findByNickname(@NotNull String nickName);

    /**
     * 判断数据库中是否存在该电话号码
     *
     * @param telephone 电话号码
     * @return true表示存在，false表示不存在
     */
    User findByTelephone(@NotNull String telephone);

    /**
     * 通过 用户名 与 加密之后的密码 对 用户 进行查询
     *
     * @param telephone    电话号码
     * @param passwordHash 加密之后的密码
     * @return 返回查询出来的用户信息
     */
    User findByTelephoneAndPass(String telephone, String passwordHash);

    /**
     * 分页查询用户列表（包含角色权限信息）
     *
     * @param page     分页参数
     * @param userName 用户名
     * @param state    状态
     * @param role     角色
     * @return 分页结果
     */
    IPage<User> pageQueryUsersWithRole(IPage<User> page,
                                       @Param("userName") String userName,
                                       @Param("state") String state,
                                       @Param("role") String role);
}
