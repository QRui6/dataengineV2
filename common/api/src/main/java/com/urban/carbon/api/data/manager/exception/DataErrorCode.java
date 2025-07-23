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
    DATA_NOT_FOUND("DATA_NOT_FOUND", "数据不存在");

    private final String code;

    private final String message;

    DataErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
