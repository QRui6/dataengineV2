package com.urban.carbon.personal.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.urban.carbon.api.admin.constants.UserStateEnum;
import com.urban.carbon.data.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 用户实体
 *
 * @author bjcug
 */
@Getter
@Setter
@TableName(value = "public.users")
public class Account extends BaseEntity {

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 密码哈希
     */
    private String passwordHash;

    /**
     * 账户状态
     */
    private UserStateEnum state;

    /**
     * 电话号码
     */
    private String telephone;

    /**
     * 最后一次登录时间
     */
    private Date lastLoginTime;

    /**
     * 头像存放地址
     */
    private String profilePhotoUrl;

    /**
     * 角色的id
     */
    private Long roleId;

    /**
     * 角色的名称
     */
    private String roleName;

    /**
     * 角色的具体权限
     */
    @TableField(exist = false)
    private String userPermission;

}
