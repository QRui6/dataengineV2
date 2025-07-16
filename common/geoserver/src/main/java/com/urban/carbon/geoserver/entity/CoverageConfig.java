package com.urban.carbon.geoserver.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * CoverageConfig类用于表示覆盖配置信息。
 *
 * <p>包含覆盖名称、原生格式和是否启用等属性。</p>
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@NoArgsConstructor
@AllArgsConstructor
public class CoverageConfig {
    /**
     * 覆盖名称。
     */
    private String name;
    /**
     * 原生格式，默认值为GeoTIFF。
     */
    private String nativeFormat = "GeoTIFF";
    /**
     * 是否启用，默认值为true。
     */
    private boolean enabled = true;

    /**
     * 构造方法，用于初始化覆盖名称。
     *
     * @param name 覆盖名称
     */
    public CoverageConfig(String name) {
        this.name = name;
    }


    /**
     * 重写toString方法，用于生成JSON格式的字符串表示。
     *
     * @return 返回覆盖配置的JSON字符串
     */
    public String toString() {
        return "{" +
                "\"coverage\": {" +
                "\"name\": \"" + name + "\"," +
                "\"title\": \"" + name + "\"," +
                "\"enabled\": " + enabled + "," +  // 布尔值无需加引号
                "\"nativeFormat\": \"" + nativeFormat + "\"," +
                "}" +
                "}";
    }
}

