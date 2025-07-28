package com.urban.carbon.gateway.auth;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.urban.carbon.api.admin.constants.UserPermissionEnum;
import com.urban.carbon.api.admin.constants.UserStateEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SaTokenConfiguration {

    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截地址
                .addInclude("/**")
                // 开放地址
                .addExclude("/favicon.ico", "/api/auth/login")
                // 鉴权方法：每次访问进入
                .setAuth(obj -> {
                    SaRouter.match("/api/auth/login", r -> {
                    });
                    // 登录校验 -- 拦截所有路由,
                    // 1、排除/auth/login 用于开放登录与注册。2、排除/first 用于开放首页展示。
                    SaRouter.match("/api/userManage/**", "/api/service/**", "/api/data/**", "/api/user/**",
                            "/api/role/**")
                            .notMatch("/api/auth/**", "/api/first/**").check(
                                    r -> {
                                        // 验证是否是登录的状态
                                        StpUtil.checkLogin();
                                        // 验证是否是激活的账户
                                        StpUtil.checkPermission(UserStateEnum.ACTIVE.name());
                                    });
                    // 权限认证 -- 用户管理、服务管理、数据源管理
                    // 对于管理用户
                    SaRouter.match("/api/userManage/**", "/api/role/**")
                            .check(r -> StpUtil.checkPermission(UserPermissionEnum.USER_MANAGER.name()));
                    // 对于服务管理者的请求, 同时要求账号处于激活状态才可以使用
                    SaRouter.match("/api/service/**",
                            r -> StpUtil.checkPermission(UserPermissionEnum.SERVICE_MANAGER.name()));
                    // 对于数据源管理者的请求, 同时要求账号处于激活状态才可以使用
                    SaRouter.match("/api/data/**",
                            r -> StpUtil.checkPermission(UserPermissionEnum.DS_MANAGER.name()));
                })
                // 异常处理方法：每次setAuth函数出现异常时进入
                .setError(this::getSaResult);
    }

    private SaResult getSaResult(Throwable throwable) {
        switch (throwable) {
            // 没有登录导致报错
            case NotLoginException ignored:
                log.error("Please login first");
                return SaResult.error("Please login first");
            // 没有对应的前端无法进行对应操作
            case NotPermissionException notPermissionException:
                if (UserPermissionEnum.USER_MANAGER.name().equals(notPermissionException.getPermission())) {
                    log.error("Don't have user manager permission");
                    return SaResult.error("Don't have user manager permission");
                } else if (UserPermissionEnum.SERVICE_MANAGER.name().equals(notPermissionException.getPermission())) {
                    log.error("Don't have service manager permission");
                    return SaResult.error("Don't have service manager permission");
                } else if (UserPermissionEnum.DS_MANAGER.name().equals(notPermissionException.getPermission())) {
                    log.error("Don't have DataSource manager permission");
                    return SaResult.error("Don't have DataSource manager permission");
                }
                log.error("error when check permission.");
                return SaResult.error("error when check permission.");
            // 默认情况下直接返回抛出的异常中的错误信息
            default:
                return SaResult.error(throwable.getMessage());
        }
    }
}
