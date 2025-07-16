package com.urban.carbon.api.upload.constants;

public enum SaveSoftType {
    /**
     * 保存在本地文件系统
     */
    LOCAL,

    /**
     * Mongodb 存储
     */
    MINIO,

    /**
     * 保存在HDFS文件系统
     */
    HDFS
}
