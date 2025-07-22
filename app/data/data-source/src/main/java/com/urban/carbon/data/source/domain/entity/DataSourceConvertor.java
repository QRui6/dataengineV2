package com.urban.carbon.data.source.domain.entity;

import com.urban.carbon.api.data.source.response.data.DataSourceInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface DataSourceConvertor {

    /**
     * 数据源转换
     */
    DataSourceConvertor INSTANCE = Mappers.getMapper(DataSourceConvertor.class);

    /**
     * 数据源转换
     *
     * @param dataSource 数据源
     * @return 数据源信息
     */
    DataSourceInfo mapToVo(DataSource dataSource);
}
