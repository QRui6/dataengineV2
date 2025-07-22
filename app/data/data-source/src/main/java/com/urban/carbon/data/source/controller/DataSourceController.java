package com.urban.carbon.data.source.controller;

import com.urban.carbon.api.data.source.response.data.DataSourceInfo;
import com.urban.carbon.web.vo.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/data/source")
public class DataSourceController {

    @GetMapping("/test")
    public String test() {
        return "Data Source Test";
    }

    @PostMapping("/add")
    public Result<DataSourceInfo> createDataSource() {
        return null;
    }

    @GetMapping("/query")
    public Result<DataSourceInfo> queryDataSource() {
        return null;
    }
}
