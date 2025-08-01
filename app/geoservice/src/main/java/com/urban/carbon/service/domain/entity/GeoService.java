package com.urban.carbon.service.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.urban.carbon.data.entity.BaseEntity;
import lombok.*;

import java.util.StringJoiner;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@TableName("service")
public class GeoService extends BaseEntity {

    /**
     * 数据源ID
     */
    private Long dsId;

    /**
     * 数据ID
     */
    private Long dataId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 服务是否启动，0表示未启动，1表示已启动
     */
    private Integer started;

    /**
     * 服务坐标系，默认为4326
     */
    private Integer serviceSrs;

    /**
     * 服务投影，默认为32662
     */
    private Integer serviceProj;

    /**
     * 工作空间名称
     */
    private String workspace;

    /**
     * 数据存储名称
     */
    private String storeName;

    /**
     * 图层名称
     */
    private String layerName;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务URL，这里是随机生成的 32 位随机数
     */
    private String serviceUrl;

    /**
     * 允许访问的类型，默认为 all
     */
    private String allowTypes;

    /**
     * 服务描述
     */
    private String serviceDesc;

    /**
     * 服务基础 URL
     */
    @TableField(exist = false)
    private String serviceBaseURL;

    public void createService(Long dsId, Long dataId, Long userId, Integer started,
                              Integer serviceSrs, Integer serviceProj, String workspace,
                              String storeName, String layerName, String serviceName,
                              String serviceUrl, String allowTypes, String serviceDesc,
                              String baseUrl) {
        this.dsId = dsId;
        this.dataId = dataId;
        this.userId = userId;
        this.started = started;
        this.serviceSrs = serviceSrs;
        this.serviceProj = serviceProj;
        this.workspace = workspace;
        this.storeName = storeName;
        this.layerName = layerName;
        this.serviceName = serviceName;
        this.serviceUrl = serviceUrl;
        this.allowTypes = allowTypes;
        this.serviceDesc = serviceDesc;
        this.serviceBaseURL = baseUrl;
    }
}
