package com.urban.carbon.upload.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.urban.carbon.upload.domain.entity.FileUploadChunk;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileUploadChunkMapper extends BaseMapper<FileUploadChunk> {
}
