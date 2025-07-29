package com.urban.carbon.admin.domain.service;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.urban.carbon.admin.domain.entity.Role;
import com.urban.carbon.admin.domain.entity.User;
import com.urban.carbon.admin.domain.entity.convertor.RoleConvertor;
import com.urban.carbon.admin.infrastructure.mapper.RoleMapper;
import com.urban.carbon.admin.infrastructure.mapper.UserMapper;
import com.urban.carbon.api.admin.constants.RoleOperateTypeEnum;
import com.urban.carbon.api.admin.exception.RoleErrorCode;
import com.urban.carbon.api.admin.exception.RoleException;
import com.urban.carbon.api.admin.response.data.RoleInfo;
import com.urban.carbon.base.response.MultiResponse;
import com.urban.carbon.base.response.OperateResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * RoleService类提供了角色相关的业务逻辑处理
 * 该类继承自ServiceImpl<RoleMapper, Role>，实现了对角色数据的基本操作
 * 通过@Service注解标识为Spring的服务层组件
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Service
public class RoleService extends ServiceImpl<RoleMapper, Role> {

    /**
     * 角色操作流服务实例，用于处理角色相关的流式操作
     */
    private final RoleOperateStreamService roleOperateStreamService;

    /**
     * 角色数据映射器，用于角色数据的持久化操作
     */
    private final RoleMapper roleMapper;

    /**
     * 用户数据映射器，用于用户数据的持久化操作
     */
    private final UserMapper userMapper;

    /**
     * 构造函数，初始化RoleService实例
     *
     * @param roleOperateStreamService 角色操作流服务实例
     * @param roleMapper               角色数据映射器
     * @param userMapper               用户数据映射器
     */
    public RoleService(RoleOperateStreamService roleOperateStreamService, RoleMapper roleMapper,
                       UserMapper userMapper) {
        this.roleOperateStreamService = roleOperateStreamService;
        this.roleMapper = roleMapper;
        this.userMapper = userMapper;
    }


    /**
     * 获取所有角色信息
     *
     * @return 返回角色信息的分页响应
     * <p>
     * 该方法用于查询系统中所有未删除且处于激活状态的角色信息，并以分页形式返回
     * 它首先创建一个分页对象，然后构建查询条件，最后调用 Mapper层 进行数据查询
     * 如果查询结果为空，将抛出自定义的 RoleException 异常
     */
    public MultiResponse<RoleInfo> getAllRole() {
        // 查询数据库，并将结果封装到分页对象中
        List<Role> roleActive = list(new QueryWrapper<Role>()
                .eq("role_active", 1)
                .eq("deleted", 0));
        // 校验查询结果，如果为空，则抛出异常
        Assert.isTrue(!roleActive.isEmpty(),
                () -> new RoleException(RoleErrorCode.ROLE_QUERY_FAIL));
        // 将查询结果转换为 VO 对象，并构建响应对象返回
        return MultiResponse.of(RoleConvertor.INSTANCE.listMapToVo(roleActive));
    }

    /**
     * 创建角色
     * <p>
     * 此方法用于在系统中创建一个新的角色，包括角色名称、权限、描述和状态
     * 它首先创建一个 Role 对象，设置其属性，然后将其插入数据库
     * 同时，此方法还会记录角色创建的操作信息
     *
     * @param rolePermission 角色权限
     * @param roleName       角色名称
     * @param roleDesc       角色描述
     * @param roleActive     角色状态
     * @param loginId        登录用户 ID
     * @return 返回一个包含操作结果和角色信息的响应对象
     */
    @Transactional
    public OperateResponse<RoleInfo> roleCreate(List<String> rolePermission, String roleName, String roleDesc,
                                                Integer roleActive, Long loginId) {
        // 创建一个 Role 实体对象，并设置其属性
        Role role = new Role();
        role.create(roleName, rolePermission, roleDesc, roleActive);
        // 插入数据，如果插入失败，则抛出异常
        Assert.isTrue(save(role), () -> new RoleException(RoleErrorCode.ROLE_CREATE_FAIL));
        // 将当前操作加入 用户操作记录表，如果记录失败，则抛出异常
        long streamResult = roleOperateStreamService.insertStream(
                role, loginId, RoleOperateTypeEnum.CREATE_ROLE);
        Assert.notNull(streamResult, () -> new RoleException(RoleErrorCode.ROLE_OPERATOR_STREAM_FAIL));
        // 创建并准备返回的响应对象
        OperateResponse<RoleInfo> response = new OperateResponse<>();
        response.setSuccess(true);
        response.setData(RoleConvertor.INSTANCE.mapToVo(role));
        // 返回包含操作结果和角色信息的响应对象
        return response;
    }

    /**
     * 修改角色信息
     *
     * @param roleId         角色 ID
     * @param roleName       角色名称
     * @param roleActive     角色状态
     * @param rolePermission 角色权限
     * @param roleDesc       角色描述
     * @param loginId        登录用户 ID
     * @return 返回操作响应对象，包含操作是否成功和角色信息
     * <p>
     * 此方法首先根据传入的参数获取角色信息，然后更新角色的权限字符串
     * 更新成功后，记录操作日志，最后返回操作结果和更新后的角色信息
     */
    @Transactional
    public OperateResponse<RoleInfo> modifyRole(Long roleId, String roleName, Integer roleActive,
                                                List<String> rolePermission, String roleDesc, Long loginId) {
        Role role = roleMapper.findByRoleId(roleId);
        Assert.notNull(role, () -> new RoleException(RoleErrorCode.ROLE_NOT_EXIST));
        // 创建一个新的角色对象
        role.modifyRole(roleName, roleActive, rolePermission, roleDesc);
        // 确保角色信息更新成功，否则抛出用户异常
        Assert.isTrue(updateById(role), () -> new RoleException(RoleErrorCode.ROLE_MODIFY_FAIL));
        // 将当前操作加入 用户操作记录表
        long streamResult = roleOperateStreamService.insertStream(
                role, loginId, RoleOperateTypeEnum.MODIFY_ROLE);
        Assert.notNull(streamResult, () -> new RoleException(RoleErrorCode.ROLE_OPERATOR_STREAM_FAIL));
        // 记录这一条
        OperateResponse<RoleInfo> response = new OperateResponse<>();
        response.setSuccess(true);
        response.setData(RoleConvertor.INSTANCE.mapToVo(role));
        return response;
    }

    /**
     * 删除角色
     *
     * @param roleId  角色ID
     * @param loginId 登录用户ID
     * @return 操作响应结果，包含删除是否成功的信息
     */
    @Transactional
    public OperateResponse<Boolean> deleteRole(Long roleId, Long loginId) {
        // 检查角色是否存在
        Role role = getById(roleId);
        Assert.notNull(role, () -> new RoleException(RoleErrorCode.ROLE_NOT_EXIST));

        // 查询是否当前角色是否有未被删除的用户
        List<User> userList = userMapper.findByRoleId(roleId);
        Assert.isTrue(userList.isEmpty(), () -> new RoleException(RoleErrorCode.ROLE_ASSOCIATED_WITH_USER));

        OperateResponse<Boolean> response = new OperateResponse<>();
        // 删除数据
        if (removeById(role)) {
            // 将删除的数据序列化并存入操作表
            long streamResult = roleOperateStreamService.insertStream(
                    role, loginId, RoleOperateTypeEnum.DELETE_ROLE);
            Assert.notNull(streamResult, () -> new RoleException(RoleErrorCode.ROLE_OPERATOR_STREAM_FAIL));
            // 构建响应对象
            response.setSuccess(true);
        } else {
            response.setSuccess(false);
        }
        return response;
    }

}
