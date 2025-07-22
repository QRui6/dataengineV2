package com.urban.carbon.data.source.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.urban.carbon.api.data.source.request.DataSourceQueryRequest;
import com.urban.carbon.api.data.source.response.data.DataSourceInfo;
import com.urban.carbon.api.data.source.service.DataSourceFacadeService;
import com.urban.carbon.data.source.domain.service.DataSourceService;
import com.urban.carbon.web.vo.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/data/source")
public class DataSourceController {

    private final DataSourceFacadeService dataSourceFacadeService;

    public DataSourceController(DataSourceFacadeService dataSourceFacadeService) {
        this.dataSourceFacadeService = dataSourceFacadeService;
    }

    @GetMapping("/test")
    public String test() {
        return "Data Source Test";
    }

    @PostMapping("/add")
    public Result<DataSourceInfo> createDataSource() {
        return null;
    }

    @GetMapping("/query")
    public Result<DataSourceInfo> queryDataSource(@RequestParam Long dataSourceId,
                                                  @RequestParam String dataSourceName) {
//        String loginId = (String) StpUtil.getLoginId();
        String loginId = "45";
        DataSourceQueryRequest request;
        if (dataSourceId != null) {
            request = new DataSourceQueryRequest(dataSourceId, Long.valueOf(loginId));
        } else if (dataSourceName != null) {
            request = new DataSourceQueryRequest(dataSourceName, Long.valueOf(loginId));
        } else {
            throw new IllegalArgumentException("dataSourceId or dataSourceName must be not null");
        }
        return Result.success(dataSourceFacadeService.queryDataSource(request).getData());
    }
}
