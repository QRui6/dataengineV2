package com.urban.carbon.data.manager.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.urban.carbon.data.manager.domain.entity.FileUploadChunk;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileUploadChunkMapper extends BaseMapper<FileUploadChunk> {

    /**
     * 根据文件ID查询分片信息
     *
     * @param fileId 文件ID
     * @return 分片信息
     */
    FileUploadChunk findByFileId(String fileId);

    /**
     * 根据上传ID查询所有分片信息
     *
     * @param uploadId 上传ID
     * @param totalChunks 总分片数
     * @return 分片信息
     */
    List<Integer> allCompleted(String uploadId, Integer totalChunks);

    /**
     * 根据文件ID和分片索引查询分片
     * @param fileId 文件ID
     * @param chunkIndex 分片索引
     * @return 分片信息
     */
    FileUploadChunk findByFileIdAndIndex(String fileId, Integer chunkIndex);

    /**
     * 根据上传ID删除所有分片信息
     *
     * @param uploadId 上传ID
     * @return 是否成功
     */
    Boolean clear(String uploadId);
}
