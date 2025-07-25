package com.urban.carbon.api.admin.response.data;

import java.io.Serial;
import java.io.Serializable;

public class BasicUserInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户头像保存地址
     */
    private String profilePhotoUrl;
}
