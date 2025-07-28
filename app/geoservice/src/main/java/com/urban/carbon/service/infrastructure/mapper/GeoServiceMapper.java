package com.urban.carbon.service.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.urban.carbon.service.domain.entity.GeoService;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GeoServiceMapper extends BaseMapper<GeoService> {

    /**
     * 批量查询
     * @param serviceIds Service id 列表
     * @param loginId 登录用户ID
     * @return 查询结果
     */
    List<GeoService> findByLoginId(List<Long> serviceIds, Long loginId);

    /**
     * 根据ID查询
     * @param serviceId Service id
     * @param loginId 登录用户ID
     * @return 查询结果
     */
    GeoService findById(Long serviceId, Long loginId);
}
