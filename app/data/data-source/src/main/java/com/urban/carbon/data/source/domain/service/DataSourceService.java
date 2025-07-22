package com.urban.carbon.data.source.domain.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.urban.carbon.data.source.domain.entity.DataSource;
import com.urban.carbon.data.source.infrastructure.mapper.DataSourceMapper;
import org.springframework.stereotype.Service;

@Service
public class DataSourceService extends ServiceImpl<DataSourceMapper, DataSource> {

    /**
     * 数据源查询mapper
     */
    private final DataSourceMapper dataSourceMapper;

    /**
     * 构造函数
     *
     * @param dataSourceMapper 数据源查询mapper
     */
    public DataSourceService(DataSourceMapper dataSourceMapper) {
        this.dataSourceMapper = dataSourceMapper;
    }

    /**
     * 获取数据源信息
     *
     * @param dataSourceId 数据源ID
     * @return 数据源信息
     */
    public DataSource findById(Long dataSourceId, Long loginId) {
        return this.dataSourceMapper.findById(dataSourceId, loginId);
    }

    /**
     * 根据名称获取数据源信息
     *
     * @param dataSourceName 数据源名称
     * @return 数据源信息
     */
    public DataSource findByName(String dataSourceName, Long loginId) {
        return this.dataSourceMapper.findByName(dataSourceName, loginId);
    }
}
