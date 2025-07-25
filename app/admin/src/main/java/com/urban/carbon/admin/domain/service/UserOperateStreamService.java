package com.urban.carbon.admin.domain.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.urban.carbon.admin.domain.entity.User;
import com.urban.carbon.admin.infrasturcture.mapper.OperateStreamMapper;
import com.urban.carbon.api.admin.constants.UserOperateTypeEnum;
import com.urban.carbon.data.entity.OperateStream;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserOperateStreamService extends ServiceImpl<OperateStreamMapper, OperateStream> {
    public Long insertStream(User user, Long userId, UserOperateTypeEnum type) {
        // 创建流记录
        OperateStream stream = new OperateStream();
        // 通过当前登录用户的 ID 才能获得
        stream.setUserId(userId);
        // 设置操作时间
        stream.setOperateTime(new Date());
        // 设置操作类型
        stream.setType(type.name());
        // 记录参数
        stream.setParam(user.toString());
        // 成功就返回id，失败就返回null
        return save(stream) ? stream.getId() : null;
    }

    public Long insertStream(List<User> userList, Long userId, UserOperateTypeEnum type) {
        // 创建流记录
        OperateStream stream = new OperateStream();
        // 通过当前登录用户的 ID 才能获得
        stream.setUserId(userId);
        // 设置操作时间
        stream.setOperateTime(new Date());
        // 设置操作类型
        stream.setType(type.name());
        // 记录参数

        stream.setParam(userList
                .stream()
                .map(User::toString)
                .toList()
                .stream()
                .reduce((s1, s2) -> s1 + "," + s2)
                .toString());
        // 成功就返回id，失败就返回null
        return save(stream) ? stream.getId() : null;
    }
}
