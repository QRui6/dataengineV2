package com.urban.carbon.api.geoservice.request;

import com.urban.carbon.api.geoservice.constants.GeoServiceFileType;
import com.urban.carbon.base.request.BaseRequest;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GeoServiceModifiedRequest extends BaseRequest {

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务描述
     */
    private String serviceDesc;

    /**
     * 数据ID
     */
    private Long dataId;

    /**
     * 服务文件类型
     */
    private GeoServiceFileType serviceFileType;

    /**
     * 服务格式类型
     */
    private String formatType;

    // TODO 需要加入 Data 相关数据

    public void generateModifiedRequest(String serviceName, String serviceDesc, String serviceFileType,
                                        String formatType, Long dataId, Long loginId) {
        this.serviceDesc = serviceDesc;
        this.serviceName = serviceName;
        this.serviceFileType = GeoServiceFileType.valueOf(serviceFileType);
        this.formatType = formatType;
        this.dataId = dataId;
        this.setLoginId(loginId);
    }
}
