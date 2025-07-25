package com.urban.carbon.api.admin.request.condition;

import com.urban.carbon.base.request.QueryCondition;
import lombok.*;

import java.io.Serial;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserTPQueryCondition implements QueryCondition {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户手机号
     */
    private String telephone;

    /**
     * 用户密码
     */
    private String password;
}
