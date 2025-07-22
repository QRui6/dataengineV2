package com.urban.carbon.data.domain.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.urban.carbon.api.data.manager.request.DataCreateRequest;
import com.urban.carbon.data.domain.entity.Data;
import com.urban.carbon.data.infrastructure.mapper.DataMapper;
import org.springframework.stereotype.Service;

@Service
public class DataService extends ServiceImpl<DataMapper, Data> {

    public Data createData(DataCreateRequest request) {
        // 首先判断数据源是否存在

        // 创建数据实体类

        // 插入记录

        // 记录操作

        // 创建返回

        return null;
    }

}
