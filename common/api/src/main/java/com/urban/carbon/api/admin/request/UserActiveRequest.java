package com.urban.carbon.api.admin.request;

import com.urban.carbon.base.request.BaseRequest;
import lombok.*;

/**
 * 用户用户激活的请求
 *
 * @author bjcug
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserActiveRequest extends BaseRequest {

    /**
     * 通过用户ID进行激活
     */
    private Long userId;

}
