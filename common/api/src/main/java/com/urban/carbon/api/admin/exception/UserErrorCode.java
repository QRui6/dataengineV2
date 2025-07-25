package com.urban.carbon.api.admin.exception;

import com.urban.carbon.base.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum UserErrorCode implements ErrorCode {

    /**
     * 当前角色不存在
     */
    USER_NOT_EXIST("USER_NOT_EXIST", "用户不存在"),

    /**
     * 用户创建失败
     */
    USER_CREATE_FAILED("USER_CREATE_FAILED", "用户创建失败"),

    /**
     * 用户状态不能进行操作
     */
    USER_STATUS_CANT_OPERATE("USER_STATUS_CANT_OPERATE", "用户状态不能进行操作"),

    /**
     * 用户名已存在
     */
    NICK_NAME_EXIST("NICK_NAME_EXIST", "用户名已存在"),

    /**
     * 用户操作失败
     */
    USER_OPERATE_FAILED("USER_OPERATE_FAILED", "用户操作失败"),

    /**
     * 旧密码与原有密码不一致
     */
    USER_PASSWD_CHECK_FAIL("USER_PASSWD_CHECK_FAIL", "旧密码与原有密码不一致"),

    /**
     * 用户上传图片失败
     */
    USER_UPLOAD_PICTURE_FAIL("USER_UPLOAD_PICTURE_FAIL", "用户上传图片失败"),

    /**
     * 手机号已存在
     */
    DUPLICATE_TELEPHONE_NUMBER("DUPLICATE_TELEPHONE_NUMBER", "手机号已存在"),

    /**
     * 删除用户失败
     */
    ACCOUNT_DELETE_FAIL("ACCOUNT_DELETE_FAIL", "删除用户失败"),

    /**
     * 用户已激活
     */
    USER_STATUS_IS_ACTIVE("USER_STATUS_IS_ACTIVE", "用户已激活"),

    /**
     * 账户或密码错误
     */
    USER_PASSWORD_ERROR("USER_PASSWORD_ERROR", "账户或密码错误"),

    /**
     * 用户更新失败
     */
    USER_UPDATE_FAILED("USER_UPDATE_FAILED", "用户更新失败");

    private final String code;

    private final String message;

    UserErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
