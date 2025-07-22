package com.urban.carbon.upload.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.urban.carbon.upload.domain.entity.FileUploadTask;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileUploadTaskMapper extends BaseMapper<FileUploadTask> {
}
