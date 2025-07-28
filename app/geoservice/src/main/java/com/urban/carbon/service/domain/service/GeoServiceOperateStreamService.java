package com.urban.carbon.service.domain.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.urban.carbon.api.geoservice.constants.GeoServiceOperateType;
import com.urban.carbon.data.entity.OperateStream;
import com.urban.carbon.service.domain.entity.GeoService;
import com.urban.carbon.service.infrastructure.mapper.GeoServiceOperateStreamMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 流服务
 *
 *
 */
@Service
public class GeoServiceOperateStreamService
        extends ServiceImpl<GeoServiceOperateStreamMapper, OperateStream> {

    /**
     * 创建流记录
     *
     * @param geoService 数据
     * @param loginId 登录用户 ID
     * @param type    操作类型
     * @return  流 ID
     */
    public Long insertStream(GeoService geoService, Long loginId, GeoServiceOperateType type) {
        // 创建流记录
        OperateStream stream = new OperateStream();
        // 通过当前登录用户的 ID 才能获得
        stream.setUserId(loginId);
        // 设置操作时间
        stream.setOperateTime(new Date());
        // 设置操作类型
        stream.setType(type.name());
        // 记录参数
        stream.setParam(JSON.toJSONString(geoService));
        // 成功就返回id，失败就返回null
        return save(stream) ? stream.getId() : null;
    }

    /**
     * 创建流记录
     *
     * @param geoServiceList 数据
     * @param loginId 登录用户 ID
     * @param type    操作类型
     * @return  流 ID
     */
    public Long insertStream(List<GeoService> geoServiceList, Long loginId, GeoServiceOperateType type) {
        // 创建流记录
        OperateStream stream = new OperateStream();
        // 通过当前登录用户的 ID 才能获得
        stream.setUserId(loginId);
        // 设置操作时间
        stream.setOperateTime(new Date());
        // 设置操作类型
        stream.setType(type.name());
        // 记录参数
        stream.setParam(geoServiceList.stream().map(JSON::toJSONString).toList()
                .stream().reduce(String::concat).toString());
        // 成功就返回id，失败就返回null
        return save(stream) ? stream.getId() : null;
    }
}
