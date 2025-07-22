package com.urban.carbon.data.domain.entity;

import com.urban.carbon.api.data.manager.response.data.DataInfo;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface DataConvertor {

    /**
     * 数据源转换器
     */
    DataConvertor INSTANCE = Mappers.getMapper(DataConvertor.class);

    /**
     * 数据源转换
     *
     * @param data 数据源
     * @return 数据源信息
     */
    DataInfo mapToVo(Data data);
}
