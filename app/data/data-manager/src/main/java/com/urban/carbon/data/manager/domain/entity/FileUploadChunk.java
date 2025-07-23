package com.urban.carbon.data.manager.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.urban.carbon.data.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 文件分片上传记录实体类，对应表 file_upload_chunk。
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Getter
@Setter
@TableName("public.file_upload_chunk")
public class FileUploadChunk extends BaseEntity {

    /**
     * 文件唯一标识
     */
    private String fileId;

    /**
     * 分片索引（从 0 开始）
     */
    private Integer chunkIndex;

    /**
     * 分片大小（可能小于 chunk_size）
     */
    private Long chunkSize;

    /**
     * 分片状态
     */
    private String status;

    /**
     * 分片的 MD5 校验值
     */
    private String md5;

    /**
     * 上传失败重试次数
     */
    private Integer retryCount;

    /**
     * 创建分片上传记录
     *
     * @param fileId 文件唯一标识
     * @param chunkIndex 分片索引（从 0 开始）
     * @param chunkSize 分片大小（可能小于 chunk_size )
     * @param status 分片状态
     * @param hashMD5 分片的 MD5 校验值
     * @param retryCount 上传失败重试次数
     */
    public void recordChunk(String fileId, Integer chunkIndex, Long chunkSize, String status,
                            String hashMD5, Integer retryCount) {
        this.fileId = fileId;
        this.chunkIndex = chunkIndex;
        this.chunkSize = chunkSize;
        this.status = status;
        this.md5 = hashMD5;
        this.retryCount = retryCount;
    }
}
