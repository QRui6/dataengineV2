package com.urban.carbon.user.domain.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.urban.carbon.user.domain.entity.Account;
import com.urban.carbon.user.domain.entity.Role;
import com.urban.carbon.user.infrastructure.mapper.OperateStreamMapper;
import com.urban.carbon.api.user.constants.RoleOperateType;
import com.urban.carbon.api.user.constants.UserOperateTypeEnum;
import com.urban.carbon.data.entity.OperateStream;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserOperateStreamService extends ServiceImpl<OperateStreamMapper, OperateStream> {

    public Long insertStream(Account account, UserOperateTypeEnum type) {
        return insertStreamInner(account.getId(), type.name(), JSON.toJSONString(account));
    }

    public Long insertStream(Role role, Long userId, RoleOperateType type) {
        return insertStreamInner(userId, type.name(), JSON.toJSONString(role));
    }

    private Long insertStreamInner(Long userId, String type, String role) {
        // 创建流记录
        OperateStream stream = new OperateStream();
        // 通过当前登录用户的 ID 才能获得
        stream.setUserId(userId);
        // 设置操作时间
        stream.setOperateTime(new Date());
        // 设置操作类型
        stream.setType(type);
        // 记录参数
        stream.setParam(role);
        // 成功就返回id，失败就返回null
        return save(stream) ? stream.getId() : null;
    }
}