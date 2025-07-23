package com.urban.carbon.api.data.manager.response.data;

import com.urban.carbon.api.data.manager.constants.FileUploadStatus;
import com.urban.carbon.api.data.manager.constants.SaveSoftType;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
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
    private Integer totalChunks;

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

    /**
     * 构建文件上传的响应信息
     *
     * @param fileId         文件ID，用于唯一标识一个文件
     * @param totalChunks    文件总共需要上传的块数
     * @param dataName       文件名称
     * @param dataDesc       文件描述
     * @param fileSize       文件大小
     * @param saveSoftType   文件保存的软类型，可能是指文件的某种分类或标签
     * @param dataType       文件类型，例如文档、图片等
     * @param uploadedChunks 已经成功上传的文件块的列表
     * @param filePath       文件在服务器上的保存路径
     * @param canceled       表示文件上传是否已被取消
     */
    public void buildResponse(String fileId, Integer totalChunks, String dataName, String dataDesc,
                              Long fileSize, SaveSoftType saveSoftType, String dataType,
                              List<Integer> uploadedChunks, String filePath, Boolean canceled) {
        // 初始化文件上传响应的基本信息
        this.fileId = fileId;
        this.uploadedChunks = uploadedChunks;
        this.totalChunks = totalChunks;
        // 初始化失败块列表为空
        this.failedChunks = new ArrayList<>();

        // 根据上传状态设置文件上传的状态和进度
        if (canceled) {
            // 如果上传被取消，设置状态为已取消，进度为0
            this.status = FileUploadStatus.CANCELED;
            this.progress = 0.;
        } else {
            if (uploadedChunks.size() == totalChunks) {
                // 如果所有块都已上传，设置状态为完成，进度为100%
                this.status = FileUploadStatus.COMPLETED;
                this.progress = 100.0;
            } else {
                // 如果部分块已上传，设置状态为上传中，并计算当前上传进度
                this.status = FileUploadStatus.UPLOADING;
                this.progress = (uploadedChunks.size() * 100.0 / totalChunks);

                // 检查并记录未成功上传的块（失败块）
                for (int i = 0; i < totalChunks; i++) {
                    if (!uploadedChunks.contains(i)) {
                        this.failedChunks.add(i);
                    }
                }
            }
        }

        // 设置文件的附加信息
        this.dataName = dataName;
        this.dataDesc = dataDesc;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.saveSoftType = saveSoftType;
        this.dataType = dataType;
    }
}
