package com.urban.carbon.file.strategy;

import com.urban.carbon.file.configuration.properties.FileProperties;
import com.urban.carbon.file.configuration.properties.HdfsProperties;
import com.urban.carbon.file.exception.FileErrorCode;
import com.urban.carbon.file.exception.FileException;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

/**
 * HDFS文件上传策略（支持分片上传与合并）
 * 策略名称为 <code>STRATEGY_NAME</code>
 *
 * @author XuGaoran
 * @since 0.0.2
 */
@Slf4j
public class HDFSFileStrategy implements FileStrategy {

    /**
     * 策略名称
     */
    private static final String STRATEGY_NAME = "HDFS";

    /**
     * HDFS 文件系统配置
     */
    private final HdfsProperties hdfsProperties;

    /**
     * HDFS 文件系统实例 Configuration
     */
    private final Configuration configuration;

    /**
     * HDFS 文件系统连接 Uri
     */
    private final String hdfsUri;

    /**
     * HDFS 文件系统实例
     */
    private final transient FileSystem fileSystem;

    /**
     * 构造函数
     *
     * @param fileProperties 文件系统配置 (Autowired)
     */
    public HDFSFileStrategy(FileProperties fileProperties) {
        // 取出 HDFS 配置
        this.hdfsProperties = fileProperties.getHdfsFileSystem();
        if (this.hdfsProperties == null) {
            throw new FileException(FileErrorCode.HDFS_CONFIG_ERROR);
        }
        // 写入 hdfs uri
        this.hdfsUri = "hdfs://" + this.hdfsProperties.getHdfsHost() + ":" + this.hdfsProperties.getHdfsPort();
        log.info("HDFS Connection Init，URI: {}", this.hdfsUri);
        // 生成配置 这里设置为 空，之后如果有扩展需要修改这里的逻辑
        this.configuration = this.setHDFSConfiguration();
        // 连接文件系统
        this.fileSystem = this.connectToFileSystem();
        // 创建 base 目录
        createBasePathIfNotExists();
    }

    @Override
    public String getFileStrategyName() {
        return STRATEGY_NAME;
    }

    @Override
    public String uploadFile(String filePath, Long userId) {
        Path srcPath = new Path(filePath);
        Path dstPath = this.getTargetPath(filePath, userId);
        try {
            // 如果目标路径已存在，则先删除
            if (fileSystem.exists(dstPath)) {
                fileSystem.delete(dstPath, true); // true 表示递归删除
            }
            // 将本地文件上传到 HDFS
            fileSystem.copyFromLocalFile(srcPath, dstPath);
            log.info("File uploaded successfully to HDFS: {}", dstPath);
            return dstPath.toString();
        } catch (IOException e) {
            log.error("Failed to upload file to HDFS: {}", e.getMessage());
            throw new FileException(e.getMessage(), FileErrorCode.HDFS_UPLOAD_FAILED);
        }
    }

    @Override
    public String uploadFile(java.nio.file.Path filePath, Long userId) {
        return this.uploadFile(filePath.toString(), userId);
    }

    @Override
    public Boolean deleteFile(String filePath) {
        try {
            Path pathToDelete = new Path(filePath);
            // 检查文件是否存在
            if (!fileSystem.exists(pathToDelete)) {
                log.warn("File does not exist in HDFS: {}", filePath);
                return false;
            }
            // 删除文件或目录（true 表示递归删除）
            boolean result = fileSystem.delete(pathToDelete, true);
            if (result) {
                log.info("File deleted successfully from HDFS: {}", filePath);
            } else {
                log.warn("Failed to delete file from HDFS: {}", filePath);
            }
            return result;
        } catch (IOException e) {
            log.error("Failed to delete file in HDFS: {}", e.getMessage());
            throw new FileException(e.getMessage(), FileErrorCode.HDFS_DELETE_FAILED);
        }
    }

    @Override
    public void downloadFile(String filePath, OutputStream outputStream) throws IOException {
        Path path = new Path(filePath);
        if (!fileSystem.exists(path)) {
            throw new IOException("File not found in HDFS: " + filePath);
        }

        try (InputStream inputStream = fileSystem.open(path)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            log.error("Failed to open or read file from HDFS: {}", filePath, e);
            throw e;
        }
    }


    /**
     * 获取目标路径
     *
     * @param filePath 文件路径
     * @return 目标路径
     */
    private Path getTargetPath(String filePath, Long userId) {
        String fileWithType = filePath.substring(
                filePath.lastIndexOf(File.separator) + 1);
        return new Path(hdfsProperties.getBasePath() + "/" + userId + "/" + fileWithType);
    }

    /**
     * 根据 basePath 创建目录（如果不存在）
     */
    private void createBasePathIfNotExists() {
        String basePath = hdfsProperties.getBasePath();
        Path path = new Path(basePath);
        try {
            if (!fileSystem.exists(path)) {
                boolean result = fileSystem.mkdirs(path);
                if (result) {
                    log.info("Successfully Create HDFS Content: {}", basePath);
                } else {
                    log.error("Can't Create HDFS Dir，Please Check: {}", basePath);
                }
            } else {
                log.debug("HDFS already Exist: {}", basePath);
            }
        } catch (IOException e) {
            throw new FileException(e.getMessage(), FileErrorCode.HDFS_CREATE_DIR_FAILED);
        }
    }

    /**
     * 设置HDFS配置信息
     * TODO 这里需要思考如何通过配置文件中配置的信息对这里进行增加，通过 Nacos 配置中心实现配置自动注入
     *
     * @return Configuration 配置信息
     */
    private Configuration setHDFSConfiguration() {
        return new Configuration();
    }

    /**
     * 连接到HDFS文件系统
     * 此方法尝试建立与HDFS文件系统的连接通过URI和配置信息实现连接
     * 如果连接失败将抛出系统异常指示HDFS连接失败
     *
     * @return FileSystem 实例表示连接到的文件系统
     */
    private FileSystem connectToFileSystem() {
        try {
            if (this.fileSystem != null && this.isFileSystemValid()) {
                log.info("HDFS FileSystem Already Connected");
                return this.fileSystem;
            }
            // 通过HDFS URI和配置信息获取FileSystem实例
            return FileSystem.get(URI.create(this.hdfsUri), configuration,
                    hdfsProperties.getHdfsUser());
        } catch (Exception e) {
            // 如果连接过程中出现任何异常抛出自定义的系统异常
            throw new FileException(e.getMessage(), FileErrorCode.HDFS_CONNECTION_FAILED);
        }
    }

    /**
     * 检查文件系统是否有效
     */
    private boolean isFileSystemValid() {
        try {
            return fileSystem != null && fileSystem.exists(new Path("/"));
        } catch (IOException e) {
            log.error("Error checking HDFS file system validity: {}", e.getMessage());
            return false;
        }
    }
}
