package com.urban.carbon.gateway.auth;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.urban.carbon.api.user.response.data.UserInfo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 获取权限的实现类
 *
 * @author bjcug
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 获取Session, 并从session中取出用户的信息
        UserInfo userInfo = (UserInfo) StpUtil.getSessionByLoginId(loginId).get((String) loginId);
        // 取出权限字符串, 使用;进行分割
        List<String> permissionList = userInfo.getUserPermission();
        // 只有当用户状态为ACTIVE时才添加状态权限
        if ("ACTIVE".equals(userInfo.getState())) {
            permissionList.add(userInfo.getState());
        }
        // 检查用户的权限列表并完成权限
        return permissionList;
    }

    /**
     * 验证角色 (目前并不依靠角色来进行权限控制)
     *
     * @param loginId   账号id
     * @param loginType 账号类型
     * @return 角色列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        UserInfo userInfo = (UserInfo) StpUtil.getSessionByLoginId(loginId).get((String) loginId);
        return List.of(userInfo.getRoleName());
    }

}
