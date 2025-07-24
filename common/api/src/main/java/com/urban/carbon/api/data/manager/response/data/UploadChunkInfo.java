package com.urban.carbon.api.data.manager.response.data;

import com.urban.carbon.api.data.manager.constants.FileUploadStatus;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UploadChunkInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件唯一标识
     */
    private String fileId;

    /**
     * 分片索引
     */
    private Integer chunkIndex;

    /**
     * 是否接收成功
     */
    private Boolean received;

    /**
     * 状态 ( UPLOADING )
     */
    private FileUploadStatus status;

    /**
     * 进度
     */
    private Double progress;

    /**
     * 分片上传信息
     * @param fileId 文件唯一标识
     * @param chunkIndex 分片索引
     * @param received 是否接收成功
     */
    public void extracted(String fileId, Integer chunkIndex, boolean received,
                          FileUploadStatus status) {
        this.fileId = fileId;
        this.chunkIndex = chunkIndex;
        this.received = received;
        this.status = status;
    }
}
