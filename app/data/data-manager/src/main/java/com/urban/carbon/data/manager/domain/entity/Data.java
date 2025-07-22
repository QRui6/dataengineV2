package com.urban.carbon.data.manager.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.urban.carbon.api.data.manager.constants.FileUploadStatus;
import com.urban.carbon.api.data.manager.constants.SaveSoftType;
import com.urban.carbon.data.entity.BaseEntity;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@TableName("public.data_info")
public class Data extends BaseEntity {

    /**
     * 文件ID
     */
    private String fileId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 关联的数据源ID
     */
    private Long dataSourceId;

    /**
     * 数据名称
     */
    private String name;

    /**
     * 数据描述
     */
    private String description;

    /**
     * 数据类型
     */
    private String type;

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
    private String status;

    /**
     * 数据源名称
     */
    @TableField(exist = false)
    private String dataSourceName;

    public void initCreate(String fileId, Long userId, Long dataSourceId, String name,
                           String description, String dataType, Long chunkSize, Integer totalChunks,
                           SaveSoftType saveSoft, FileUploadStatus status, String dataSourceName) {
    this.fileId = fileId;
    this.userId = userId;
    this.dataSourceId = dataSourceId;
    this.name = name;
    this.description = description;
    this.type = dataType;
    this.chunkSize = chunkSize;
    this.totalChunks = totalChunks;
    this.saveSoft = saveSoft.name();
    this.status = status.name();
    this.dataSourceName = dataSourceName;
}

}
