package com.urban.carbon.service.facade;

import com.urban.carbon.api.data.manager.response.data.DataInfo;
import com.urban.carbon.api.data.manager.service.DataFacadeService;
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
import com.urban.carbon.base.response.QueryResponse;
import com.urban.carbon.file.strategy.FileStrategy;
import com.urban.carbon.file.strategy.FileStrategyFactory;
import com.urban.carbon.rpc.facade.Facade;
import com.urban.carbon.service.domain.entity.GeoService;
import com.urban.carbon.service.domain.entity.GeoServiceConvertor;
import com.urban.carbon.service.domain.service.GeoServiceService;
import org.apache.dubbo.config.annotation.DubboService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@DubboService(version = "1.0.0")
public class GeoServiceFacadeServiceImpl implements GeoServiceFacadeService {

    /**
     * 用于存储需要发布服务的文件的位置
     */
    private static final String TMP_PATH = System.getProperty("java.io.tmpdir");

    /**
     * GeoServiceService 服务
     */
    private final GeoServiceService geoServiceService;

    /**
     * DataFacadeService 服务
     */
    private final DataFacadeService dataFacadeService;

    /**
     * 文件策略工厂
     */
    private final FileStrategyFactory fileStrategyFactory;

    /**
     * 构造函数
     *
     * @param geoServiceService GeoServiceService 服务
     */
    public GeoServiceFacadeServiceImpl(GeoServiceService geoServiceService, DataFacadeService dataFacadeService,
                                       FileStrategyFactory fileStrategyFactory) {
        this.geoServiceService = geoServiceService;
        this.dataFacadeService = dataFacadeService;
        this.fileStrategyFactory = fileStrategyFactory;
    }

    @Override
    @Facade
    public OperateResponse<GeoServiceInfo> publishService(GeoServiceModifiedRequest request) {
        // 校验 request 中提供的数据是否与提供的类型是匹配的
        QueryResponse<DataInfo> fileResponse = dataFacadeService.findById(
                request.getDataId(), request.getLoginId());
        DataInfo dataInfo = fileResponse.getData();
        String saveSoft = dataInfo.getSaveSoft();
        // 将文件下载对应的位置 tmp 目录中
        String filePath = dataInfo.getFilePath();
        String fileOnServerPath = Path.of(TMP_PATH, filePath.substring(filePath.lastIndexOf("/"))).toString();
        FileStrategy strategy = fileStrategyFactory.getStrategy(saveSoft);
        try {
            strategy.downloadFile(filePath, new FileOutputStream(fileOnServerPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 发布服务
        GeoService geoService = geoServiceService.publishService(fileOnServerPath, dataInfo.getDataType(),
                dataInfo.getDataSourceId(), dataInfo.getId(), request.getServiceName(),
                request.getFormatType(), request.getServiceDesc(), request.getLoginId());
        OperateResponse<GeoServiceInfo> response = new OperateResponse<>();
        response.setSuccess(true);
        response.setData(GeoServiceConvertor.INSTANCE.mapToVo(geoService));
        return response;
    }

    @Override
    public PageResponse<GeoServiceInfo> pageQueryService(GeoServicePageQueryRequest request) {
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
    public OperateResponse<List<Long>> deleteService(GeoServiceQueryRequest request) throws IOException {
        List<GeoService> geoServices = geoServiceService.deleteService(
                ((GeoServiceIdsQueryCondition) request.getCondition()).getIds(), request.getLoginId());
        List<Long> list = geoServices.stream().map(GeoService::getId).toList();
        OperateResponse<List<Long>> response = new OperateResponse<>();
        response.setSuccess(true);
        response.setData(list);
        return response;
    }

    @Override
    public OperateResponse<GeoServiceInfo> enableService(Long serviceId, Long loginId) {
        GeoService geoService = geoServiceService.enableOrDisableService(serviceId, loginId, 1);
        OperateResponse<GeoServiceInfo> response = new OperateResponse<>();
        response.setSuccess(true);
        response.setData(GeoServiceConvertor.INSTANCE.mapToVo(geoService));
        return response;
    }

    @Override
    public OperateResponse<GeoServiceInfo> disableService(Long serviceId, Long loginId) {
        GeoService geoService = geoServiceService.enableOrDisableService(serviceId, loginId, 0);
        OperateResponse<GeoServiceInfo> response = new OperateResponse<>();
        response.setSuccess(true);
        response.setData(GeoServiceConvertor.INSTANCE.mapToVo(geoService));
        return response;
    }

    @Override
    public QueryResponse<GeoServiceInfo> queryService(Long serviceId, String serviceMd5, Long loginId) {
        GeoService service = geoServiceService.getService(serviceId, serviceMd5, loginId);
        QueryResponse<GeoServiceInfo> response = new QueryResponse<>();
        response.setSuccess(true);
        response.setData(GeoServiceConvertor.INSTANCE.mapToVo(service));
        return response;
    }
}
