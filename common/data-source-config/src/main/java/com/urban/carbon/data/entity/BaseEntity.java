package com.urban.carbon.data.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.StringJoiner;

/**
 * BaseEntity类是所有实体类的基类，提供了通用的字段和方法。
 *
 * <p>包含以下字段：
 * - id：主键字段，自增，自己判断类型；
 * - deleted：逻辑删除字段；
 * - lockVersion：乐观锁，版本号字段；
 * - gmtCreate：创建时间字段；
 * - gmtModified：修改时间字段。</p>
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Getter
@Setter
public class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键字段，自增，自己判断类型
     */
    @TableId(type= IdType.AUTO)
    private Long id;

    /**
     * 逻辑删除字段
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    /**
     * 乐观锁，版本号字段
     */
    @Version
    @TableField(fill = FieldFill.INSERT)
    private Integer lockVersion;

    /**
     * 创建时间字段
     */
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    /**
     * 修改时间字段
     */
    @TableField(fill = FieldFill.INSERT)
    private Date gmtModified;

    @Override
    public String toString() {
        return new StringJoiner(", ", BaseEntity.class.getSimpleName() + "[", "]")
                .add("id = " + id)
                .add("create time = " + gmtCreate.toString())
                .add("update time = " + gmtModified.toString())
                .add("Logic deleted = " + deleted)
                .add("Lock version = " + lockVersion)
                .toString();
    }
}

