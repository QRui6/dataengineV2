package com.urban.carbon.data.source.domain.service;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.urban.carbon.api.data.source.constants.DataSourceOperateType;
import com.urban.carbon.api.data.source.exception.DataSourceErrorCode;
import com.urban.carbon.api.data.source.exception.DataSourceException;
import com.urban.carbon.api.data.source.response.data.DataSourceInfo;
import com.urban.carbon.base.response.OperateResponse;
import com.urban.carbon.base.response.PageResponse;
import com.urban.carbon.data.source.domain.entity.DataSource;
import com.urban.carbon.data.source.domain.entity.convertor.DataSourceConvertor;
import com.urban.carbon.data.source.infrastructure.mapper.DataSourceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class DataSourceService extends ServiceImpl<DataSourceMapper, DataSource> {

    /**
     * 数据源查询mapper
     */
    private final DataSourceMapper dataSourceMapper;

    /**
     * 数据源操作记录mapper
     */
    private final DataSourceOperateStreamService dataSourceOperateStreamService;

    /**
     * 构造函数
     *
     * @param dataSourceMapper 数据源查询mapper
     */
    public DataSourceService(DataSourceMapper dataSourceMapper,
                             DataSourceOperateStreamService dataSourceOperateStreamService) {
        this.dataSourceMapper = dataSourceMapper;
        this.dataSourceOperateStreamService = dataSourceOperateStreamService;
    }

    /**
     * 获取数据源信息
     *
     * @param dataSourceId 数据源ID
     * @return 数据源信息
     */
    public DataSource findById(Long dataSourceId, Long loginId) {
        return this.dataSourceMapper.findById(dataSourceId, loginId);
    }

    /**
     * 创建数据源
     *
     * @param loginId 登录用户ID
     * @return 数据源
     */
    @Transactional
    public OperateResponse<DataSourceInfo> createDataSource(
            String dataSourceName, String dataSourceDesc, Long loginId) {
        // 创建数据源
        DataSource dataSource = new DataSource();
        dataSource.create(dataSourceName, dataSourceDesc, loginId);
        // 插入数据
        Assert.isTrue(dataSourceMapper.insert(dataSource) > 0,
                () -> new DataSourceException(DataSourceErrorCode.DATA_SOURCE_CREATE_FAIL));
        // 记录操作
        Assert.isTrue(dataSourceOperateStreamService.insertStream(
                        dataSource, loginId, DataSourceOperateType.DATASOURCE_CREATE) > 0,
                () -> new DataSourceException(DataSourceErrorCode.DATA_SOURCE_OPERATOR_STREAM_FAIL));
        // 封装返回结果
        DataSourceInfo dsInfo = DataSourceConvertor.INSTANCE.mapToVo(dataSource);
        OperateResponse<DataSourceInfo> response = new OperateResponse<>();
        response.setSuccess(true);
        response.setData(dsInfo);
        return response;
    }

    /**
     * 修改数据源信息
     *
     * @param dataSourceId   数据源ID
     * @param dataSourceName 数据源名称
     * @param dataSourceDesc 数据源描述
     * @param loginId        登录用户ID
     * @return 操作响应结果，包含修改后的数据源信息
     */
    @Transactional
    public OperateResponse<DataSourceInfo> modifyDataSource(
            Long dataSourceId, String dataSourceName, String dataSourceDesc, Long loginId) {
        // 查询数据源是否存在
        DataSource dataSource = dataSourceMapper.selectById(dataSourceId);
        Assert.notNull(dataSource, () -> new DataSourceException(DataSourceErrorCode.DATA_SOURCE_NOT_EXISTS));
        // 查询当前用户是否有修改的权限
        Assert.isTrue(dataSource.getDsUserId().equals(loginId),
                () -> new DataSourceException(DataSourceErrorCode.NO_PRIVILEGES));
        // 修改数据源信息
        if (dataSourceName != null) {
            dataSource.setDsName(dataSourceName);
        }
        if (dataSourceDesc != null) {
            dataSource.setDsDesc(dataSourceDesc);
        }
        // 更新修改时间
        dataSource.setGmtModified(new Date());
        OperateResponse<DataSourceInfo> response = new OperateResponse<>();
        if (updateById(dataSource)) {
            Long streamResult = dataSourceOperateStreamService.insertStream(
                    dataSource, loginId, DataSourceOperateType.DATASOURCE_MODIFY);
            Assert.isTrue(streamResult > 0,
                    () -> new DataSourceException(DataSourceErrorCode.DATA_SOURCE_OPERATOR_STREAM_FAIL));
            response.setSuccess(true);
            response.setData(DataSourceConvertor.INSTANCE.mapToVo(dataSource));
        }
        response.setSuccess(false);
        return response;
    }

    public OperateResponse<List<Long>> deleteBatchDataSource(Long loginId, List<DataSource> dsSuccess) {
        OperateResponse<List<Long>> response = new OperateResponse<>();
        // 写操作记录
        if (!dsSuccess.isEmpty()) {
            Long streamResult = dataSourceOperateStreamService.insertStream(
                    dsSuccess, loginId, DataSourceOperateType.DATASOURCE_DELETE);
            Assert.isTrue(streamResult > 0,
                    () -> new DataSourceException(DataSourceErrorCode.DATA_SOURCE_OPERATOR_STREAM_FAIL));
            // 封装返回结果
            response.setSuccess(true);
            response.setData(dsSuccess.stream().map(DataSource::getId).toList());
        } else {
            response.setSuccess(false);
        }
        // 返回结果
        return response;
    }

    /**
     * 没有条件，查询所有的内容
     * TODO 这里需要考虑深度分页问题
     *
     * @param currentPage 当前页码
     * @param pageSize 每页数量
     * @param loginId 登录用户id
     * @return 分页查询结果
     */
    public PageResponse<DataSourceInfo> pageQuery(int currentPage, int pageSize, Long loginId) {
        Page<DataSource> page = this.page(new Page<>(currentPage, pageSize),
                new QueryWrapper<>(DataSource.class)
                        .eq("ds_user_id", loginId)
                        .orderByDesc("gmt_create")
        );
        return PageResponse.of(DataSourceConvertor.INSTANCE.mapToList(page.getRecords()),
                (int) page.getTotal(), pageSize, currentPage);
    }

    /**
     * 通过名称进行查询
     * TODO 这里需要考虑深度分页问题
     *
     * @param currentPage 当前页码
     * @param pageSize 每页数量
     * @param dsName 数据源名称
     * @param loginId 登录用户id
     * @return 分页查询结果
     */
    public PageResponse<DataSourceInfo> pageQueryByName(
            int currentPage, int pageSize, String dsName, Long loginId) {
        Page<DataSource> page = this.page(new Page<>(currentPage, pageSize),
                new QueryWrapper<>(DataSource.class)
                        .eq("ds_user_id", loginId)
                        .like("ds_name", dsName)
                        .orderByDesc("gmt_create")
        );
        return PageResponse.of(DataSourceConvertor.INSTANCE.mapToList(page.getRecords()),
                (int) page.getTotal(), pageSize, currentPage);
    }

    /**
     * 通过ID列表查询数据源
     *
     * @param dataSourceIds 数据源ID列表
     * @param loginId 登录用户id
     * @return 查询结果
     */
    public List<DataSource> findByIds(List<Long> dataSourceIds, Long loginId) {
        return dataSourceMapper.findByIds(dataSourceIds, loginId);
    }
}
