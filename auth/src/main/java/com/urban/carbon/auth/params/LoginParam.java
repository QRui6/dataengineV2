package com.urban.carbon.auth.params;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginParam extends RegisterParam {

    /**
     * 记住我
     */
    @NotNull
    private Boolean rememberMe;
}
