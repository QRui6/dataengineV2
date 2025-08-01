package com.urban.carbon.geoserver.entity;

import io.micrometer.common.util.StringUtils;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * DataStore 实体类 为 shapefile 类型数据服务
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Setter
public class DataStore implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    private Boolean enabled;

    private String workspaceName;

    private ConnectionParameters connectionParameters;

    /**
     * ConnectionParameters 配置连接参数。
     *
     * @author XuGaoran
     */
    @Getter
    @Setter
    public static class ConnectionParameters implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private List<Entry> entries;

        /**
         * 构造方法用于初始化连接参数。
         *
         * @param url                    URL 地址
         * @param cacheReuseMemoryMaps   缓存和重用内存映射
         * @param fileType               文件类型
         * @param charset                编码
         * @param createSpatialIndex     创建空间索引
         * @param fileSystemType         文件系统类型
         * @param enableSpatialIndex     启用空间索引
         * @param memoryMappedBuffer     内存映射缓冲
         * @param timeZone               时间
         */
        public ConnectionParameters(String url, String cacheReuseMemoryMaps, String fileType,
                                    String charset, String createSpatialIndex, String fileSystemType,
                                    String enableSpatialIndex, String memoryMappedBuffer, String timeZone) {
            entries = new ArrayList<>();
            // 需要 url 属性，必需
            entries.add(new Entry("url", "file:" + url));
            // 添加 cache and reuse memory maps 属性
            insertList("cache and reuse memory maps", cacheReuseMemoryMaps, "true");
            // 添加文件的类型
            insertList("filetype", fileType, "shapefile");
            // 添加charset
            insertList("charset", charset, "UTF-8");
            // 添加 create spatial index
            insertList("create spatial index", createSpatialIndex, "true");
            // 添加 file system type
            insertList("fstype", fileSystemType, "shape");
            // 添加 enable spatial index
            insertList("enable spatial index", enableSpatialIndex, "true");
            // 添加 memory mapped buffer
            insertList("memory mapped buffer", memoryMappedBuffer, "false");
            // 添加 time zone
            insertList("timezone", timeZone, "Asia/Shanghai");
        }

        /**
         * 插入列表项，如果值不为空则使用该值，否则使用默认值。
         *
         * @param key          键
         * @param value        值
         * @param defaultValue 默认值
         */
        private void insertList(String key, String value, String defaultValue) {
            String temp = StringUtils.isNotBlank(value) ? value : defaultValue;
            entries.add(new Entry(key, temp));
        }

        /**
         * Entry 是一个键值对对象。
         *
         * @author XuGaoran
         */
        @AllArgsConstructor
        public static class Entry implements Serializable {

            @Serial
            private static final long serialVersionUID = 1L;

            private String key;

            private String value;

            /**
             * 返回 Entry 的字符串表示形式。
             *
             * @return 字符串表示
             */
            @Override
            public String toString() {
                if (value.equals("true") || value.equals("false")) {
                    return "{\"@key\":\"" + key +
                            "\",\"$\":" + value + "}";
                }
                return "{\"@key\":\"" + key +
                        "\",\"$\":\"" + value + "\"}";
            }
        }

        /**
         * 返回 ConnectionParameters 的字符串表示形式。
         *
         * @return 字符串表示
         */
        @Override
        public String toString() {
            StringJoiner joiner = new StringJoiner(",");
            entries.forEach(entry -> joiner.add(entry.toString()));
            return "\"connectionParameters\":{\"entry\":[" + joiner + "]}";
        }
    }

    /**
     * 返回 DataStore 的字符串表示形式。
     *
     * @return 字符串表示
     */
    @Override
    public String toString() {
        return "{\"dataStore\":{" +
                "\"name\":\"" + name + "\"," +
                "\"workspace\":{\"name\":\"" + workspaceName + "\",\"link\":\"http://" + workspaceName + "\"}," +
                "\"description\":\"" + description + "\"," +
                "\"enabled\":" + enabled + "," +
                connectionParameters +
                "}}";
    }

    /**
     * 构建创建数据源的参数。
     *
     * @param workspaceName          工作区名称
     * @param description            数据源描述
     * @param enabled                是否启用
     * @param dataStoreName          数据源名称
     * @param dataStorePath          数据源路径
     * @param cacheReuseMemoryMaps   缓存和重用内存映射
     * @param fileType               文件类型
     * @param charset                编码
     * @param createSpatialIndex     创建空间索引
     * @param fileSystemType         文件系统类型
     * @param enableSpatialIndex     启用空间索引
     * @param memoryMappedBuffer     内存映射缓冲
     * @param timeZone               时间
     * @return 数据源参数
     */
    public static String buildDataStore(
            String workspaceName, String description, Boolean enabled, String dataStoreName,
            String dataStorePath,String cacheReuseMemoryMaps, String fileType, String charset,
            String createSpatialIndex, String fileSystemType, String enableSpatialIndex,
            String memoryMappedBuffer, String timeZone) {
        DataStore dataStore = new DataStore();
        dataStore.setWorkspaceName(workspaceName);
        dataStore.setDescription(description);
        dataStore.setEnabled(enabled);
        dataStore.setName(dataStoreName);
        ConnectionParameters param = new ConnectionParameters(
                dataStorePath, cacheReuseMemoryMaps, fileType, charset, createSpatialIndex,
                fileSystemType, enableSpatialIndex, memoryMappedBuffer, timeZone);
        dataStore.setConnectionParameters(param);
        return dataStore.toString();
    }
}

