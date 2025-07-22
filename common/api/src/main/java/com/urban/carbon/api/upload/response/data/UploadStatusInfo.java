package com.urban.carbon.api.upload.response.data;

import com.urban.carbon.api.upload.constants.FileUploadStatus;
import com.urban.carbon.api.upload.constants.SaveSoftType;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UploadStatusInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件上传过程中用于标记的 UUID
     */
    private String fileId;

    /**
     * 已上传的分片索引
     */
    private List<Integer> uploadedChunks;

    /**
     * 总分片数
     */
    private Long totalChunks;

    /**
     * 状态
     */
    private FileUploadStatus status;

    /**
     * 上传进度 (4.20)
     */
    private Double progress;

    /**
     * 文件路径, 这里指的是存储在 HDFS 上的文件路径
     */
    private String filePath;

    /**
     * 文件大小 单位为字节
     */
    private Long fileSize;

    /**
     * 数据类型，也就是文件的后缀名是什么
     */
    private String dataType;

    /**
     * 文件保存方式, 保存到了什么存储系统中
     */
    private SaveSoftType saveSoftType;

    /**
     * 数据名称
     */
    private String dataName;

    /**
     * 数据描述
     */
    private String dataDesc;

    /**
     * 上传失败的列表
     */
    private List<Integer> failedChunks;

    public void buildResponse(String fileId, Long totalChunks, String dataName, String dataDesc,
                              Long fileSize, SaveSoftType saveSoftType, String dataType,
                              List<Integer> uploadedChunks, String filePath, Boolean canceled) {
        this.fileId = fileId;
        this.uploadedChunks = uploadedChunks;
        this.totalChunks = totalChunks;
        if (canceled) {
            this.status = FileUploadStatus.CANCELED;
            this.progress = 0.;
        } else {
            if (uploadedChunks.size() == totalChunks) {
                this.status = FileUploadStatus.COMPLETED;
                this.progress = 100.0;
            } else {
                this.status = FileUploadStatus.UPLOADING;
                this.progress = (uploadedChunks.size() * 100.0 / totalChunks) ;
            }
        }
        this.dataName = dataName;
        this.dataDesc = dataDesc;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.saveSoftType = saveSoftType;
        this.dataType = dataType;
    }
}
