package com.urban.carbon.file.strategy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * 文件存储策略接口，用于支持不同的底层存储方案（如 HDFS、本地磁盘、MinIO、S3 等）
 * 该接口仅定义与具体存储相关的文件级操作：上传和删除
 *
 * @author XuGaoran
 * @since 0.0.2
 */
public interface FileStrategy {

    /**
     * 获取当前存储策略的名称
     *
     * @return 存储策略名称
     */
    String getFileStrategyName();

    /**
     * 将指定路径的临时文件或分片上传到目标存储系统
     *
     * @param filePath 本地文件路径（或临时分片路径）
     * @return 是否上传成功
     */
    String uploadFile(String filePath);

    /**
     * 将指定路径的临时文件或分片上传到目标存储系统
     * TODO 文件写入耗费性能严重，开启一个虚拟线程用于文件写入
     *
     * @param filePath 本地文件路径（或临时分片路径）
     * @return 是否上传成功
     */
    String uploadFile(Path filePath);

    /**
     * 从目标存储系统中删除指定路径的文件
     *
     * @param filePath 要删除的文件路径
     * @return 是否删除成功
     */
    Boolean deleteFile(String filePath);

    /**
     * 从目标存储系统中下载指定路径的文件内容，并写入到输出流中。
     * 该方法适用于大文件下载，避免将整个文件加载到内存中。
     * 调用者必须确保输出流在使用完毕后正确关闭。
     *
     * @param filePath     文件在存储系统中的路径
     * @param outputStream 输出流，用于写入文件内容
     * @throws IOException 下载过程中发生 I/O 异常
     */
    void downloadFile(String filePath, OutputStream outputStream) throws IOException;


}
