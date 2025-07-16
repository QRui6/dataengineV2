package com.urban.carbon.api.upload.response.data;

import com.urban.carbon.api.upload.constants.FileUploadStatus;
import com.urban.carbon.api.upload.constants.SaveSoftType;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UploadInitInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件唯一标识 ( RandomGenerator )
     */
    private String FileId;

    /**
     * 分片大小 ( Byte )
     */
    private Long chunkSize;

    /**
     * 分片总数
     */
    private Integer totalChunks;

    /**
     * 状态 ( INITIALIZED )
     */
    private FileUploadStatus status;

    /**
     * 保存到哪一个数据存储中
     */
    private SaveSoftType saveSoft;

    /**
     * 使用上传初始化信息填充响应对象
     * 该方法用于设置上传文件的初始信息，包括文件ID、分块大小和总分块数，并将文件上传状态设置为已初始化
     *
     * @param FileId      文件ID，唯一标识上传的文件
     * @param chunkSize   分块大小，表示每个文件分块的大小
     * @param totalChunks 总分块数，表示文件被分割成的总块数
     */
    public void extracted(String FileId, long chunkSize, Integer totalChunks, SaveSoftType saveSoft) {
        // 设置文件ID
        this.setFileId(FileId);
        // 设置分块大小
        this.setChunkSize(chunkSize);
        // 设置总分块数
        this.setTotalChunks(totalChunks);
        // 设置文件上传状态为已初始化
        this.setStatus(FileUploadStatus.INITIALIZED);
        // 设置保存到哪一个数据存储中
        this.setSaveSoft(saveSoft);
    }
}
