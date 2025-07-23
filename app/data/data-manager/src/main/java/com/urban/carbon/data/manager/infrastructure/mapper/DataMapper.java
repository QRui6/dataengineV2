package com.urban.carbon.data.manager.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.urban.carbon.data.manager.domain.entity.Data;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DataMapper extends BaseMapper<Data> {

    /**
     * 根据fileId查询数据
     *
     * @param fileId 文件ID
     * @return 查询结果
     */
    Data findByFileId(String fileId);
}
