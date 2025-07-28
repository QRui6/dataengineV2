package com.urban.carbon.service.facade;

import com.urban.carbon.api.geoservice.exception.GeoServiceErrorCode;
import com.urban.carbon.api.geoservice.exception.GeoServiceException;
import com.urban.carbon.api.geoservice.request.GeoServiceModifiedRequest;
import com.urban.carbon.api.geoservice.request.GeoServicePageQueryRequest;
import com.urban.carbon.api.geoservice.request.GeoServiceQueryRequest;
import com.urban.carbon.api.geoservice.request.condition.GeoServiceIdsQueryCondition;
import com.urban.carbon.api.geoservice.request.condition.GeoServiceNameQueryCondition;
import com.urban.carbon.api.geoservice.request.condition.GeoServiceloginIdQueryCondition;
import com.urban.carbon.api.geoservice.response.data.GeoServiceInfo;
import com.urban.carbon.api.geoservice.service.GeoServiceFacadeService;
import com.urban.carbon.base.response.OperateResponse;
import com.urban.carbon.base.response.PageResponse;
import com.urban.carbon.base.response.SingleResponse;
import com.urban.carbon.rpc.facade.Facade;
import com.urban.carbon.service.domain.entity.GeoService;
import com.urban.carbon.service.domain.entity.GeoServiceConvertor;
import com.urban.carbon.service.domain.service.GeoServiceService;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.tomcat.jni.FileInfo;

import java.util.List;

@DubboService(version = "0.0.1")
public class GeoServiceFacadeServiceImpl implements GeoServiceFacadeService {

    /**
     * GeoServiceService 服务
     */
    private final GeoServiceService geoServiceService;

    /**
     * 构造函数
     *
     * @param geoServiceService GeoServiceService 服务
     */
    public GeoServiceFacadeServiceImpl(GeoServiceService geoServiceService) {
        this.geoServiceService = geoServiceService;
    }

    @Override
    @Facade
    public OperateResponse<GeoServiceInfo> publishService(GeoServiceModifiedRequest request) {
        // 校验 request 中提供的数据是否与提供的类型是匹配的
        FileInfo fileInfo = geoServiceService.checkDataType(request, request.getLoginId());
        request.setFileInfo(fileInfo);
        // 发布服务
        GeoService geoService = geoServiceService.publishService(request, request.getLoginId());
        OperateResponse<GeoServiceInfo> response = new OperateResponse<>();
        response.setSuccess(true);
        response.setData(GeoServiceConvertor.INSTANCE.mapToVo(geoService));
        return response;
    }

    @Override
    public PageResponse<GeoServiceInfo> queryService(GeoServicePageQueryRequest request) {
        PageResponse<GeoService> result;
        if (request.getCondition() instanceof GeoServiceNameQueryCondition) {
            result = geoServiceService.queryService(
                    ((GeoServiceNameQueryCondition) request.getCondition()).getServiceName(), request.getLoginId(),
                    request.getCurrentPage(), request.getPageSize());
        } else if (request.getCondition() instanceof GeoServiceloginIdQueryCondition) {
            result = geoServiceService.queryService(null, request.getLoginId(),
                    request.getCurrentPage(), request.getPageSize());
        } else {
            throw new GeoServiceException(GeoServiceErrorCode.QUERY_PARAMS_ERROR);
        }
        return PageResponse.of(
                GeoServiceConvertor.INSTANCE.mapToList(result.getDatas()),
                result.getTotal(),
                result.getPageSize(),
                result.getCurrentPage());
    }

    @Override
    public OperateResponse<List<Long>> deleteService(GeoServiceQueryRequest request) {
        return geoServiceService.deleteService(
                ((GeoServiceIdsQueryCondition) request.getCondition()).getIds(), request.getLoginId());
    }

    @Override
    public SingleResponse<GeoServiceInfo> getService(Long serviceId, String serviceMd5) {
        return null;
    }

    @Override
    public SingleResponse<GeoServiceInfo> getService(Long serviceId, String serviceMd5, Long loginId) {
        return geoServiceService.getService(serviceId, serviceMd5, loginId);
    }

    @Override
    public OperateResponse<GeoServiceInfo> enableService(Long serviceId, Long loginId) {
        return null;
    }

    @Override
    public OperateResponse<GeoServiceInfo> disableService(Long serviceId, Long loginId) {
        return null;
    }
}
