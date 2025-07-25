package com.urban.carbon.data.source.domain.entity.convertor;

import com.urban.carbon.api.data.source.response.data.DataSourceInfo;
import com.urban.carbon.data.source.domain.entity.DataSource;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

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

    /**
     * 数据源列表转换
     *
     * @param records 数据源列表
     * @return 数据源信息列表
     */
    List<DataSourceInfo> mapToList(List<DataSource> records);
}
