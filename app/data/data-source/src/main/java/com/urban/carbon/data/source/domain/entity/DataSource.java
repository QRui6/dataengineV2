package com.urban.carbon.data.source.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.urban.carbon.data.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("public.data_source")
public class DataSource extends BaseEntity {
    /**
     * 数据源名称
     */
    private String dsName;

    /**
     * 创建数据源的用户ID
     */
    private Long dsUserId;

    /**
     * 数据源描述
     */
    private String dsDesc;

    /**
     * 创建用户
     * @param dsName 数据源名称
     * @param dsDesc 数据源描述
     * @param loginId 创建用户的ID
     */
    public void create(String dsName, String dsDesc, Long loginId) {
        this.setDsUserId(loginId);
        this.setDsName(dsName);
        this.setDsDesc(dsDesc);
    }
}
