package com.urban.carbon.api.admin.request;

import com.urban.carbon.base.request.BaseRequest;
import lombok.*;

/**
 * 用户注册请求
 *
 * @author bjcug
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest extends BaseRequest {

    /**
     * 电话号码
     */
    private String telephone;

    /**
     * 密码 (明文)
     */
    private String password;

}
