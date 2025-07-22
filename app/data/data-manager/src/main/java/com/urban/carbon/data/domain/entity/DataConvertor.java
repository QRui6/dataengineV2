package com.urban.carbon.data.domain.entity;

import com.urban.carbon.api.data.manager.response.data.DataInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface DataConvertor {

    DataConvertor INSTANCE = Mappers.getMapper(DataConvertor.class);

    DataInfo mapToVo(Data data);
}
