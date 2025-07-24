package com.urban.carbon.auth.params;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginParam extends RegisterParam {

    /**
     * 记住我
     */
    private Boolean rememberMe;
}
