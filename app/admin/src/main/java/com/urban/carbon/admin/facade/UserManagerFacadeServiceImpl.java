package com.urban.carbon.admin.facade;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.urban.carbon.admin.domain.entity.User;
import com.urban.carbon.admin.domain.entity.convertor.UserConvertor;
import com.urban.carbon.admin.domain.service.UserService;
import com.urban.carbon.api.admin.exception.UserErrorCode;
import com.urban.carbon.api.admin.exception.UserException;
import com.urban.carbon.api.admin.request.UserActiveRequest;
import com.urban.carbon.api.admin.request.UserModifyRequest;
import com.urban.carbon.api.admin.request.UserQueryRequest;
import com.urban.carbon.api.admin.request.UserRegisterRequest;
import com.urban.carbon.api.admin.request.condition.UserIdQueryCondition;
import com.urban.carbon.api.admin.request.condition.UserTPQueryCondition;
import com.urban.carbon.api.admin.request.condition.UserTelephoneQueryCondition;
import com.urban.carbon.api.admin.response.data.UserInfo;
import com.urban.carbon.api.admin.service.UserManagerFacadeService;
import com.urban.carbon.base.response.OperateResponse;
import com.urban.carbon.base.response.QueryResponse;
import com.urban.carbon.file.strategy.FileStrategyFactory;
import com.urban.carbon.rpc.facade.Facade;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.context.annotation.Primary;

import java.util.Date;

/**
 * 用户管理功能
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@DubboService(version = "1.0.0")
@Primary
public class UserManagerFacadeServiceImpl implements UserManagerFacadeService {

    /**
     * 用户服务
     */
    private final UserService userService;

    /**
     * 构造函数
     *
     * @param userService 用户服务
     */
    public UserManagerFacadeServiceImpl(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取用户信息
     *
     * @param request 请求参数, 可能是手机号, 用户id
     * @return 返回封装好的用户查询结果类
     */
    @Facade
    @Override
    public QueryResponse<UserInfo> query(UserQueryRequest request) {
        User user = switch (request.getCondition()) {
            case UserIdQueryCondition userIdQueryCondition:
                yield userService.findById(userIdQueryCondition.getLoginId());
            case UserTelephoneQueryCondition userTelephoneQueryCondition:
                yield userService.findByTelephone(userTelephoneQueryCondition.getTelephone());
            case UserTPQueryCondition userTPQueryCondition:
                yield userService.findByTelephoneAndPass(
                        userTPQueryCondition.getTelephone(), userTPQueryCondition.getPassword());
            default:
                throw new UnsupportedOperationException(request.getCondition() + "'' is not supported");
        };

        QueryResponse<UserInfo> response = new QueryResponse<>();
        response.setSuccess(true);
        UserInfo userInfo = UserConvertor.INSTANCE.mapToVo(user);
        response.setData(userInfo);
        return response;
    }

    /**
     * 注册方法
     *
     * @param userRegisterRequest 用户注册请求, 通过电话, 密码, 角色进行注册
     * @return 返回用户操作的结果
     */
    @Facade
    @Override
    public OperateResponse<UserInfo> register(UserRegisterRequest userRegisterRequest, Long loginId) {
        // 调用注册方法
        return userService.register(userRegisterRequest, loginId);
    }

    /**
     * 刷新用户最后登录时间
     *
     * @param userId 用户ID，用于定位需要更新的用户记录
     * @return Boolean 更新成功返回true，失败时抛出异常
     * @throws UserException 当用户更新失败时抛出此异常
     */
    @Override
    public Boolean refreshLastLoginTime(Long userId) {
        // 创建用户对象并设置当前时间为最后登录时间
        User user = new User();
        user.setLastLoginTime(new Date());

        try {
            // 根据用户ID更新用户的最后登录时间
            userService.update(user, new QueryWrapper<User>().eq("id", userId));
            return true;
        } catch (Exception e) {
            throw new UserException(UserErrorCode.USER_UPDATE_FAILED);
        }
    }


    /**
     * 用户信息修改
     *
     * @param userModifyRequest 用户修改信息请求
     * @return 返回用户操作的结果
     */
    @Facade
    @Override
    public OperateResponse<UserInfo> modify(UserModifyRequest userModifyRequest) {
        return userService.modify(userModifyRequest);
    }

    /**
     * 冻结
     *
     * @param userId 用户id
     * @return 用户操作记录
     */
    @Override
    @Facade
    public OperateResponse<UserInfo> freeze(Long userId, Long loginId) {
        return userService.freeze(userId, loginId);
    }

    /**
     * 解冻
     *
     * @param userId 用户id
     * @return 用户操作记录
     */
    @Override
    @Facade
    public OperateResponse<UserInfo> unfreeze(Long userId, Long loginId) {
        return userService.unfreeze(userId, loginId);
    }

    /**
     * 用户激活
     *
     * @param userActiveRequest 激活请求
     * @return 用户操作记录
     */
    @Override
    @Facade
    public OperateResponse<UserInfo> active(UserActiveRequest userActiveRequest, Long loginId) {
        return userService.active(userActiveRequest, loginId);
    }
}
