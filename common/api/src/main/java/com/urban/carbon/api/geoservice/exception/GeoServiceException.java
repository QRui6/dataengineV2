package com.urban.carbon.api.geoservice.exception;

import com.urban.carbon.base.exception.BizException;
import com.urban.carbon.base.exception.ErrorCode;

public class GeoServiceException extends BizException {
    public GeoServiceException(ErrorCode errorCode) {
        super(errorCode);
    }

    public GeoServiceException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public GeoServiceException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public GeoServiceException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public GeoServiceException(String message, Throwable cause, boolean enableSuppression,
                               boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode);
    }
}
