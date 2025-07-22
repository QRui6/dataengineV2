package com.urban.carbon.api.data.manager.request;

import com.urban.carbon.base.request.BaseRequest;
import lombok.*;

@Getter
@Setter
@ToString
public class DataCreateRequest extends BaseRequest {

    /**
     * 数据名称
     */
    private String dataName;

    /**
     * 数据的类型
     */
    private String dataType;

    /**
     * 数据描述
     */
    private String dataDesc;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 数据源ID
     */
    private Long dataSourceId;

    /**
     * 创建请求
     *
     * @param dataName 数据名称
     * @param dataDesc 数据描述
     * @param fileSize 文件大小
     * @param dataType 数据类型
     * @param dataSourceId 数据源ID
     * @param aLong 创建人ID
     */
    public void createRequest(String dataName, String dataDesc, Long fileSize,
                              String dataType, Long dataSourceId, Long aLong) {
        this.setDataName(dataName);
        this.setDataType(dataType);
        this.setDataDesc(dataDesc);
        this.setFileSize(fileSize);
        this.setDataSourceId(dataSourceId);
        this.setLoginId(aLong);
    }
}
