package com.urban.carbon.data.source.domain.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.urban.carbon.api.data.source.constants.DataSourceOperateType;
import com.urban.carbon.data.entity.OperateStream;
import com.urban.carbon.data.source.domain.entity.DataSource;
import com.urban.carbon.data.source.infrastructure.mapper.DataSourceOperateStreamMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DataSourceOperateStreamService
        extends ServiceImpl<DataSourceOperateStreamMapper, OperateStream> {

    public Long insertStream(DataSource dataSource, Long userId,
                             DataSourceOperateType type) {
        // 创建流记录
        OperateStream stream = new OperateStream();
        // 通过当前登录用户的 ID 才能获得
        stream.setUserId(userId);
        // 设置操作时间
        stream.setOperateTime(new Date());
        // 设置操作类型
        stream.setType(type.name());
        // 记录参数
        stream.setParam(dataSource.toString());
        // 成功就返回id，失败就返回null
        return save(stream) ? stream.getId() : null;
    }

    public Long insertStream(List<DataSource> dataSources, Long userId,
                             DataSourceOperateType type) {
        // 创建流记录
        OperateStream stream = new OperateStream();
        // 通过当前登录用户的 ID 才能获得
        stream.setUserId(userId);
        // 设置操作时间
        stream.setOperateTime(new Date());
        // 设置操作类型
        stream.setType(type.name());
        // 记录参数
        stream.setParam(dataSources.stream()
                .map(JSON::toJSONString)
                .toList()
                .stream()
                .reduce(String::concat)
                .toString());
        // 成功就返回id，失败就返回null
        return save(stream) ? stream.getId() : null;
    }
}
