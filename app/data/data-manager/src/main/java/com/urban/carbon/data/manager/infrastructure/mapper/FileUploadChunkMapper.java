package com.urban.carbon.data.manager.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.urban.carbon.data.manager.domain.entity.FileUploadChunk;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileUploadChunkMapper extends BaseMapper<FileUploadChunk> {
}
