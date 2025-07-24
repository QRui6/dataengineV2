package com.urban.carbon.auth.controller;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;

import com.urban.carbon.api.user.request.UserQueryRequest;
import com.urban.carbon.api.user.request.UserRegisterRequest;
import com.urban.carbon.api.user.response.data.UserInfo;
import com.urban.carbon.api.user.service.UserFacadeService;
import com.urban.carbon.auth.params.LoginParam;
import com.urban.carbon.auth.params.RegisterParam;
import com.urban.carbon.auth.vo.LoginVO;
import com.urban.carbon.base.response.OperateResponse;
import com.urban.carbon.base.response.QueryResponse;
import com.urban.carbon.web.vo.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;


/**
 * 认证相关接口, 主要包含注册与登录两个方法
 *
 * @author bjcug
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @DubboReference(version = "1.0.0")
    private UserFacadeService userFacadeService;

    /**
     * 默认登录超时时间：7天
     */
    private static final Integer DEFAULT_LOGIN_SESSION_TIMEOUT = 60 * 60 * 24 * 7;

    /**
     * 用户注册方法
     *
     * @param registerParam 前端传来的请求参数
     * @return 返回是否注册成功
     * @author bjcug
     */
    @PostMapping("/register")
    public Result<Boolean> register(@Valid @RequestBody RegisterParam registerParam) {
        OperateResponse<UserInfo> registerResult = userRegister(registerParam);
        // 在用户的 Facade 里面完成密码加密, 时间操作, 等任务, 随后将数据入库
        if (registerResult.getSuccess()) {
            return Result.success(true);
        }
        // 错误就返回对应的错误码
        return Result.error(registerResult.getResponseCode(), registerResult.getResponseMessage());
    }

    /**
     * 登录方法, 需要注意的是, 我们需要判断用户到底是登录还是注册,
     * 所以这里需要设计一套判断的方法
     *
     * @param loginParam 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginParam loginParam) {
        // 查询用户信息
        UserQueryRequest userQueryRequest = new UserQueryRequest(
                loginParam.getTelephone(), loginParam.getPassword());
        QueryResponse<UserInfo> userQueryResponse = userFacadeService.query(
                userQueryRequest);
        UserInfo userInfo = userQueryResponse.getData();
        if (userInfo == null) {
            // 需要注册
            OperateResponse<UserInfo> response = userRegister(loginParam);
            if (response.getSuccess()) {
                userQueryResponse = userFacadeService.query(userQueryRequest);
                userInfo = userQueryResponse.getData();
                return getLoginVOResult(loginParam, userInfo);
            }
            return Result.error(response.getResponseCode(), response.getResponseMessage());
        } else {
            // 登录
            return getLoginVOResult(loginParam, userInfo);
        }
    }

    /**
     * 登出方法, 这里登出并没有将redis里面的缓存清除，
     * 这里只是将cookie清除掉了
     *
     * @return 返回成功，这里不会返回 false，同时也没有发现报错
     */
    @PostMapping("/logout")
    public Result<Boolean> logout() {
        StpUtil.logout();
        return Result.success(true);
    }

    /**
     * 测试方法，尝试auth模块是否成功启动
     *
     * @return "test"
     */
    @RequestMapping("test")
    public String test() {
        return "test";
    }

    /**
     * 将注册方法抽离成一个独立的方法
     *
     * @param registerParam 注册参数，但是可以将loginParam作为参数传入
     * @return 返回注册的结果
     */
    private OperateResponse<UserInfo> userRegister(RegisterParam registerParam) {
        // 先创建用户注册的请求对象
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setTelephone(registerParam.getTelephone());
        userRegisterRequest.setPassword(registerParam.getPassword());
        // 在用户的 Facade 里面完成密码加密, 时间操作, 等任务, 随后将数据入库, 这里0表示不进行记录
        return userFacadeService.register(userRegisterRequest, 0L);
    }

    /**
     * 抽离登录方法
     *
     * @param loginParam 登录参数
     * @param userInfo   用户信息
     * @return 返回 Result 的结果
     */
    private Result<LoginVO> getLoginVOResult(LoginParam loginParam, UserInfo userInfo) {
        StpUtil.login(userInfo.getUserId(), new SaLoginModel().setIsLastingCookie(loginParam.getRememberMe())
                .setTimeout(DEFAULT_LOGIN_SESSION_TIMEOUT));
        StpUtil.getSession().set(userInfo.getUserId().toString(), userInfo);
        Boolean ignored = userFacadeService.refreshLastLoginTime(userInfo.getUserId());
        return Result.success(new LoginVO(userInfo));
    }

}

