package com.urban.carbon.api.upload.request;

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
     * 文件
     */
    private InputStream fileInputStream;

    /**
     * 文件的MD5
     */
    private String hashMD5;

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
