package com.urban.carbon.api.data.source.exception;

import com.urban.carbon.base.exception.BizException;
import com.urban.carbon.base.exception.ErrorCode;

public class DataSourceException extends BizException {

    public DataSourceException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DataSourceException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public DataSourceException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public DataSourceException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public DataSourceException(String message, Throwable cause, boolean enableSuppression,
                               boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode);
    }
}
