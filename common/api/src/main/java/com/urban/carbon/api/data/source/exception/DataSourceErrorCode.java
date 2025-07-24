package com.urban.carbon.api.data.source.exception;

import com.urban.carbon.base.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum DataSourceErrorCode implements ErrorCode {

    /**
     * 数据源不存在
     */
    DATA_SOURCE_NOT_EXIST("DATA_SOURCE_NOT_EXIST", "数据源不存在"),

    /**
     * 数据源创建失败
     */
    DATA_SOURCE_CREATE_FAIL("DATA_SOURCE_CREATE_FAIL", "数据源创建失败"),

    /**
     * 数据源操作流失败
     */
    DATA_SOURCE_OPERATOR_STREAM_FAIL("DATA_SOURCE_OPERATOR_STREAM_FAIL", "数据源操作流失败"),

    /**
     * 查询条件不支持
     */
    QUERY_CONDITION_NOT_SUPPORT("QUERY_CONDITION_NOT_SUPPORT", "查询条件不支持"),

    /**
     * 数据源不存在
     */
    DATA_SOURCE_NOT_EXISTS("DATA_SOURCE_NOT_EXISTS", "数据源不存在"),

    /**
     * 没有权限
     */
    NO_PRIVILEGES("NO_PRIVILEGES", "没有权限"),

    /**
     * 数据源有数据
     */
    DATA_SOURCE_HAS_DATA("DATA_SOURCE_HAS_DATA", "数据源有数据");

    private final String message;

    private final String code;

    DataSourceErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
