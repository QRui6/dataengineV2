package com.urban.carbon.api.data.manager.exception;

import com.urban.carbon.base.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum DataErrorCode implements ErrorCode {

    DATA_CREATE_ERROR("DATA_CREATE_ERROR", "数据创建失败"),

    DATA_OPERATE_STREAM_FAIL("DATA_OPERATE_STREAM_FAIL", "数据流操作失败");

    private String code;

    private String message;

    DataErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
