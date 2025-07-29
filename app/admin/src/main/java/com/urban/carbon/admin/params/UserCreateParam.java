package com.urban.carbon.admin.params;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * 用户创建参数
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateParam {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String name;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String telephone;

    /**
     * 角色
     */
    @NotBlank(message = "角色不能为空")
    private String role;
}
