package com.urban.carbon.user.domain.service;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.urban.carbon.user.domain.entity.Account;
import com.urban.carbon.user.domain.entity.Role;
import com.urban.carbon.user.infrastructure.mapper.AccountMapper;
import com.urban.carbon.user.infrastructure.mapper.RoleMapper;
import com.urban.carbon.api.user.constants.RoleOperateType;
import com.urban.carbon.api.user.constants.UserPermissionEnum;
import com.urban.carbon.api.user.exception.UserManageErrorCode;
import com.urban.carbon.api.user.exception.UserManageException;
import com.urban.carbon.base.response.MultiResponse;
import com.urban.carbon.base.response.OperateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.StringJoiner;

@Slf4j
@Service
public class RoleService extends ServiceImpl<RoleMapper, Role> {

    /**
     * 账户查询 Mapper
     */
    private final AccountMapper accountMapper;

    /**
     * 操作日志 Mapper
     */
    private final UserOperateStreamService userOperateStreamService;

    public RoleService(UserOperateStreamService userOperateStreamService, AccountMapper accountMapper) {
        this.userOperateStreamService = userOperateStreamService;
        this.accountMapper = accountMapper;
    }

    /**
     * 获取所有角色信息
     *
     * @return 返回角色信息的响应
     * <p>
     * 该方法用于查询系统中所有未删除且处于激活状态的角色信息，并以分页形式返回
     * 它首先创建一个分页对象，然后构建查询条件，最后调用Mapper层进行数据查询
     * 如果查询结果为空，将抛出自定义的UserException异常
     */
    public MultiResponse<Role> getAllRole() {
        // 查询数据库，并将结果封装到分页对象中
        List<Role> roleActive = list(new QueryWrapper<Role>().eq(
                "role_active", 1).eq("deleted", 0));
        // 校验查询结果，如果为空，则抛出异常
        Assert.isTrue(!roleActive.isEmpty(),
                () -> new UserManageException(UserManageErrorCode.ROLE_QUERY_EMPTY));
        // 将查询结果转换为 VO 对象，并构建响应对象返回
        return MultiResponse.of(roleActive);
    }

    /**
     * 创建角色
     *
     * @param roleName 角色名称
     * @param roleDesc 角色描述
     * @param rolePermission 角色权限
     * @param userId 用户ID
     * @return 创建的角色对象
     */
    public OperateResponse<Role> roleCreate(
            String roleName, String roleDesc,
            List<UserPermissionEnum> rolePermission, Long userId) {
        // 创建一个 Role 实体对象，并设置其属性
        Role role = new Role();
        // 默认插入 PERSONAL 权限
        if (rolePermission == null || rolePermission.isEmpty()) {
            throw new UserManageException(UserManageErrorCode.PERMISSION_EMPTY);
        }
        if (!rolePermission.contains(UserPermissionEnum.PERSONAL)) {
            rolePermission.add(UserPermissionEnum.PERSONAL);
        }
        StringJoiner joiner = new StringJoiner(",");
        rolePermission.forEach(entry -> joiner.add(entry.name()));
        String permissions = joiner.toString();
        role.create(roleName, permissions, roleDesc, 1);
        // 插入数据，如果插入失败，则抛出异常
        Assert.isTrue(save(role), () -> new UserManageException(UserManageErrorCode.ROLE_CREATE_FAIL));
        // 将当前操作加入 用户操作记录表，如果记录失败，则抛出异常
        long streamResult = userOperateStreamService.insertStream(
                role, userId, RoleOperateType.CREATE_ROLE);
        Assert.notNull(streamResult, () -> new UserManageException(UserManageErrorCode.ROLE_CREATE_FAIL));
        // 创建并准备返回的响应对象
        OperateResponse<Role> response = new OperateResponse<>();
        response.setSuccess(true);
        response.setData(role);
        // 返回包含操作结果和角色信息的响应对象
        return response;
    }

    /**
     * 更新角色信息
     *
     * @param roleId 角色ID
     * @param roleName 角色名称
     * @param roleDesc 角色描述
     * @param rolePermission 角色权限
     * @param roleActive 角色状态
     * @param userId 用户ID
     * @return 操作结果
     */
    public OperateResponse<Role> modifyRole(
            Long roleId, String roleName, String roleDesc,
            List<UserPermissionEnum> rolePermission, Integer roleActive, Long userId) {
        // 创建一个新的角色对象
        Role role = new Role();
        role.modifyRole(roleId, roleName, roleActive, rolePermission, roleDesc);
        // 确保角色信息更新成功，否则抛出用户异常
        Assert.isTrue(updateById(role), () -> new UserManageException(UserManageErrorCode.ROLE_MODIFY_FAIL));
        // 将当前操作加入 用户操作记录表
        long streamResult = userOperateStreamService.insertStream(
                role, userId, RoleOperateType.MODIFY_ROLE);
        Assert.notNull(streamResult, () -> new UserManageException(UserManageErrorCode.ROLE_MODIFY_FAIL));
        // 记录这一条
        OperateResponse<Role> response = new OperateResponse<>();
        response.setSuccess(true);
        response.setData(role);
        return response;
    }

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     * @param userId 用户ID
     * @return 删除结果
     */
    public OperateResponse<Role> deleteRole(Long roleId, Long userId) {
        // 检查角色是否存在
        Role role = getById(roleId);
        Assert.notNull(role, () -> new UserManageException(UserManageErrorCode.ROLE_NOT_EXIST));
        // 查询是否当前角色是否有未被删除的用户
        List<Account> userList = accountMapper.selectList(
                new QueryWrapper<Account>().eq("role_id", roleId));
        Assert.isTrue(userList.isEmpty(), () -> new UserManageException(UserManageErrorCode.ROLE_DELETE_FAIL));
        OperateResponse<Role> response = new OperateResponse<>();
        // 删除数据
        if (removeById(role)) {
            // 将删除的数据序列化并存入操作表
            long streamResult = userOperateStreamService.insertStream(
                    role, userId, RoleOperateType.DELETE_ROLE);
            Assert.notNull(streamResult, () -> new UserManageException(UserManageErrorCode.ROLE_DELETE_FAIL));
            // 构建响应对象
            response.setSuccess(true);
            response.setData(role);
        } else {
            response.setSuccess(false);
        }
        return response;
    }
}
