package com.urban.carbon.user.params;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

@Getter
@Setter
@ToString
public class RoleModifiedParam extends RoleCreateParam {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @NotNull
    private Long id;

    /**
     * 角色是否激活，默认为不激活
     */
    private Integer roleActive;
}
