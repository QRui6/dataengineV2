package com.urban.carbon.api.data.manager.service;

import com.urban.carbon.api.data.manager.request.DataCreateRequest;
import com.urban.carbon.api.data.manager.response.data.DataInfo;
import com.urban.carbon.base.response.OperateResponse;

public interface DataFacadeService {

    /**
     * 创建数据
     *
     * @param request 创建数据请求
     * @return 创建数据结果
     */
    OperateResponse<DataInfo> initCreateData(DataCreateRequest request);
}
