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
    SERVICE_NOT_FIND("SERVICE_NOT_FIND", "服务不存在"),

    /**
     * 服务更新失败
     */
    SERVICE_UPDATE_FAILED("SERVICE_UPDATE_FAILED", "服务更新失败"),

    /**
     * 服务未启用
     */
    SERVICE_NOT_START("SERVICE_NOT_START", "服务未启用"),

    /**
     * 服务不匹配
     */
    SERVICE_NOT_MATCH("SERVICE_NOT_MATCH", "服务不匹配"),

    /**
     * 数据类型不匹配
     */
    DATA_TYPE_NOT_MATCH("DATA_TYPE_NOT_MATCH", "数据类型不匹配"),

    /**
     * 发布shp数据源失败
     */
    PUBLISH_SHP_DATASTORE_FAIL("PUBLISH_SHP_DATASTORE_FAIL", "发布shp数据源失败"),

    /**
     * 发布shp图层失败
     */
    PUBLISH_SHP_LAYER_FAIL("PUBLISH_SHP_LAYER_FAIL", "发布shp图层失败"),

    /**
     * 发布tif数据源失败
     */
    PUBLISH_TIF_DATASTORE_FAIL("PUBLISH_TIF_DATASTORE_FAIL", "发布tif数据源失败"),

    /**
     * 发布tif图层失败
     */
    PUBLISH_TIF_LAYER_FAIL("PUBLISH_TIF_LAYER_FAIL", "发布tif图层失败");

    private final String code;

    private final String message;

    GeoServiceErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
