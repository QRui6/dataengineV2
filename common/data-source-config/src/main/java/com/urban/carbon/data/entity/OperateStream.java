package com.urban.carbon.data.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 操作日志
 *
 * @author XuGaoran
 * @since 0.0.1
 */
@Getter
@Setter
@TableName("public.operate_stream")
public class OperateStream extends BaseEntity {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 操作类型
     */
    private String type;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 操作参数
     */
    private String param;

    /**
     * 扩展字段
     */
    private String extendInfo;
}

