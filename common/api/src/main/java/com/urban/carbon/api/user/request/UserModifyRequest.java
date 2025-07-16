package com.urban.carbon.api.user.request;

import com.urban.carbon.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 用户基础信息修改请求
 * @author XuGaoran
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

    private String profilePhotoUrl;

    private String telephone;

}
