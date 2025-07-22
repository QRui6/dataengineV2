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
     * 分片状态（0-未上传，1-上传中，2-失败，3-成功）
     */
    private Integer status;

    /**
     * 上传时间
     */
    private Date uploadTime;

    /**
     * 分片的 MD5 校验值
     */
    private String md5;

    /**
     * 分片存储路径
     */
    private String serverPath;

    /**
     * 上传失败重试次数
     */
    private Short retryCount;
}
