package com.urban.carbon.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
     * 数据类型（0-文件，1-表格）
     */
    private Integer type;

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
     * 数据源名称
     */
    @TableField(exist = false)
    private String dataSourceName;
}
