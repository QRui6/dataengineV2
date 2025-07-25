package com.urban.carbon.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.urban.carbon.user.domain.entity.Account;
import com.urban.carbon.user.domain.entity.convertor.UserConvertor;
import com.urban.carbon.user.domain.service.UserService;
import com.urban.carbon.user.params.UserModifiedParam;
import com.urban.carbon.api.user.exception.UserErrorCode;
import com.urban.carbon.api.user.exception.UserException;
import com.urban.carbon.api.user.response.data.UserInfo;
import com.urban.carbon.web.vo.Result;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
public class PersonalController {

    /**
     * 用户服务
     */
    private final UserService userService;

    public PersonalController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/getUserInfo")
    public Result<UserInfo> getUserInfo() {
        String userId = (String) StpUtil.getLoginId();
        Account account = userService.findById(Long.valueOf(userId));
        if (account == null) {
            throw new UserException(UserErrorCode.USER_NOT_EXIST);
        }
        return Result.success(UserConvertor.INSTANCE.mapToVo(account));
    }

    /**
     * 修改用户的昵称。
     *
     * @param userModifyParam 包含新昵称的参数
     * @return 修改结果
     * @throws UserException 如果用户不存在
     */
    @PostMapping("/modifyNickName")
    public Result<Boolean> modifyNickName(@Valid @RequestBody UserModifiedParam userModifyParam) {
        String userId = (String) StpUtil.getLoginId();
        Account account = userService.findById(Long.valueOf(userId));
        if (account == null) {
            throw new UserException(UserErrorCode.USER_NOT_EXIST);
        }
        // 这里不存在冗余操作，
        // userService中的findById方法是存在缓存的，本地缓存，还有Redis分布式缓存
        // 所以就算上面的信息存在，这里也需要回到数据库中查一遍，看看是否真的存在。
        Boolean modifyResult = userService.modifyAccount(
                Long.valueOf(userId), account.getNickName(), null,
                null, null).getSuccess();
        if (modifyResult) {
            refreshUserInSession(userId);
        }
        return Result.success(modifyResult);
    }

    /**
     * 修改用户的密码。
     *
     * @param userModifyParam 包含旧密码和新密码的参数
     * @return 修改结果
     * @throws UserException 如果用户不存在或旧密码不正确
     */
    @PostMapping("/modifyPassword")
    public Result<Boolean> modifyPassword(@Valid @RequestBody UserModifiedParam userModifyParam) {
        // 查询用户信息
        String userId = (String) StpUtil.getLoginId();
        Account account = userService.findById(Long.valueOf(userId));
        if (account == null) {
            throw new UserException(UserErrorCode.USER_NOT_EXIST);
        }
        // 检查旧密码是否和原本的一致
        if (!StringUtils.equals(account.getPasswordHash(),
                DigestUtil.md5Hex(userModifyParam.getOldPassword()))) {
            throw new UserException(UserErrorCode.USER_PASSWD_CHECK_FAIL);
        }
        // 这里不存在冗余操作，
        // userService中的findById方法是存在缓存的，本地缓存，还有Redis分布式缓存
        // 所以就算上面的信息存在，这里也需要回到数据库中查一遍，看看是否真的存在。
        Boolean modifyResult = userService.modifyAccount(
                Long.valueOf(userId), null, null,
                userModifyParam.getNewPassword(), null).getSuccess();
        return Result.success(modifyResult);
    }

    /**
     * 修改用户的头像, 每个用户不会记录之前用了什么头像，所以会直接覆盖之前的内容
     *
     * @param file 上传的头像文件
     * @return 头像文件保存路径
     */
    @Deprecated(since = "0.0.2", forRemoval = true)
    @PostMapping("/modifyProfilePhoto")
    public Result<String> modifyProfilePhoto(@RequestParam("file_data") MultipartFile file) {
        // 获取用户ID
//        String userId = (String) StpUtil.getLoginId();
//        if (file.isEmpty()) {
//            throw new UserException(UserErrorCode.USER_UPLOAD_PICTURE_FAIL);
//        }
//        FileUploadVO res = fileService.upload(file, Long.valueOf(userId));
//        if (!res.getUploadSuccess()) {
//            throw new UserException(UserErrorCode.USER_UPLOAD_PICTURE_FAIL);
//        }
//        Boolean result = userService.modifyAccount(
//                Long.valueOf(userId), null, null,
//                null, null).getSuccess();
//        if (!result) {
//            throw new UserException(UserErrorCode.USER_UPLOAD_PICTURE_FAIL);
//        }
//        return Result.success(res.getFilePath());
        return null;
    }

    /**
     * 刷新用户会话中的用户信息。
     *
     * @param userId 用户ID
     */
    private void refreshUserInSession(String userId) {
        Account account = userService.getById(Long.valueOf(userId));
        UserInfo userInfo = UserConvertor.INSTANCE.mapToVo(account);
        StpUtil.getSession().set(userInfo.getUserId().toString(), userInfo);
    }
}
