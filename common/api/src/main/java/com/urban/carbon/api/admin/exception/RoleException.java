package com.urban.carbon.api.admin.exception;

import com.urban.carbon.base.exception.BizException;
import com.urban.carbon.base.exception.ErrorCode;

public class RoleException extends BizException {

    public RoleException(ErrorCode errorCode) {
        super(errorCode);
    }

    public RoleException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public RoleException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public RoleException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public RoleException(String message, Throwable cause, boolean enableSuppression,
                         boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode);
    }
}
