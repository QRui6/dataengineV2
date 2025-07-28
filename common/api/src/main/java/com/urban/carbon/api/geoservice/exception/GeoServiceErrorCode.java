package com.urban.carbon.api.geoservice.exception;

import com.urban.carbon.base.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum GeoServiceErrorCode implements ErrorCode {

    /**
     * Geoserver地址错误
     */
    GEOSERVER_URL_ERROR("GEOSERVER_URL_ERROR", "Geoserver地址错误"),

    /**
     * 文件不存在
     */
    FILE_NOT_FOUND("FILE_NOT_FOUND", "文件不存在"),

    /**
     * 服务操作流失败
     */
    SERVICE_OPERATE_STREAM_FAIL("SERVICE_OPERATE_STREAM_FAIL", "服务操作流失败"),

    /**
     * 查询参数错误
     */
    QUERY_PARAMS_ERROR("QUERY_PARAMS_ERROR", "查询参数错误"),

    /**
     * 服务不存在
     */
    SERVICE_NOT_FIND("SERVICE_NOT_FIND", "服务不存在");

    private final String code;

    private final String message;

    GeoServiceErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
