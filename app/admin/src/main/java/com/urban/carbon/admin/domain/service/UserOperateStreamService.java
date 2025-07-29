package com.urban.carbon.admin.domain.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.urban.carbon.admin.domain.entity.User;
import com.urban.carbon.admin.infrastructure.mapper.OperateStreamMapper;
import com.urban.carbon.api.admin.constants.UserOperateTypeEnum;
import com.urban.carbon.data.entity.OperateStream;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 用户操作流服务
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Service
public class UserOperateStreamService extends ServiceImpl<OperateStreamMapper, OperateStream> {

    /**
     * 插入用户操作流记录
     *
     * @param user   用户对象，用于记录操作参数
     * @param userId 用户ID，标识操作用户
     * @param type   操作类型枚举
     * @return 成功返回流记录ID，失败返回null
     */
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
        stream.setParam(JSON.toJSONString(user));
        // 成功就返回id，失败就返回null
        return save(stream) ? stream.getId() : null;
    }


    /**
     * 插入用户操作流记录
     *
     * @param userList 用户列表参数
     * @param userId   当前操作用户ID
     * @param type     操作类型枚举
     * @return 成功返回流记录ID，失败返回null
     */
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
                .map(JSON::toJSONString)
                .toList()
                .stream()
                .reduce((s1, s2) -> s1 + "," + s2)
                .toString());
        // 成功就返回id，失败就返回null
        return save(stream) ? stream.getId() : null;
    }

}
