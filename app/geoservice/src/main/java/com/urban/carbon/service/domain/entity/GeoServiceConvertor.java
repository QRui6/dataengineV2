package com.urban.carbon.service.domain.entity;

import com.urban.carbon.api.geoservice.response.data.GeoServiceInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
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
    @Mapping(source = "geoService.workspace", target = "workspaceName")
    @Mapping(source = "geoService.allowTypes", target = "allowFormatTypes", qualifiedByName = "stringToList")
    @Mapping(source = "geoService.serviceSrs", target = "SRS")
    @Mapping(source = "geoService.serviceProj", target = "projection")
    @Mapping(source = "geoService.serviceUrl", target = "baseUrl")
    GeoServiceInfo mapToVo(GeoService geoService);

    /**
     * 批量转换
     *
     * @param geoServices geoService 列表
     * @return GeoServiceInfo 列表
     */
    @Mapping(source = "geoService.workspace", target = "workspaceName")
    @Mapping(source = "geoService.allowTypes", target = "allowFormatTypes", qualifiedByName = "stringToList")
    @Mapping(source = "geoService.serviceSrs", target = "SRS")
    @Mapping(source = "geoService.serviceProj", target = "projection")
    @Mapping(source = "geoService.serviceUrl", target = "baseUrl")
    List<GeoServiceInfo> mapToList(List<GeoService> geoServices);

    /**
     * 将数据中的String按照要求进行分割
     *
     * @param value 需要处理的数值
     * @return 返回处理好的列表
     */
    @Named(value = "stringToList")
    default List<String> stringToList(String value) {
        if (value == null || value.isEmpty()) {
            return List.of(); // 返回空列表
        }
        return Arrays.asList(value.split(","));
    }
}
