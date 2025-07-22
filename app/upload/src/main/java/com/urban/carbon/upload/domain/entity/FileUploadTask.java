package com.urban.carbon.upload.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.urban.carbon.data.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 文件上传任务实体类，对应表 file_upload_task。
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Getter
@Setter
@TableName("public.file_upload_task")
public class FileUploadTask extends BaseEntity {

    /**
     * 文件唯一标识
     */
    private String fileId;

    /**
     * 关联 data_info.id
     */
    private Long dataInfoId;

    /**
     * 分片大小（字节）
     */
    private Long chunkSize;

    /**
     * 总分片数
     */
    private Integer totalChunks;

    /**
     * 存储策略（如 MINIO、HDFS）
     */
    private String saveSoft;

    /**
     * 上传状态（0-初始化，1-上传中，2-完成，3-失败）
     */
    private Integer status;

    /**
     * 过期时间
     */
    private Date gmtExpired;

    /**
     * 上传用户ID
     */
    private String createdBy;

    public void createTask() {

    }
}
