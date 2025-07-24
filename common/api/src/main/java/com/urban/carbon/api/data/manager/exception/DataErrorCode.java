package com.urban.carbon.api.data.manager.exception;

import com.urban.carbon.base.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum DataErrorCode implements ErrorCode {

    /**
     * 数据创建失败
     */
    DATA_CREATE_ERROR("DATA_CREATE_ERROR", "数据创建失败"),

    /**
     * 数据流操作失败
     */
    DATA_OPERATE_STREAM_FAIL("DATA_OPERATE_STREAM_FAIL", "数据流操作失败"),

    /**
     * 数据不存在
     */
    DATA_NOT_FOUND("DATA_NOT_FOUND", "数据不存在"),

    /**
     * 数据已上传
     */
    DATA_ALREADY_UPLOADED("DATA_ALREADY_UPLOADED", "数据已上传"),

    /**
     * 查询条件不支持
     */
    QUERY_CONDITION_NOT_SUPPORT("QUERY_CONDITION_NOT_SUPPORT", "查询条件不支持"),

    /**
     * 数据不存在
     */
    DATA_NOT_EXISTS("DATA_NOT_EXISTS", "数据不存在"),

    /**
     * 数据删除失败
     */
    DATA_DELETED_FAILED("DATA_DELETED_FAILED", "数据删除失败");

    private final String code;

    private final String message;

    DataErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
