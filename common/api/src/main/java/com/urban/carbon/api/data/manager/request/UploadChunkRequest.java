package com.urban.carbon.api.data.manager.request;

import com.urban.carbon.base.request.BaseRequest;
import lombok.*;

import java.io.InputStream;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UploadChunkRequest extends BaseRequest {

    /**
     * 文件唯一标识
     */
    private String fileId;

    /**
     * 分片索引
     */
    private Integer chunkIndex;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 分片大小
     */
    private Long chunkSize;

    /**
     * 文件
     */
    private InputStream fileInputStream;

    /**
     * 文件的MD5
     */
    private String hashMD5;

    /**
     * 总分片数
     */
    private Integer totalChunks;

    /**
     * 创建上传分片请求
     *
     * @param fileId 文件唯一标识
     * @param chunkIndex 分片索引
     * @param inputStream 文件输入流
     */
    public void createRequest(String fileId, Integer chunkIndex,
                              InputStream inputStream, String hashMD5) {
        this.fileId = fileId;
        this.chunkIndex = chunkIndex;
        this.fileInputStream = inputStream;
        this.hashMD5 = hashMD5;
    }
}
