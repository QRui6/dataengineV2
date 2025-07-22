package com.urban.carbon.file.exception;

import com.urban.carbon.base.exception.ErrorCode;

/**
 * FileErrorCode 枚举类用于定义文件相关操作的错误码。
 *
 * <p>包含各种文件操作失败的错误信息，如上传、下载、删除、创建目录等。</p>
 *
 * <p>使用方法示例：</p>
 * <pre>
 * // 抛出文件上传失败异常
 * throw new BizException(FileErrorCode.FILE_UPLOAD_FAILED);
 *
 * // 抛出带有自定义信息的文件不存在异常
 * throw new BizException("无法找到指定的文件", FileErrorCode.FILE_NOT_FOUND);
 *
 * // 在捕获异常时使用错误码
 * try {
 *     // 文件操作逻辑
 * } catch (SomeException e) {
 *     throw new BizException(FileErrorCode.FILE_DELETE_FAILED, e);
 * }
 * </pre>
 *
 * @author XuGaoran
 * @since 0.0.2
 */
public enum FileErrorCode implements ErrorCode {
    /**
     * 文件上传相关错误
     */
    FILE_UPLOAD_FAILED("FILE_UPLOAD_FAILED", "文件上传失败"),
    CHUNK_SIZE_ERROR("CHUNK_SIZE_ERROR", "分块大小错误"),
    CHUNK_LOCK_FAILED("CHUNK_LOCK_FAILED", "分块锁失败"),
    INVALID_CHUNK_INDEX("INVALID_CHUNK_INDEX", "无效的分块索引"),
    CHUNK_NOT_MATCH("CHUNK_NOT_MATCH", "分块内容不匹配"),
    CHUNK_UPLOAD_FAILED("CHUNK_UPLOAD_FAILED", "分块上传失败"),
    UPLOAD_NOT_COMPLETE("UPLOAD_NOT_COMPLETE", "上传未完成"),
    STATUS_GET_FAILED("STATUS_GET_FAILED", "获取上传状态失败"),
    MD5_TOOL_INIT_ERROR("MD5_TOOL_INIT_ERROR", "MD5工具初始化错误"),

    /**
     * 文件下载相关错误
     */
    FILE_DOWNLOAD_FAILED("FILE_DOWNLOAD_FAILED", "文件下载失败"),
    HDFS_DOWNLOAD_FAILED("HDFS_DOWNLOAD_FAILED", "HDFS文件下载失败"),

    /**
     * 文件删除相关错误
     */
    FILE_DELETE_FAILED("FILE_DELETE_FAILED", "文件删除失败"),
    HDFS_DELETE_FAILED("HDFS_DELETE_FAILED", "HDFS文件删除失败"),

    /**
     * HDFS 相关错误
     */
    HDFS_CONFIG_ERROR("HDFS_CONFIG_ERROR", "HDFS配置错误"),
    HDFS_CONNECTION_FAILED("HDFS_CONNECTION_FAILED", "HDFS连接失败"),
    HDFS_CREATE_DIR_FAILED("HDFS_CREATE_DIR_FAILED", "HDFS创建目录失败"),
    HDFS_UPLOAD_FAILED("HDFS_UPLOAD_FAILED", "HDFS文件上传失败"),
    HDFS_MERGE_FAILED("HDFS_MERGE_FAILED", "HDFS文件合并失败"),

    /**
     * MinIO 相关错误
     */
    MINIO_CONNECTION_FAILED("MINIO_CONNECTION_FAILED", "MinIO连接失败"),

    /**
     * 通用文件操作错误
     */
    DIRECTORY_CREATE_FAILED("DIRECTORY_CREATE_FAILED", "目录创建失败"),

    /**
     * 文件不存在
     */
    FILE_NOT_FOUND("FILE_NOT_FOUND", "文件不存在"),

    /**
     * MinIO 文件下载失败
     */
    MINIO_DOWNLOAD_FAILED("MINIO_DOWNLOAD_FAILED", "MinIO文件下载失败"),

    /**
     * 上传初始化失败
     */
    UPLOAD_INIT_ERROR("UPLOAD_INIT_ERROR", "上传初始化失败");

    private final String code;

    private final String message;

    FileErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

