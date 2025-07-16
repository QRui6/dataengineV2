package com.urban.carbon.api.user.exception;

import com.urban.carbon.base.exception.ErrorCode;

public enum UserManageErrorCode implements ErrorCode {

    /**
     * 可用角色为空
     */
    ROLE_QUERY_EMPTY("ROLE_QUERY_EMPTY", "可用角色为空"),

    /**
     * 角色创建失败
     */
    ROLE_CREATE_FAIL("ROLE_CREATE_FAIL", "角色创建失败"),

    /**
     * 权限列表为空
     */
    PERMISSION_EMPTY("PERMISSION_EMPTY", "权限列表为空"),

    /**
     * 角色修改失败
     */
    ROLE_MODIFY_FAIL("ROLE_MODIFY_FAIL", "角色修改失败"),

    /**
     * 角色不存在
     */
    ROLE_NOT_EXIST("ROLE_NOT_EXIST", "角色不存在"),

    /**
     * 角色删除失败
     */
    ROLE_DELETE_FAIL("ROLE_DELETE_FAIL", "角色删除失败");

    private final String code;

    private final String message;

    UserManageErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
