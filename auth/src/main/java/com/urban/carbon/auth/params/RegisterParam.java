package com.urban.carbon.auth.params;

import com.urban.carbon.base.validator.IsMobile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterParam {

    /**
     * 手机号
     */
    @IsMobile
    @NotBlank
    private String telephone;

    /**
     * 密码
     */
    @NotBlank
    private String password;

}