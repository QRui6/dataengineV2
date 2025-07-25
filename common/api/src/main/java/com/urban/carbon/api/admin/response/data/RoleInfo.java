package com.urban.carbon.api.admin.response.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
public class RoleInfo  implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long roleId;

    private String roleName;

    private List<String> permissions;

    private String roleDesc;
}
