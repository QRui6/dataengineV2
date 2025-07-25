package com.urban.carbon.api.admin.request;

import com.urban.carbon.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.InputStream;

/**
 * 用户基础信息修改请求
 * @author bjcug
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserModifyRequest extends BaseRequest {

    @NotNull(message = "userId不能为空")
    private Long userId;

    private String nickName;

    private String password;

    private InputStream photoInputStream;

    private String telephone;

    private String roleName;

}
