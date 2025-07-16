package com.urban.carbon.geoserver.exception;

import com.urban.carbon.base.exception.ErrorCode;

/**
 * GeoServer 错误码
 *
 * @author  bjcug
 **/
public enum GeoServerErrorCode implements ErrorCode {
    
    /**
     * GeoServer请求失败
     */
    GEOSERVER_REQUEST_FAILED("GEOSERVER_REQUEST_FAILED", "GeoServer请求失败"),

    /**
     * 工作空间删除失败
     */
    WORKSPACE_DELETE_FAILED("WORKSPACE_DELETE_FAILED", "工作空间删除失败"),

    /**
     * 数据存储删除失败
     */
    DATASTORE_DELETE_FAILED("DATASTORE_DELETE_FAILED", "数据存储删除失败"),

    /**
     * 矢量数据存储创建失败
     */
    DATASTORE_CREATE_FAILED("DATASTORE_CREATE_FAILED", "矢量数据存储创建失败"),

    /**
     * 栅格数据存储创建失败
     */
    COVERAGE_STORE_CREATE_FAILED("COVERAGE_STORE_CREATE_FAILED", "栅格数据存储创建失败"),

    /**
     * 栅格图层创建失败
     */
    COVERAGE_CREATE_FAILED("COVERAGE_CREATE_FAILED", "栅格图层创建失败"),

    /**
     * 矢量图层创建失败
     */
    FEATURE_TYPE_CREATE_FAILED("FEATURE_TYPE_CREATE_FAILED", "矢量图层创建失败");


    private final String code;

    private final String message;

    GeoServerErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}

