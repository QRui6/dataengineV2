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
    public String dataName;

    /**
     * 数据描述
     */
    public String dataDesc;

    /**
     * 数据源ID
     */
    public Long dataSourceId;

    /**
     * 创建请求
     *
     * @param dataName 数据名称
     * @param dataDesc 数据描述
     * @param dataSourceId 数据源ID
     * @param aLong 创建人ID
     */
    public void createRequest(String dataName, String dataDesc, Long dataSourceId, Long aLong) {
        this.setDataName(dataName);
        this.setDataDesc(dataDesc);
        this.setDataSourceId(dataSourceId);
        this.setLoginId(aLong);
    }
}
