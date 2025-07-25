package com.urban.carbon.admin.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.urban.carbon.admin.domain.entity.User;
import com.urban.carbon.admin.domain.entity.convertor.UserConvertor;
import com.urban.carbon.admin.domain.service.UserService;
import com.urban.carbon.admin.params.UserCreateParam;
import com.urban.carbon.admin.params.UserQueryParam;
import com.urban.carbon.admin.params.UserSwitchParam;
import com.urban.carbon.admin.params.UserUpdateParam;
import com.urban.carbon.api.admin.constants.UserStateEnum;
import com.urban.carbon.api.admin.exception.UserErrorCode;
import com.urban.carbon.api.admin.exception.UserException;
import com.urban.carbon.api.admin.request.UserActiveRequest;
import com.urban.carbon.api.admin.response.data.UserInfo;
import com.urban.carbon.base.response.OperateResponse;
import com.urban.carbon.base.response.PageResponse;
import com.urban.carbon.web.util.MultiResultConvertor;
import com.urban.carbon.web.vo.MultiResult;
import com.urban.carbon.web.vo.Result;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/userManage")
public class UserManagerController {

    private final UserService userService;

    public UserManagerController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 创建新用户
     *
     * @param param 用户创建参数
     * @return 创建结果，包含随机生成的密码
     */
    @PostMapping("/create")
    public Result<UserInfo> createUser(@Valid @RequestBody UserCreateParam param) {
        // 验证当前登录用户是否存在
        String loginId = (String) StpUtil.getLoginId();
        if (userService.findById(Long.valueOf(loginId)) == null) {
            throw new UserException(UserErrorCode.USER_NOT_EXIST);
        }

        // 调用服务层创建用户
        OperateResponse<UserInfo> response = userService.createUser(
                param.getName(),
                param.getTelephone(),
                param.getRole(),
                Long.valueOf(loginId));

        // 返回创建结果，包含随机生成的密码
        return Result.success(response.getData());
    }

    /**
     * 查询用户列表
     *
     * @param param 用户查询参数
     * @return 用户列表分页结果
     */
    @GetMapping("/queryAll")
    public MultiResult<UserInfo> queryAllUsers(UserQueryParam param) {

        // 验证当前登录用户是否存在
        String loginId = (String) StpUtil.getLoginId();
        if (userService.findById(Long.valueOf(loginId)) == null) {
            throw new UserException(UserErrorCode.USER_NOT_EXIST);
        }

        // 调用服务层方法查询用户列表，使用新的支持多条件查询的方法
        PageResponse<User> pageResponse = userService.pageQueryUsers(
                param.getUserName(), param.getState(), param.getRole(), param.getPage(), param.getPageSize());

        // 将User实体转换为UserInfo VO
        PageResponse<UserInfo> userInfoPageResponse = PageResponse.of(
                UserConvertor.INSTANCE.mapToVo(pageResponse.getDatas()),
                pageResponse.getTotal(),
                pageResponse.getPageSize(),
                pageResponse.getCurrentPage());
        // 将PageResponse转换为MultiResult
        return MultiResultConvertor.convert(userInfoPageResponse);
    }

    /**
     * 更新用户信息
     *
     * @param param 用户更新参数
     * @return 更新后的用户信息
     */
    @PostMapping("/update")
    public Result<UserInfo> updateUser(@Valid @RequestBody UserUpdateParam param) {
        // 验证当前登录用户是否存在
        String loginId = (String) StpUtil.getLoginId();
        if (userService.findById(Long.valueOf(loginId)) == null) {
            throw new UserException(UserErrorCode.USER_NOT_EXIST);
        }

        // 调用服务层更新用户
        OperateResponse<UserInfo> response = userService.updateUser(
                param.getUserId(), param.getName(), param.getTelephone(),
                param.getRole(), Long.valueOf(loginId));

        // 返回更新结果
        return Result.success(response.getData());
    }

    /**
     * 删除单个用户
     *
     * @param userId 用户ID
     * @return 删除结果
     * @deprecated 推荐使用批量删除接口
     */
    @Deprecated(since = "0.0.2", forRemoval = true)
    @DeleteMapping("/delete")
    public Result<Boolean> deleteUser(@RequestParam Long userId) {
        String loginId = (String) StpUtil.getLoginId();
        if (userService.findById(Long.valueOf(loginId)) == null) {
            throw new UserException(UserErrorCode.USER_NOT_EXIST);
        }
        OperateResponse<UserInfo> response = userService.deleteUser(userId, Long.valueOf(loginId));
        return Result.success(response.getSuccess());
    }

    /**
     * 批量删除用户
     *
     * @param ids 用户ID列表
     * @return 删除结果，包含被删除的用户信息列表
     */
    @DeleteMapping("/batchDelete")
    public Result<List<UserInfo>> batchDeleteUsers(@RequestParam List<Long> ids) {
        // 验证当前登录用户是否存在
        String loginId = (String) StpUtil.getLoginId();
        if (userService.findById(Long.valueOf(loginId)) == null) {
            throw new UserException(UserErrorCode.USER_NOT_EXIST);
        }

        // 调用服务层批量删除用户
        OperateResponse<List<UserInfo>> response = userService.batchDeleteUsers(
                ids, Long.valueOf(loginId));

        // 返回删除结果，包含被删除的用户信息
        return Result.success(response.getData());
    }

    @PostMapping("/active")
    public Result<UserInfo> activeUser(@RequestBody UserSwitchParam param) {
        String loginId = (String) StpUtil.getLoginId();
        UserStateEnum userState = checkUser(loginId, param.getUserId());
        if (userState != UserStateEnum.INIT) {
            return Result.error("USER_STATE_ERROR", "只有待激活（INIT）状态的用户才能激活");
        }
        UserActiveRequest userActiveRequest = new UserActiveRequest();
        userActiveRequest.setUserId(param.getUserId());
        return Result.success(
                userService.active(userActiveRequest, Long.valueOf(loginId)).getData());
    }

    @PostMapping("/freeze")
    public Result<UserInfo> freezeUser(@RequestBody UserSwitchParam param) {
        String loginId = (String) StpUtil.getLoginId();
        UserStateEnum userState = checkUser(loginId, param.getUserId());
        if (userState != UserStateEnum.ACTIVE) {
            return Result.error("USER_STATE_ERROR", "当前用户处于未激活或者冻结状态");
        }
        return Result.success(
                userService.freeze(param.getUserId(), Long.valueOf(loginId)).getData());
    }

    @PostMapping("/unfreeze")
    public Result<UserInfo> unfreezeUser(@RequestBody UserSwitchParam param) {
        String loginId = (String) StpUtil.getLoginId();
        UserStateEnum userState = checkUser(loginId, param.getUserId());
        if (userState != UserStateEnum.FROZEN) {
            return Result.error("USER_STATE_ERROR", "当前用户未处于冻结状态");
        }
        return Result.success(
                userService.unfreeze(param.getUserId(), Long.valueOf(loginId)).getData());
    }

    /**
     * 检查用户状态
     *
     * @param loginId 登录ID，用于验证用户是否存在
     * @param userId  用户ID，用于获取用户状态
     * @return 用户当前状态枚举值
     */
    private UserStateEnum checkUser(String loginId, Long userId) {
        // 验证登录用户是否存在
        if ((userService.findById(Long.valueOf(loginId))) == null) {
            throw new UserException(UserErrorCode.USER_NOT_EXIST);
        }
        // 新增：只有INIT状态才能激活
        User user = userService.findById(userId);
        if (user == null) {
            throw new UserException(UserErrorCode.USER_NOT_EXIST);
        }
        return user.getState();
    }

}
