package com.urban.carbon.user.params;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户修改密码使用的参数
 *
 * @author XuGaoran
 */
@Setter
@Getter
public class UserModifiedParam {

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;

}
