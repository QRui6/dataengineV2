package com.urban.carbon.geoserver.entity;

import lombok.AllArgsConstructor;

import java.io.Serial;
import java.io.Serializable;


/**
 * 使用AllArgsConstructor注解自动生成包含所有字段的构造函数
 *
 * @author ChangBaorui
 * @since 0.0.2
 */
@AllArgsConstructor
public class FeatureType implements Serializable {

    /**
     * 序列化版本ID，用于兼容性
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 特性名称
     */
    private final String name;

    /**
     * 重写toString方法，以方便特性类型的序列化
     * @return 特性类型的JSON字符串表示
     */
    @Override
    public String toString() {
        // 特性是否启用，默认为true
        boolean enabled = true;
        return "{" +
                "\"featureType\":{" +
                "\"name\":\"" + name + "\"," +
                "\"enabled\":" + enabled +
                "}" +
                "}";
    }

}


