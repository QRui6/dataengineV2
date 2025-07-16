package com.urban.carbon.web.util;


import com.urban.carbon.base.response.PageResponse;
import com.urban.carbon.web.vo.MultiResult;

import static com.urban.carbon.base.response.ResponseCode.SUCCESS;

/**
 * @author Hollis
 */
public class MultiResultConvertor {

    /**
     * 将PageResponse转换为MultiResult对象。
     *
     * @param pageResponse 分页响应对象
     * @param <T>          数据类型
     * @return 转换后的MultiResult对象
     */
    public static <T> MultiResult<T> convert(PageResponse<T> pageResponse) {
        return new MultiResult<T>(true, SUCCESS.name(),
                SUCCESS.name(), pageResponse.getDatas(), pageResponse.getTotal(),
                pageResponse.getCurrentPage(), pageResponse.getPageSize());
    }
}

