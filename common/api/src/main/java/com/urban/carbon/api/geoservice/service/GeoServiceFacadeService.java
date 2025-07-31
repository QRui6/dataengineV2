package com.urban.carbon.api.geoservice.service;

import com.urban.carbon.api.geoservice.request.GeoServiceModifiedRequest;
import com.urban.carbon.api.geoservice.request.GeoServicePageQueryRequest;
import com.urban.carbon.api.geoservice.request.GeoServiceQueryRequest;
import com.urban.carbon.api.geoservice.response.data.GeoServiceInfo;
import com.urban.carbon.base.response.OperateResponse;
import com.urban.carbon.base.response.PageResponse;
import com.urban.carbon.base.response.QueryResponse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface GeoServiceFacadeService {

    /**
     * 发布服务。
     *
     * @param request 服务修改请求对象，包含服务的相关信息。
     * @return 返回操作结果，包含服务发布状态及相关信息。
     */
    OperateResponse<GeoServiceInfo> publishService(GeoServiceModifiedRequest request);

    /**
     * 查询服务。
     *
     * @param request 服务查询请求对象，包含查询条件。
     * @return 返回服务查询结果，包含服务列表及相关信息。
     */
    PageResponse<GeoServiceInfo> pageQueryService(GeoServicePageQueryRequest request);

    /**
     * 删除服务。
     *
     * @param request 服务查询请求对象，包含删除条件。
     * @return 删除结果，包含删除状态及相关信息。
     */
    OperateResponse<List<Long>> deleteService(GeoServiceQueryRequest request) throws IOException;

    /**
     * 将 md5 形式的网址转换成可以访问的内容
     *
     * @param serviceId 服务ID
     * @param serviceMd5 服务MD5
     * @return 转换结果，包含转换后的网址及相关信息
     */
    QueryResponse<GeoServiceInfo> queryService(Long serviceId, String serviceMd5, Long loginId);

    /**
     * 启用服务
     *
     * @param serviceId 服务ID
     * @param loginId 登录用户ID
     * @return 启用结果
     */
    OperateResponse<GeoServiceInfo> enableService(Long serviceId, Long loginId);

    /**
     * 禁用服务
     *
     * @param serviceId 服务ID
     * @param loginId 登录用户ID
     * @return 禁用结果
     */
    OperateResponse<GeoServiceInfo> disableService(Long serviceId, Long loginId);
}
