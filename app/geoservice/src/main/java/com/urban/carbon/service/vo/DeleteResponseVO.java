package com.urban.carbon.service.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 批量删除数据源的响应结果
 *
 * @author bjcug
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class DeleteResponseVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 批量删除成功的数据源ID列表
     */
    private List<Long> successList;

    /**
     * 批量删除失败的数据源ID列表
     */
    private List<Long> failList;

    /**
     * 构造方法 —— 批量删除数据源的响应结果
     *
     * @param idList 数据源信息列表
     * @param ids 待删除的数据源ID列表
     */
    public DeleteResponseVO(List<Long> idList, List<Long> ids) {
        this.successList = idList;
        this.failList = ids.stream().filter(id -> !this.successList.contains(id)).toList();
    }
}

