package com.urban.carbon.admin.params;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户状态切换参数
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Data
public class UserSwitchParam {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 操作类型
     * true: 激活用户，设置状态为ACTIVE
     * false: 禁用用户，设置状态为INIT
     */
    @NotNull(message = "操作类型不能为空")
    private Boolean operate;
}
