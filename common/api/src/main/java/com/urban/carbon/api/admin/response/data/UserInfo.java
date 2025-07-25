package com.urban.carbon.api.admin.response.data;

import com.urban.carbon.api.admin.constants.UserStateEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class UserInfo extends BasicUserInfo {

    /**
     * 手机号(进行脱敏处理)
     */
    private String telephone;

    /**
     * 用户当前的账号状态
     *
     * @see UserStateEnum
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

    /**
     * 明文密码（仅在创建用户时返回）
     */
    private String plainPassword;
}
