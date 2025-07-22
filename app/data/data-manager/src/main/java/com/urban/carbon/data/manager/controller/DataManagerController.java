package com.urban.carbon.data.manager.controller;

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

    @PostMapping("/initCreate")
    public Result<DataInfo> initCreateDataInfo(
            @RequestParam String dataName, @RequestParam String dataDesc, @RequestParam String dataType,
            @RequestParam Long dataSourceId, @RequestParam Long fileSize) {
//        String loginId = (String) StpUtil.getLoginId();
        String loginId = "45";
        DataCreateRequest request = new DataCreateRequest();
        request.createRequest(dataName, dataDesc, fileSize, dataType,
                dataSourceId, Long.valueOf(loginId));
        OperateResponse<DataInfo> response = dataFacadeService.initCreateData(request);
        return Result.success(response.getData());
    }
}
