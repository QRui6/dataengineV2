package com.urban.carbon.api.data.manager.exception;

import com.urban.carbon.base.exception.BizException;
import com.urban.carbon.base.exception.ErrorCode;

public class DataException extends BizException {
    public DataException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DataException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public DataException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public DataException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public DataException(String message, Throwable cause, boolean enableSuppression,
                         boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode);
    }
}
