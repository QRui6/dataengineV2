package com.urban.carbon.service.domain.entity;

import com.urban.carbon.api.geoservice.response.data.GeoServiceInfo;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface GeoServiceConvertor {

    /**
     * 静态实例
     */
    GeoServiceConvertor INSTANCE = Mappers.getMapper(GeoServiceConvertor.class);

    /**
     * 转换
     *
     * @param geoService geoService 实体
     * @return GeoServiceInfo
     */
    GeoServiceInfo mapToVo(GeoService geoService);

    /**
     * 批量转换
     *
     * @param geoServices geoService 列表
     * @return GeoServiceInfo 列表
     */
    List<GeoServiceInfo> mapToList(List<GeoService> geoServices);
}
