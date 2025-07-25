package com.urban.carbon.api.admin.service;

import com.urban.carbon.api.admin.request.UserActiveRequest;
import com.urban.carbon.api.admin.request.UserModifyRequest;
import com.urban.carbon.api.admin.request.UserQueryRequest;
import com.urban.carbon.api.admin.request.UserRegisterRequest;
import com.urban.carbon.api.admin.response.data.UserInfo;
import com.urban.carbon.base.response.OperateResponse;
import com.urban.carbon.base.response.QueryResponse;

/**
 * 对用户信息进行修改的时候调用的Facade服务接口
 *
 * @author bjcug
 */
public interface UserManagerFacadeService {
    /**
     * 查询单个用户信息
     *
     * @param request 请求参数, 可能是手机号, 用户id
     * @return 返回单个用户查询结果
     */
    QueryResponse<UserInfo> query(UserQueryRequest request);

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return 返回用户模块操作的通用返回
     */
    OperateResponse<UserInfo> register(UserRegisterRequest userRegisterRequest, Long loginId);

    /**
     * 用户信息修改
     *
     * @param userModifyRequest 用户修改信息请求
     * @return 返回用户模块操作的通用返回
     */
    OperateResponse<UserInfo> modify(UserModifyRequest userModifyRequest);

    /**
     * 设置一个快速设置最后一次登录时间的接口
     *
     * @param userId 用户ID
     * @return 返回是否操作成功
     */
    Boolean refreshLastLoginTime(Long userId);

    /**
     * 用户冻结
     *
     * @param userId 用户id
     * @return 返回操作结果
     */
    OperateResponse<UserInfo> freeze(Long userId, Long loginId);

    /**
     * 用户解冻
     *
     * @param userId 用户id
     * @return 返回操作结果
     */
    OperateResponse<UserInfo> unfreeze(Long userId, Long loginId);

    /**
     * 用户激活
     *
     * @param userActiveRequest 激活请求
     * @return 返回用户模块操作的通用返回
     */
    OperateResponse<UserInfo> active(UserActiveRequest userActiveRequest, Long loginId);
}
