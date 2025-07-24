package com.urban.carbon.data.manager.domain.service;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.urban.carbon.api.data.manager.constants.DataOperateType;
import com.urban.carbon.api.data.manager.constants.FileUploadStatus;
import com.urban.carbon.api.data.manager.exception.DataErrorCode;
import com.urban.carbon.api.data.manager.exception.DataException;
import com.urban.carbon.api.data.manager.response.data.UploadInitInfo;
import com.urban.carbon.data.manager.domain.entity.Data;
import com.urban.carbon.data.manager.infrastructure.mapper.DataMapper;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.util.Date;

@Service
@Slf4j
public class DataService extends ServiceImpl<DataMapper, Data> {

    /**
     * 数据服务
     */
    private final DataMapper dataMapper;

    /**
     * 数据操作流服务
     */
    private final DataOperateStreamService dataOperateStreamService;

    public DataService(DataMapper dataMapper, DataOperateStreamService dataOperateStreamService) {
        this.dataMapper = dataMapper;
        this.dataOperateStreamService = dataOperateStreamService;
    }

    /**
     * 创建数据
     *
     * @param dataName     数据名称
     * @param dataDesc     数据描述
     * @param dataType     数据类型
     * @param dataSourceId 数据源ID
     * @param loginId      创建人ID
     * @param dsName       数据源名称
     * @param info         初始化信息
     * @return 创建的数据
     */
    @Transactional
    public Data initCreate(
            String dataName, String dataDesc, String dataType, Long fileSize,
            Long dataSourceId, Long loginId, String dsName, UploadInitInfo info) {
        // 创建数据实体类
        Data data = new Data();
        data.initCreate(info.getFileId(), loginId, dataSourceId, dataName, dataDesc, dataType,
                info.getChunkSize(), info.getTotalChunks(), fileSize, info.getSaveSoft(),
                info.getStatus(), dsName);
        // 插入记录
        if (this.save(data)) {
            // 插入操作记录
            Long insertStream = dataOperateStreamService.insertStream(
                    data, loginId, DataOperateType.CREATE);
            Assert.notNull(insertStream, () -> new DataException(DataErrorCode.DATA_OPERATE_STREAM_FAIL));
            return data;
        } else {
            log.error("Data Create Failed!");
            return null;
        }
    }

    /**
     * 通过文件ID查询数据
     *
     * @param fileId 文件ID
     * @return 数据
     */
    public Data findByFileId(String fileId) {
        return this.dataMapper.findByFileId(fileId);
    }

    /**
     * 上传数据状态方法
     * 该方法负责将给定的数据对象信息更新到系统中，包括文件路径、状态和修改时间
     * 并记录操作日志
     *
     * @param filePath 文件路径，用于指定数据文件的位置
     * @param data     数据对象，包含需要更新的数据信息
     * @param loginId  用户登录ID，用于记录操作日志
     * @return 返回一个布尔值，表示数据更新是否成功
     */
    public Boolean uploadDataStatus(String filePath, Data data, Long loginId) {
        // 设置 data 中的信息
        data.setFilePath(filePath);
        // 设置数据状态为完成
        data.setStatus(FileUploadStatus.COMPLETED.name());
        // 设置修改时间
        data.setGmtModified(new Date());
        if (this.saveOrUpdate(data)) {
            // 插入操作记录
            Long insertStream = dataOperateStreamService.insertStream(
                    data, loginId, DataOperateType.UPDATE);
            // 确保操作记录插入成功
            Assert.notNull(insertStream, () -> new DataException(
                    DataErrorCode.DATA_OPERATE_STREAM_FAIL));
            return true;
        }
        return false;
    }

    /**
     * 取消上传数据
     * <p>
     * 此方法用于在数据上传过程中取消上传操作它通过更新数据的状态和删除标记来实现取消操作
     *
     * @param data 要取消上传的数据对象
     * @param loginId 当前登录用户的唯一标识符
     * @return 返回更新操作是否成功如果返回true，则表示取消操作成功；如果返回false，则表示取消操作失败
     */
    public Boolean cancelUploadData(Data data, Long loginId) {
        // 设置数据的上传状态为已取消
        data.setStatus(FileUploadStatus.CANCELED.name());
        // 标记数据为已删除，以逻辑删除的方式从数据库中移除该数据
        data.setDeleted(1);
        // 设置修改时间
        data.setGmtModified(new Date());
        // 调用updateById方法，根据数据的ID更新数据库中的记录
        if (this.saveOrUpdate(data)) {
            // 插入操作记录
            Long insertStream = dataOperateStreamService.insertStream(
                    data, loginId, DataOperateType.UPDATE);
            // 确保操作记录插入成功
            Assert.notNull(insertStream, () -> new DataException(
                    DataErrorCode.DATA_OPERATE_STREAM_FAIL));
            return true;
        }
        return false;
    }

    /**
     * 根据数据ID和登录ID查找数据对象
     * 此方法用于从数据访问层获取特定的数据对象，确保只有登录用户有权限访问
     *
     * @param dataId  数据对象的唯一标识符
     * @param loginId 当前登录用户的唯一标识符
     * @return 返回找到的数据对象，如果未找到则返回null
     */
    public Data findById(Long dataId, Long loginId) {
        return this.dataMapper.findById(dataId, loginId);
    }
}
