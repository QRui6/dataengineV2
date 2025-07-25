package com.urban.carbon.api.admin.exception;

import com.urban.carbon.base.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum RoleErrorCode implements ErrorCode {

    /**
     * 角色查询失败
     */
    ROLE_QUERY_FAIL("ROLE_QUERY_FAIL", "角色查询失败"),

    /**
     * 角色创建失败
     */
    ROLE_CREATE_FAIL("ROLE_CREATE_FAIL", "角色创建失败"),

    /**
     * 角色操作记录失败
     */
    ROLE_OPERATOR_STREAM_FAIL("ROLE_OPERATOR_STREAM_FAIL", "角色操作记录失败"),

    /**
     * 角色不存在
     */
    ROLE_NOT_EXIST("ROLE_NOT_EXIST", "角色不存在"),

    /**
     * 角色修改失败
     */
    ROLE_MODIFY_FAIL("ROLE_MODIFY_FAIL", "角色修改失败"),

    /**
     * 角色已关联用户
     */
    ROLE_ASSOCIATED_WITH_USER("ROLE_ASSOCIATED_WITH_USER", "角色已关联用户");

    private final String code;

    private final String message;

    RoleErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
