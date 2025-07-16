package com.urban.carbon.geoserver.exception;

import com.urban.carbon.base.exception.ErrorCode;
import com.urban.carbon.base.exception.SystemException;

/**
 * GeoServer异常
 *
 * @author bjcug
 */
public class GeoServerException extends SystemException {

    public GeoServerException(ErrorCode errorCode) {
        super(errorCode);
    }

    public GeoServerException(String message, Throwable cause, boolean enableSuppression,
                              boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace, errorCode);
    }

    public GeoServerException(Throwable cause, ErrorCode errorCode) {
        super(cause, errorCode);
    }

    public GeoServerException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

    public GeoServerException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}

