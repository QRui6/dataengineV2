package com.urban.carbon.geoserver.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CoverageStore {

    private String workspaceName;

    private String storeName;

    private String storePath;

    /**
     * 重写toString方法，返回CoverageStore对象的JSON字符串表示。
     *
     * @return 返回格式化的JSON字符串
     */
    @Override
    public String toString() {
        return "{\"coverageStore\":{" +
                "\"name\":\"" + storeName +
                "\",\"type\":\"GeoTIFF\",\"enabled\":true,\"workspace\":{" +
                "\"name\":\"" + workspaceName + "\"}," +
                "\"url\":\"" + storePath + "\"}}";
    }
}

