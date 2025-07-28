package com.urban.carbon.api.data.manager.response.data;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DataInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据id
     */
    private Long id;

    /**
     * 文件id
     */
    private String fileId;

    /**
     * 数据源id
     */
    private Long dataSourceId;

    /**
     * 数据源名称
     */
    private String dataSourceName;

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
    private String dataType;

    /**
     * 总分块数
     */
    private Integer totalChunks;

    /**
     * 分块大小
     */
    private Long chunkSize;

    /**
     * 文件存储路径
     */
    private String filePath;

    /**
     * 存储软件名称
     */
    private String saveSoft;

    /**
     * 上传的状态
     */
    private String status;
}
