package com.urban.carbon.data.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.urban.carbon.api.data.manager.request.DataCreateRequest;
import com.urban.carbon.api.data.manager.response.data.DataInfo;
import com.urban.carbon.api.data.manager.service.DataFacadeService;
import com.urban.carbon.base.response.OperateResponse;
import com.urban.carbon.web.vo.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/data/manager")
public class DataManagerController {

    private final DataFacadeService dataFacadeService;

    public DataManagerController(DataFacadeService dataFacadeService) {
        this.dataFacadeService = dataFacadeService;
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @PostMapping("/add")
    public Result<DataInfo> createDataInfo(
            @RequestParam String dataName, @RequestParam String dataDesc,
            @RequestParam Long dataSourceId) {
        String loginId = (String) StpUtil.getLoginId();
        DataCreateRequest request = new DataCreateRequest();
        request.createRequest(dataName, dataDesc, dataSourceId, Long.valueOf(loginId));
        OperateResponse<DataInfo> response = dataFacadeService.createData(request);
        return Result.success(response.getData());
    }
}
