package com.urban.carbon.file.strategy;

import com.urban.carbon.file.configuration.properties.FileProperties;
import com.urban.carbon.file.configuration.properties.MinioProperties;
import com.urban.carbon.file.exception.FileErrorCode;
import com.urban.carbon.file.exception.FileException;
import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

@Slf4j
public class MinioFileStrategy implements FileStrategy {

    /**
     * 策略名称
     */
    private static final String STRATEGY_NAME = "MINIO";

    /**
     * Minio 文件系统配置
     */
    private final MinioProperties minioProperties;

    /**
     * Minio 客户端实例
     */
    private transient MinioClient minioClient;

    /**
     * 构造函数，初始化 MinioFileStrategy 实例
     *
     * @param fileProperties 文件系统配置属性 ( Autowired )
     */
    public MinioFileStrategy(FileProperties fileProperties) {
        // 获取 MinioFileSystem 配置
        this.minioProperties = fileProperties.getMinioFileSystem();
        if (minioProperties == null) {
            log.error("MinioFileSystem Configuration is null");
            throw new RuntimeException("MinioFileSystem Configuration is null");
        }
        // 获取客户端
        this.minioClient = getMinioClient();
        // 如果不存在 Bucket 就创建
        createBucketIfNotExists();
    }

    /**
     * 获取Minio客户端实例
     * 本方法旨在确保获取到的Minio客户端实例是有效且可用的它首先检查是否已经存在一个有效的Minio客户端实例，
     * 如果存在，则直接返回该实例如果不存在或者现有的实例无效，它将构建一个新的Minio客户端实例，
     * 并验证这个新实例的有效性如果新实例验证失败，将抛出系统异常，表明Minio连接建立失败
     *
     * @return MinioClient实例，如果无法建立有效连接，则抛出SystemException异常
     * @throws FileException 当新构建的Minio客户端实例验证失败时
     */
    private MinioClient getMinioClient() {
        // 检查是否存在有效的Minio客户端实例
        if (minioClient != null && this.isFileSystemValid(this.minioClient)) {
            return minioClient;
        }
        // 构建新的Minio客户端实例
        MinioClient minioClient = MinioClient.builder()
                .endpoint(minioProperties.getMinioHost(), minioProperties.getMinioPort(), false)
                .credentials(minioProperties.getMinioUser(), minioProperties.getMinioPassword())
                .build();
        // 验证新构建的Minio客户端实例是否有效
        if (!this.isFileSystemValid(minioClient)) {
            throw new FileException(FileErrorCode.MINIO_CONNECTION_FAILED);
        }
        // 返回有效的Minio客户端实例
        return minioClient;
    }

    /**
     * 验证 MinIO 文件系统的连接是否有效
     *
     * @param client MinioClient 实例，用于与 MinIO 服务器进行通信
     * @return Boolean 表示文件系统是否有效如果能够成功列出存储桶，则返回 true；否则返回 false
     */
    private Boolean isFileSystemValid(MinioClient client) {
        try {
            // 列出所有存储桶以验证连接
            client.listBuckets();
            return true; // 连接成功
        } catch (Exception e) {
            // 记录日志并返回 false 表示连接失败
            log.error("MinIO Connection invalid! Message: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取存储对象的完整路径
     *
     * @param filePath 文件路径
     * @return 完整的对象名称
     */
    private String getObjectName(String filePath) {
        if (!StringUtils.hasText(filePath)) {
            throw new IllegalArgumentException("Path can't be Blank");
        }
        // 这里的路径是 filePath, 所以我们取出使用/分割的最后一个元素作为对象名称
        int idxOfSplit = filePath.lastIndexOf("/");
        if (idxOfSplit == -1) {
            return filePath;
        } else {
            return filePath.substring(idxOfSplit + 1);
        }
    }

    /**
     * 创建存储桶（如果不存在）
     */
    private void createBucketIfNotExists() {
        try {
            String basePath = minioProperties.getBasePath();
            boolean found = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(basePath).build());
            if (!found) {
                // 创建存储桶
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(basePath).build());
                log.info("Successfully Create MinIO Bucket: {}", basePath);
            }
        } catch (Exception e) {
            log.error("Check or Create MinIO Bucket Failed", e);
            throw new FileException(FileErrorCode.MINIO_CONNECTION_FAILED);
        }
    }

    @Override
    public String getFileStrategyName() {
        return STRATEGY_NAME;
    }

    @Override
    public String uploadFile(String filePath, Long userId) {
        try {
            String basePath = minioProperties.getBasePath();
            // 验证连接是否有效
            this.minioClient = getMinioClient();
            // 获取对象名称
            String objectName = userId + "/" + getObjectName(filePath);
            // 上传文件
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(basePath)
                            .object(objectName)
                            .filename(filePath)
                            .build());
            log.info("File Upload Success: {} -> {}", filePath,
                    basePath + "/" + objectName);
            return basePath + "/" + objectName;
        } catch (Exception e) {
            log.error("File Upload Failed: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public String uploadFile(Path filePath, Long userId) {
        return this.uploadFile(filePath.toString(), userId);
    }

    @Override
    public Boolean deleteFile(String filePath) {
        try {
            // 验证连接是否有效
            this.minioClient = getMinioClient();
            // 删除文件
            String basePath = minioProperties.getBasePath();
            // 获取对象名称
            String objectName = filePath.substring(basePath.length() + 2);
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(basePath)
                            .object(objectName)
                            .build());

            log.info("File Deleted Success: {}", filePath);
            return true;
        } catch (Exception e) {
            log.error("File Deleted Failed: {}", filePath, e);
            return false;
        }
    }

    @Override
    public void downloadFile(String filePath, OutputStream outputStream) throws IOException {
        try {
            // 验证连接是否有效
            this.minioClient = getMinioClient();
            // 获取文件输入流
            String basePath = minioProperties.getBasePath();
            // 获取对象名称
            String objectName = filePath.substring(basePath.length() + 2);
            try (InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(basePath)
                            .object(objectName)
                            .build())) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            log.error("File Download Failed: {}", filePath, e);
            throw new IOException("MinIO download failed", e);
        }
    }

}
