package com.urban.carbon.api.data.source.exception;

import com.urban.carbon.base.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum DataSourceErrorCode implements ErrorCode {

    DATA_SOURCE_NOT_EXIST("DATA_SOURCE_NOT_EXIST", "数据源不存在"),
    ;

    private String message;

    private String code;

    DataSourceErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
