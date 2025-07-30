package com.urban.carbon.data.source.params;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * 数据源参数
 *
 * @author bjcug
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceModifiedParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据源名称
     */
    @NotBlank(message = "数据源名称不能为空")
    private String name;

    /**
     * 数据源描述
     */
    @NotBlank(message = "数据源描述不能为空")
    private String description;

    /**
     * 数据源id
     */
    private Long id;
}
