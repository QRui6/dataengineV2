package com.urban.carbon.upload.domain.entity;

import com.urban.carbon.api.upload.constants.SaveSoftType;
import com.urban.carbon.api.upload.request.UploadInitRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadCache {

    /**
     * 上传的数据名称
     */
    private String dataName;

    /**
     * 数据描述
     */
    private String dataDesc;

    /**
     * 分块大小
     */
    private Long chunkSize;

    /**
     * 总分块数
     */
    private Long totalChunks;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 保存方式
     */
    private SaveSoftType saveSoft;

    /**
     * 前端提供的hash值
     */
    private String provideHash;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 创建缓存的函数
     * 该函数用于初始化上传文件的缓存信息，根据上传请求的相关数据设置缓存的各个属性
     *
     * @param dataName    数据的名称
     * @param dataDesc    数据的描述
     * @param chunkSize   文件分块大小，单位为字节，用于指示每个文件块的大小
     * @param totalChunks 总文件块数量，表示完成上传所需的所有分块数目
     * @param fileSize    文件总大小，单位为字节，用于记录上传文件的总大小
     */
    public void createCache(String dataName, String dataDesc, Long chunkSize,
                            Long totalChunks, Long fileSize, SaveSoftType saveSoft) {
        // 设置缓存的名称
        this.dataName = dataName;
        // 设置缓存的描述信息
        this.dataDesc = dataDesc;
        // 设置文件分块大小
        this.chunkSize = chunkSize;
        // 设置总文件块数量
        this.totalChunks = totalChunks;
        // 设置文件总大小
        this.fileSize = fileSize;
        // 设置保存方式
        this.saveSoft = saveSoft;
    }
}
