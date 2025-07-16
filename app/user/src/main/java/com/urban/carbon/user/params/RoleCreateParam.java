package com.urban.carbon.user.params;

import com.urban.carbon.api.user.constants.UserPermissionEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
public class RoleCreateParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色名称
     */
    @NotNull
    private String roleName;

    /**
     * 角色权限  <code>UserPermissionEnum</code>
     * UserPermissionEnum.PERSONAL.name()
     * [ xx, xx, xx, xx ]
     * xx,xx,xx,xx
     */
    private List<String> rolePermission;

    /**
     * 角色描述
     */
    private String roleDesc;

    public List<UserPermissionEnum> convertor() {
        return rolePermission.stream().map(UserPermissionEnum::valueOf).toList();
    }
}
