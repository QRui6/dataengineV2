package com.urban.carbon.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.urban.carbon.api.admin.request.UserModifyRequest;
import com.urban.carbon.api.admin.request.UserQueryRequest;
import com.urban.carbon.api.admin.service.UserManagerFacadeService;
import com.urban.carbon.base.response.OperateResponse;
import com.urban.carbon.api.admin.exception.UserErrorCode;
import com.urban.carbon.api.admin.exception.UserException;
import com.urban.carbon.api.admin.response.data.UserInfo;
import com.urban.carbon.user.domain.entity.Account;
import com.urban.carbon.user.domain.entity.convertor.AccountConvertor;
import com.urban.carbon.user.domain.service.AccountService;
import com.urban.carbon.user.params.UserModifiedParam;
import com.urban.carbon.web.vo.Result;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/user")
public class PersonalController {

    /**
     * 用户服务
     */
    private final AccountService accountService;

    private final UserManagerFacadeService userManagerFacadeService;

    public PersonalController(AccountService accountService,
                              UserManagerFacadeService userManagerFacadeService) {
        this.accountService = accountService;
        this.userManagerFacadeService = userManagerFacadeService;
    }

    @GetMapping("/getUserInfo")
    public Result<UserInfo> getUserInfo() {
        String loginId = (String) StpUtil.getLoginId();
        Account response = accountService.getById(Long.valueOf(loginId));
        return Result.success(AccountConvertor.INSTANCE.mapToVo(response));
    }

    /**
     * 修改用户的昵称。
     *
     * @param param 包含新昵称的参数
     * @return 修改结果
     * @throws UserException 如果用户不存在
     */
    @PostMapping("/modifyNickName")
    public Result<UserInfo> modifyNickName(@Valid @RequestBody UserModifiedParam param) {
        String userId = (String) StpUtil.getLoginId();
        if (param.getNickName().isBlank()) {
            throw new UserException(UserErrorCode.PARAM_ERROR);
        }
        UserModifyRequest request = new UserModifyRequest();
        request.setUserId(Long.valueOf(userId));
        request.setLoginId(Long.valueOf(userId));
        request.setNickName(param.getNickName());
        OperateResponse<UserInfo> response = userManagerFacadeService.modify(request);
        if (response.getSuccess()) {
            refreshUserInSession(userId);
        }
        return Result.success(response.getData());
    }

    /**
     * 修改用户的密码。
     *
     * @param param 包含旧密码和新密码的参数
     * @return 修改结果
     * @throws UserException 如果用户不存在或旧密码不正确
     */
    @PostMapping("/modifyPassword")
    public Result<UserInfo> modifyPassword(@Valid @RequestBody UserModifiedParam param) {
        // 查询用户信息
        String userId = (String) StpUtil.getLoginId();
        if (param.getNewPassword().isBlank() || param.getOldPassword().isBlank()) {
            throw new UserException(UserErrorCode.PARAM_ERROR);
        }
        Account account = accountService.getById(Long.valueOf(userId));
        // 检查旧密码是否和原本的一致
        if (!StringUtils.equals(account.getPasswordHash(),
                DigestUtil.md5Hex(param.getOldPassword()))) {
            throw new UserException(UserErrorCode.USER_PASSWD_CHECK_FAIL);
        }
        UserModifyRequest request = new UserModifyRequest();
        request.setUserId(Long.valueOf(userId));
        request.setLoginId(Long.valueOf(userId));
        request.setPassword(param.getNewPassword());
        OperateResponse<UserInfo> response = userManagerFacadeService.modify(request);
        if (response.getSuccess()) {
            refreshUserInSession(userId);
        }
        return Result.success(response.getData());
    }

    /**
     * 修改用户的头像, 每个用户不会记录之前用了什么头像，所以会直接覆盖之前的内容
     *
     * @param file 上传的头像文件
     * @return 头像文件保存路径
     */
    @Deprecated(since = "0.0.2", forRemoval = true)
    @PostMapping("/modifyProfilePhoto")
    public Result<String> modifyProfilePhoto(@RequestParam("file") MultipartFile file)
            throws IOException {
        // 获取用户ID
        String userId = (String) StpUtil.getLoginId();
        if (file.isEmpty()) {
            throw new UserException("File is Empty", UserErrorCode.USER_UPLOAD_PICTURE_FAIL);
        }
        if (file.getSize() > 1024 * 1024 * 2) {
            throw new UserException("Photo can't be large than 2 MB!",
                    UserErrorCode.USER_UPLOAD_PICTURE_FAIL);
        }
        UserModifyRequest request = new UserModifyRequest();
        request.setUserId(Long.valueOf(userId));
        request.setLoginId(Long.valueOf(userId));
        request.setPhotoInputStream(file.getInputStream());
        OperateResponse<UserInfo> response = userManagerFacadeService.modify(request);
        if (!response.getSuccess()) {
            throw new UserException(UserErrorCode.USER_UPLOAD_PICTURE_FAIL);
        }
        return Result.success(response.getData().getProfilePhotoUrl());
    }

    /**
     * 刷新用户会话中的用户信息。
     *
     * @param userId 用户ID
     */
    private void refreshUserInSession(String userId) {
        Account account = accountService.getById(Long.valueOf(userId));
        UserInfo userInfo = AccountConvertor.INSTANCE.mapToVo(account);
        StpUtil.getSession().set(userInfo.getUserId().toString(), userInfo);
    }
}
