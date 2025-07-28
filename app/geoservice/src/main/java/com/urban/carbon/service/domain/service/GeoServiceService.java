package com.urban.carbon.service.domain.service;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.urban.carbon.api.data.manager.response.data.DataInfo;
import com.urban.carbon.api.geoservice.constants.GeoServiceOperateType;
import com.urban.carbon.api.geoservice.exception.GeoServiceErrorCode;
import com.urban.carbon.api.geoservice.exception.GeoServiceException;
import com.urban.carbon.base.response.PageResponse;
import com.urban.carbon.base.utils.RandomNameGenerator;
import com.urban.carbon.geoserver.GeoServerHttpUtils;
import com.urban.carbon.service.domain.entity.GeoService;
import com.urban.carbon.service.infrastructure.mapper.GeoServiceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeoServiceService extends ServiceImpl<GeoServiceMapper, GeoService> {

    /**
     * 服务操作流服务
     */
    private final GeoServiceOperateStreamService geoServiceOperateStreamService;

    /**
     * 服务操作流服务
     */
    private final GeoServiceMapper geoServiceMapper;

    /**
     * GeoServer HTTP 服务
     */
    private final GeoServerHttpUtils geoServerHttpUtils;

    /**
     * 构造函数
     *
     * @param geoServiceOperateStreamService 服务操作流服务
     * @param geoServiceMapper               服务数据访问接口
     * @param geoServerHttpUtils             GeoServer HTTP 服务
     */
    public GeoServiceService(GeoServiceOperateStreamService geoServiceOperateStreamService,
                             GeoServiceMapper geoServiceMapper, GeoServerHttpUtils geoServerHttpUtils) {
        this.geoServiceOperateStreamService = geoServiceOperateStreamService;
        this.geoServiceMapper = geoServiceMapper;
        this.geoServerHttpUtils = geoServerHttpUtils;
    }

    @Transactional
    public GeoService publishService(DataInfo dataInfo, String serviceName, String allowTypes,
                                     String serviceDesc, Long loginId) {
        String filePath = dataInfo.getFilePath();
        String storeName = filePath.substring(
                filePath.lastIndexOf("/") + 1, filePath.length() - 4);
        String workspaceName = geoServerHttpUtils.getGeoServerProperties().getWorkspace();
        Integer srs = Integer.parseInt(geoServerHttpUtils.getGeoServerProperties().getGeoCode());
        Integer proj = Integer.parseInt(geoServerHttpUtils.getGeoServerProperties().getGeoProjection());
        // 调用 GeoServerService 发布对应类型的服务
        try {
            // 1. 创建工作空间（如果不存在）
            geoServerHttpUtils.createWorkspace(workspaceName);
            // 2. 创建矢量数据存储 (Shapefile)
            geoServerHttpUtils.createShapefileDataStore(workspaceName, storeName, filePath);
            // 3. 创建 FeatureType (发布矢量图层服务)
            geoServerHttpUtils.createFeatureType(workspaceName, storeName, storeName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String serviceUrl = RandomNameGenerator.generateRandomURL();
        // 将返回的结果封装成 ServiceEntity 写入数据库
        GeoService geoService = new GeoService();
        geoService.createService(dataInfo.getDsId(), dataInfo.getId(), loginId, 1, srs, proj,
                workspaceName, storeName, storeName, serviceName, serviceUrl, allowTypes, serviceDesc);
        // 记录操作
        if (this.saveOrUpdate(geoService)) {
            long streamResult = geoServiceOperateStreamService.insertStream(
                    geoService, loginId, GeoServiceOperateType.SERVICE_PUBLISH);
            Assert.isTrue(streamResult > 0,
                    () -> new GeoServiceException(GeoServiceErrorCode.SERVICE_OPERATE_STREAM_FAIL));
            return geoService;
        }
        return null;
    }

    public PageResponse<GeoService> queryService(
            String serviceName, Long loginId, Integer pageNum, Integer pageSize) {
        Page<GeoService> page = this.page(new Page<>(pageNum, pageSize),
                new QueryWrapper<>(GeoService.class)
                        .eq("user_id", loginId)
                        .like(serviceName != null && !serviceName.isEmpty(),
                                "service_name", serviceName));
        return PageResponse.of(page.getRecords(), (int) page.getTotal(),
                (int) page.getCurrent(), (int) page.getSize());
    }

    @Transactional
    public List<GeoService> deleteService(List<Long> serviceIds, Long loginId) throws IOException {
        // 检查所有的id是否归属于loginId，同时是否可以被删除
        List<GeoService> geoServiceList = geoServiceMapper.findByLoginId(serviceIds, loginId);
        // 逐个删除 TODO
        List<GeoService> successToReturn = new ArrayList<>();
        for (GeoService geoService : geoServiceList) {
            geoServerHttpUtils.removeFeatureType(geoService.getWorkspace(), geoService.getStoreName(),
                    geoService.getLayerName(), true);
            geoServerHttpUtils.removeStore(geoService.getWorkspace(), geoService.getStoreName(),
                    "datastores", true);
            if (this.removeById(geoService)) {
                successToReturn.add(geoService);
            }
        }
        long streamResult = geoServiceOperateStreamService.insertStream(
                geoServiceList, loginId, GeoServiceOperateType.SERVICE_DELETE);
        Assert.isTrue(streamResult > 0,
                () -> new GeoServiceException(GeoServiceErrorCode.SERVICE_OPERATE_STREAM_FAIL));
        return successToReturn;
    }

    @Transactional
    public GeoService enableOrDisableService(Long serviceId, Long loginId, int startState) {
        GeoService geoService = geoServiceMapper.findById(serviceId, loginId);
        // 如果为空或者删除标记为1，则返回 SERVICE_NOT_FIND
        Assert.notNull(geoService, () -> new GeoServiceException(
                GeoServiceErrorCode.SERVICE_NOT_FIND));
        // 检查服务是否已启动
        if (geoService.getStarted() == startState) {
            return geoService;
        }
        geoService.setStarted(startState);
        // 更新内容
        boolean result = this.updateById(geoService);
        Assert.isTrue(result, () -> new GeoServiceException(GeoServiceErrorCode.SERVICE_UPDATE_FAILED));
        long streamResult = geoServiceOperateStreamService.insertStream(
                geoService, loginId, GeoServiceOperateType.SERVICE_ENABLE);
        Assert.isTrue(streamResult > 0,
                () -> new GeoServiceException(GeoServiceErrorCode.SERVICE_OPERATE_STREAM_FAIL));
        return geoService;
    }

    /**
     * 将数据库中的内容转换成可以访问的内容
     *
     * @param serviceId 服务id
     * @param loginId   登录用户id
     * @return 响应
     */
    public GeoService getService(Long serviceId, String serviceMd5, Long loginId) {
        GeoService geoService = geoServiceMapper.findById(serviceId, loginId);
        Assert.notNull(geoService, () -> new GeoServiceException(GeoServiceErrorCode.SERVICE_NOT_FIND));
        Assert.isTrue(geoService.getStarted() == 1,
                () -> new GeoServiceException(GeoServiceErrorCode.SERVICE_NOT_START));
        String[] splitString = geoService.getServiceUrl().split("/");
        Assert.isTrue(splitString[splitString.length - 1].equals(serviceMd5),
                () -> new GeoServiceException(GeoServiceErrorCode.SERVICE_NOT_MATCH));
        return geoService;
    }
}
