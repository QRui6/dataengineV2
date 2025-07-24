package com.urban.carbon.auth.exception;

import com.urban.carbon.base.exception.BizException;
import com.urban.carbon.base.exception.ErrorCode;

/**
 * 认证异常
 *
 * @author bjcug
 */
public class AuthException extends BizException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public AuthException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public AuthException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public AuthException(String message, Throwable cause, boolean enableSuppression,
                         boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode);
    }

}