package com.urban.carbon.service.params;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GeoServiceModifiedParam  implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务描述
     */
    private String serviceDesc;

    /**
     * 使用了哪一个数据ID
     */
    private Long dataId;

    /**
     * 服务类型 SHP，TIFF
     */
    private String serviceFileType;

    /**
     * 服务格式 image/png; image/jpg; image/jpeg
     */
    private String formatType;

}
