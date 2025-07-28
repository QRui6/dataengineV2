package com.urban.carbon.service.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.urban.carbon.api.geoservice.request.GeoServiceModifiedRequest;
import com.urban.carbon.api.geoservice.request.GeoServicePageQueryRequest;
import com.urban.carbon.api.geoservice.request.GeoServiceQueryRequest;
import com.urban.carbon.api.geoservice.response.data.GeoServiceInfo;
import com.urban.carbon.api.geoservice.service.GeoServiceFacadeService;
import com.urban.carbon.base.response.OperateResponse;
import com.urban.carbon.base.response.PageResponse;
import com.urban.carbon.base.response.SingleResponse;
import com.urban.carbon.service.params.GeoServiceModifiedParam;
import com.urban.carbon.service.vo.DeleteResponseVO;
import com.urban.carbon.web.util.MultiResultConvertor;
import com.urban.carbon.web.vo.MultiResult;
import com.urban.carbon.web.vo.Result;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SpringBootApplication
@EnableDubbo
public class GeoServiceController {

    private final GeoServiceFacadeService geoServiceFacadeService;

    public GeoServiceController(GeoServiceFacadeService geoServiceFacadeService) {
        this.geoServiceFacadeService = geoServiceFacadeService;
    }

    @PostMapping("/publish")
    public Result<GeoServiceInfo> publishService(@RequestBody GeoServiceModifiedParam params) {
        String loginId = (String) StpUtil.getLoginId();
        GeoServiceModifiedRequest request = new GeoServiceModifiedRequest();
        request.generateModifiedRequest(params.getServiceName(), params.getServiceDesc(), params.getServiceFileType(),
                params.getFormatType(), params.getDataId(), Long.valueOf(loginId));
        OperateResponse<GeoServiceInfo> response = geoServiceFacadeService.publishService(request);
        return Result.success(response.getData());
    }

    @GetMapping("/query")
    public MultiResult<GeoServiceInfo> queryService(@RequestParam String serviceName,
                                                 @RequestParam Integer currentPage,
                                                 @RequestParam Integer pageSize) {
        String loginId = (String) StpUtil.getLoginId();
        GeoServicePageQueryRequest request;
        if (serviceName != null && !serviceName.isEmpty()) {
            request = new GeoServicePageQueryRequest(serviceName, Long.valueOf(loginId));
        } else {
            request = new GeoServicePageQueryRequest(Long.valueOf(loginId));
        }
        request.setCurrentPage(currentPage);
        request.setPageSize(pageSize);
        PageResponse<GeoServiceInfo> response = geoServiceFacadeService.queryService(request);
        return MultiResultConvertor.convert(response);
    }

    @DeleteMapping("/delete")
    public Result<DeleteResponseVO> deleteService(@RequestParam List<Long> serviceIds) {
        String loginId = (String) StpUtil.getLoginId();
        GeoServiceQueryRequest request = new GeoServiceQueryRequest(serviceIds, Long.valueOf(loginId));
        OperateResponse<List<Long>> response = geoServiceFacadeService.deleteService(request);
        return Result.success(new DeleteResponseVO(serviceIds, response.getData()));
    }

    @GetMapping("/query/{serviceMd5}")
    public Result<GeoServiceInfo> queryService(@PathVariable String serviceMd5, @RequestParam Long serviceId) {
        String loginId = (String) StpUtil.getLoginId();
        SingleResponse<GeoServiceInfo> service = geoServiceFacadeService.getService(serviceId, serviceMd5);
        return Result.success(service.getData());
    }

    @PutMapping("/enable")
    public Result<GeoServiceInfo> enableService(@RequestParam Long serviceId) {
        String loginId = (String) StpUtil.getLoginId();
        OperateResponse<GeoServiceInfo> resp = geoServiceFacadeService.enableService(serviceId, Long.valueOf(loginId));
        return Result.success(resp.getData());
    }

    @PutMapping("/disable")
    public Result<GeoServiceInfo> disableService(@RequestParam Long serviceId) {
        String loginId = (String) StpUtil.getLoginId();
        OperateResponse<GeoServiceInfo> resp = geoServiceFacadeService.disableService(serviceId, Long.valueOf(loginId));
        return Result.success(resp.getData());
    }
}
