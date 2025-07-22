package com.urban.carbon.data.source.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.urban.carbon.data.source.domain.entity.DataSource;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DataSourceMapper extends BaseMapper<DataSource> {

    /**
     * 根据ID查询数据源信息
     *
     * @param dataSourceId 数据源ID
     * @param loginId 登录用户ID
     * @return 数据源信息
     */
    DataSource findById(Long dataSourceId, Long loginId);

    /**
     * 根据名称查询数据源信息
     *
     * @param dataSourceName 数据源名称
     * @param loginId 登录用户ID
     * @return 数据源信息
     */
    DataSource findByName(String dataSourceName, Long loginId);
}
