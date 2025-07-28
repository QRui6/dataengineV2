package com.urban.carbon.data.manager.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.urban.carbon.data.manager.domain.entity.Data;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DataMapper extends BaseMapper<Data> {

    /**
     * 根据fileId查询数据
     *
     * @param fileId 文件ID
     * @return 查询结果
     */
    Data findByFileId(String fileId);

    /**
     * 根据dataId查询数据
     *
     * @param dataId 数据ID
     * @param loginId 登录用户ID
     * @return 查询结果
     */
    Data findById(Long dataId, Long loginId);

    /**
     * 判断数据源下是否存在数据
     *
     * @param dsId 数据源ID
     * @return 存在返回1，不存在返回0
     */
    int existsData(Long dsId);

    /**
     * 根据dataIds查询数据
     *
     * @param ids 数据ID列表
     * @param loginId 登录用户ID
     * @return 查询结果
     */
    List<Data> findByDataIds(List<Long> ids, Long loginId);
}
