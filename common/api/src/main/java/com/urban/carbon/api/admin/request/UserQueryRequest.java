package com.urban.carbon.api.admin.request;

import com.urban.carbon.api.admin.request.condition.UserIdQueryCondition;
import com.urban.carbon.api.admin.request.condition.UserTPQueryCondition;
import com.urban.carbon.api.admin.request.condition.UserTelephoneQueryCondition;
import com.urban.carbon.base.request.BaseRequest;
import com.urban.carbon.base.request.QueryCondition;
import lombok.*;

import java.io.Serial;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserQueryRequest extends BaseRequest {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用于查询的条件
     */
    private QueryCondition condition;

    public UserQueryRequest(Long loginId) {
        UserIdQueryCondition condition = new UserIdQueryCondition();
        condition.setLoginId(loginId);
        this.condition = condition;
    }

    public UserQueryRequest(String telephone) {
        UserTelephoneQueryCondition condition = new UserTelephoneQueryCondition();
        condition.setTelephone(telephone);
        this.condition = condition;
    }

    public UserQueryRequest(String telephone, String password) {
        UserTPQueryCondition condition = new UserTPQueryCondition();
        condition.setTelephone(telephone);
        condition.setPassword(password);
        this.condition = condition;
    }

}
