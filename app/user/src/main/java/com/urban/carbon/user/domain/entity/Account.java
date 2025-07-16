package com.urban.carbon.user.domain.entity;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.houbb.sensitive.annotation.strategy.SensitiveStrategyPhone;
import com.urban.carbon.api.user.constants.UserStateEnum;
import com.urban.carbon.data.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@TableName("public.users")
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
    @SensitiveStrategyPhone
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

    /**
     * 普通用户注册
     *
     * @param telephone 电话
     * @param nickName  昵称
     * @param password  密码
     */
    public void register(String telephone, String nickName, String password,
                         Long roleId, String roleName) {
        // 设置电话
        this.setTelephone(telephone);
        // 设置昵称
        this.setNickName(nickName);
        // 对明文密码进行加密
        this.setPasswordHash(DigestUtil.md5Hex(password));
        // 设置初始化相关内容
        this.setState(UserStateEnum.INIT);
        // 设置角色id
        this.setRoleId(roleId);
        // 设置角色
        this.setRoleName(roleName);
    }

    /**
     * 判断当前用户的状态，如果是INIT、ACTIVE状态则返回true、否则返回false
     *
     * @return true/false
     */
    public boolean canModifyInfo() {
        return state == UserStateEnum.INIT || state == UserStateEnum.ACTIVE;
    }

    /**
     * 修改用户信息
     *
     * @param nickName 昵称
     * @param telephone 手机号
     * @param password 密码
     * @param profilePhotoUrl 头像
     */
    public void modifyInfo(String nickName, String telephone, String password, String profilePhotoUrl) {
        if (nickName != null) {
            this.nickName = nickName;
        }
        if (telephone != null) {
            this.telephone = telephone;
        }
        if (password != null) {
            this.passwordHash = DigestUtil.md5Hex(password);
        }
        if (profilePhotoUrl != null) {
            this.profilePhotoUrl = profilePhotoUrl;
        }
    }
}
