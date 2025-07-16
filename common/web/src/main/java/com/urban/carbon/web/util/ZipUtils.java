package com.urban.carbon.web.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 工具类：用于处理 Shapefile 文件和 ZIP 文件。
 * 功能包括：
 * - 检查文件路径有效性
 * - 处理 .shp 文件
 * - 处理 .zip 文件，提取其中的 .shp 文件
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Slf4j
public class ZipUtils {

    /**
     * 处理 ZIP 文件或单独的 SHP 文件。
     * 根据文件类型执行不同的操作。
     *
     * @param filePath 文件路径
     * @param baseName 新的文件名基础部分
     * @return 成功返回 true，失败返回 false
     */
    public static String dealZipFile(String filePath, String baseName) {
        // 检查文件路径有效性
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            log.error("File does not exist: {}", filePath);
            return null;
        }
        // 检查文件类型
        String extension = getExtension(path.getFileName().toString());
        if (extension.equals(".shp")) {
            return handleShpFile(path, baseName);
        } else if (extension.equals(".zip")) {
            return handleZipFile(path, baseName);
        } else {
            log.error("Unsupported file type: {}", extension);
            return null;
        }
    }

    /**
     * 处理单独的 SHP 文件。
     * 将文件复制并重命名为 {baseName}.shp。
     *
     * @param shpPath  SHP 文件路径
     * @param baseName 新的文件名基础部分
     * @return 成功返回 true，失败返回 false
     */
    private static String handleShpFile(Path shpPath, String baseName) {
        try {
            Path targetPath = shpPath.getParent().resolve(baseName + ".shp");
            Files.copy(shpPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            return targetPath.toString();
        } catch (IOException e) {
            log.error("Error handling SHP file: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 处理 ZIP 文件。
     * 解压 ZIP 文件，提取其中的 SHP 文件并重命名为 {baseName}.shp。
     *
     * @param zipPath  ZIP 文件路径
     * @param baseName 新的文件名基础部分
     * @return 成功返回 true，失败返回 false
     */
    private static String handleZipFile(Path zipPath, String baseName) {
        try {
            Path tempDir = Files.createTempDirectory("temp_unzip_");
            unzip(zipPath, tempDir);
            Path shpFile = findShpFile(tempDir);
            if (shpFile == null) {
                log.error("No SHP file found in ZIP: {}", zipPath);
                return null;
            }
            Path targetPath = zipPath.getParent().resolve(baseName + ".shp");
            Files.move(shpFile, targetPath, StandardCopyOption.REPLACE_EXISTING);
            deleteDirectory(tempDir);
            return targetPath.toString();
        } catch (IOException e) {
            log.error("Error handling ZIP file: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 解压 ZIP 文件到指定目录。
     *
     * @param zipPath ZIP 文件路径
     * @param destDir 目标解压目录
     * @throws IOException 如果发生 I/O 异常
     */
    private static void unzip(Path zipPath, Path destDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipPath),
                Charset.forName("GBK"))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path entryPath = destDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    Files.copy(zis, entryPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }
    }

    /**
     * 查找目录中的 SHP 文件。
     *
     * @param dir 要查找的目录
     * @return 找到的 SHP 文件路径，未找到返回 null
     */
    private static Path findShpFile(Path dir) {
        try (var stream = Files.walk(dir)) {
            return stream.filter(path -> 
                path.toString().toLowerCase().endsWith(".shp") && Files.isRegularFile(path))
                .findFirst().orElse(null);
        } catch (IOException e) {
            log.error("Error finding SHP file: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 删除目录及其中的所有文件和子目录。
     *
     * @param path 要删除的目录路径
     * @throws IOException 如果发生 I/O 异常
     */
    private static void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        }
    }

    /**
     * 获取文件扩展名。
     *
     * @param filename 文件名
     * @return 扩展名（包含点号）
     */
    private static String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex);
    }
}

