package com.urban.carbon.api.data.manager.constants;

public enum FileUploadStatus {

    /**
     * 初始化
     */
    INITIALIZED,

    /**
     * 上传中
     */
    UPLOADING,

    /**
     * 等待合并
     */
    WAITING_FOR_MERGE,

    /**
     * 合并中
     */
    MERGING,

    /**
     * 上传完成
     */
    COMPLETED,

    /**
     * 上传失败
     */
    FAILED,

    /**
     * 重试
     */
    NEED_RETRY,

    /**
     * 取消
     */
    CANCELED
}
