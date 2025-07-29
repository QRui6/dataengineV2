package com.urban.carbon.admin.domain.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.urban.carbon.admin.domain.entity.Role;
import com.urban.carbon.admin.infrastructure.mapper.OperateStreamMapper;
import com.urban.carbon.api.admin.constants.RoleOperateTypeEnum;
import com.urban.carbon.data.entity.OperateStream;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 角色操作流服务
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Service
public class RoleOperateStreamService extends ServiceImpl<OperateStreamMapper, OperateStream> {

    /**
     * 插入角色操作流记录
     *
     * @param role 角色对象，用于记录操作参数
     * @param userId 用户ID，标识操作者
     * @param type 操作类型枚举，标识具体的操作类型
     * @return 返回插入成功的记录ID，插入失败则返回null
     */
    public Long insertStream(Role role, Long userId, RoleOperateTypeEnum type) {
        // 创建流记录
        OperateStream stream = new OperateStream();
        // 通过当前登录用户的 ID 才能获得
        stream.setUserId(userId);
        // 设置操作时间
        stream.setOperateTime(new Date());
        // 设置操作类型
        stream.setType(type.name());
        // 记录参数
        stream.setParam(JSON.toJSONString(role));
        // 成功就返回id，失败就返回null
        return save(stream) ? stream.getId() : null;
    }

}
