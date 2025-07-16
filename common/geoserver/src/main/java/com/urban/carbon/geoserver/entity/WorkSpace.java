package com.urban.carbon.geoserver.entity;

import lombok.AllArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * WorkSpace类用于表示GeoServer中的工作空间实体。
 *
 * <p>包含工作空间的名称属性。</p>
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Setter
@AllArgsConstructor
public class WorkSpace implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 工作空间的名称属性。
     */
    private String name;

    /**
     * 重写toString方法，返回工作空间的JSON字符串表示。
     *
     * @return 工作空间的JSON字符串
     */
    @Override
    public String toString() {
        return "{\"workspace\":{\"name\":\"" + name + "\"}}";
    }
}

