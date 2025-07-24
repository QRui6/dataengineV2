package com.urban.carbon.auth.params;

import com.urban.carbon.base.validator.IsMobile;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterParam {

    /**
     * 手机号
     */
    @IsMobile
    private String telephone;

    /**
     * 密码
     */
    private String password;

}