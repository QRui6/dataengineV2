package com.urban.carbon.file.strategy;

import java.io.InputStream;
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
     * @return         是否上传成功
     */
    String uploadFile(String filePath);

    /**
     * 将指定路径的临时文件或分片上传到目标存储系统
     *
     * @param filePath 本地文件路径（或临时分片路径）
     * @return         是否上传成功
     */
    String uploadFile(Path filePath);

    /**
     * 从目标存储系统中删除指定路径的文件
     *
     * @param filePath 要删除的文件路径
     * @return         是否删除成功
     */
    Boolean deleteFile(String filePath);

    /**
     * 从目标存储系统中下载指定路径的文件内容
     * 返回值为 InputStream：允许调用方按需读取文件内容，适合大文件。
     * 不加载整个文件到内存：通过流的方式逐步读取和传输数据，降低内存压力。
     * 调用者负责关闭流：文档中应注明调用方需要自行关闭流以释放资源。
     *
     * @param filePath 文件在存储系统中的路径
     * @return 返回文件内容的输入流，用于流式下载，适用于大文件
     */
    InputStream downloadFile(String filePath);

}
