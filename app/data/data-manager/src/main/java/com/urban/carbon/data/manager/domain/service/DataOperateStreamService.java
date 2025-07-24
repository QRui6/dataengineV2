package com.urban.carbon.data.manager.domain.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.urban.carbon.api.data.manager.constants.DataOperateType;
import com.urban.carbon.data.manager.domain.entity.Data;
import com.urban.carbon.data.entity.OperateStream;
import com.urban.carbon.data.manager.infrastructure.mapper.OperateStreamMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DataOperateStreamService extends ServiceImpl<OperateStreamMapper, OperateStream> {

    public Long insertStream(Data data, Long userId, DataOperateType type) {
        // 创建流记录
        OperateStream stream = new OperateStream();
        // 通过当前登录用户的 ID 才能获得
        stream.setUserId(userId);
        // 设置操作时间
        stream.setOperateTime(new Date());
        // 设置操作类型
        stream.setType(type.name());
        // 记录参数
        stream.setParam(data.toString());
        // 成功就返回id，失败就返回null
        return save(stream) ? stream.getId() : null;
    }

    public Long insertStream(List<Data> dataList, Long userId, DataOperateType type) {
        // 创建流记录
        OperateStream stream = new OperateStream();
        // 通过当前登录用户的 ID 才能获得
        stream.setUserId(userId);
        // 设置操作时间
        stream.setOperateTime(new Date());
        // 设置操作类型
        stream.setType(type.name());
        // 记录参数
        stream.setParam(dataList.stream()
                .map(data -> data.getId() + ":" + data)
                .toList()
                .stream()
                .reduce(String::concat).toString());
        // 成功就返回id，失败就返回null
        return save(stream) ? stream.getId() : null;
    }
}
