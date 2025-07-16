package com.urban.carbon.api.user.response.data;

import com.github.houbb.sensitive.annotation.strategy.SensitiveStrategyPhone;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class UserInfo extends BasicUserInfo {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 手机号(进行脱敏处理)
     */
    @SensitiveStrategyPhone
    private String telephone;

    /**
     * 用户当前的账号状态
     */
    private String state;

    /**
     * 用户角色
     */
    private String roleName;

    /**
     * 用户权限 (从角色表中拿出来的信息)
     */
    private List<String> userPermission;

    /**
     * 注册时间
     */
    private Date gmtCreate;

    /**
     * 上次修改时间
     */
    private Date gmtModified;

    /**
     * 上次登录时间
     */
    private Date lastLoginTime;
}
