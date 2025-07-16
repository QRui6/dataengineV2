package com.urban.carbon.api.user.exception;

import com.urban.carbon.base.exception.BizException;
import com.urban.carbon.base.exception.ErrorCode;

public class UserManageException extends BizException {

    public UserManageException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserManageException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public UserManageException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public UserManageException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public UserManageException(String message, Throwable cause, boolean enableSuppression,
                               boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode);
    }
}
