package com.urban.carbon.data.manager.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.urban.carbon.api.data.manager.request.DataPageQueryRequest;
import com.urban.carbon.api.data.manager.request.DataQueryRequest;
import com.urban.carbon.api.data.manager.response.data.DataInfo;
import com.urban.carbon.api.data.manager.service.DataFacadeService;
import com.urban.carbon.base.response.PageResponse;
import com.urban.carbon.data.manager.vo.DeleteResponseVO;
import com.urban.carbon.web.util.MultiResultConvertor;
import com.urban.carbon.web.vo.MultiResult;
import com.urban.carbon.web.vo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/data/interaction")
public class DataInteractionController {

    private final DataFacadeService dataFacadeService;

    public DataInteractionController(DataFacadeService dataFacadeService) {
        this.dataFacadeService = dataFacadeService;
    }

    /**
     * 获取数据源列表
     *
     * @return 数据源列表
     */
    @GetMapping("/query")
    public MultiResult<DataInfo> queryNameList(@RequestParam Long dsId, @RequestParam Integer page,
                                               @RequestParam Integer pageSize) {
        String loginId = (String) StpUtil.getLoginId();
        DataPageQueryRequest request = new DataPageQueryRequest(dsId, page, pageSize, Long.valueOf(loginId));
        PageResponse<DataInfo> response = dataFacadeService.queryDataList(request);
        return MultiResultConvertor.convert(response);
    }

    @DeleteMapping("/delete")
    public Result<DeleteResponseVO> deleteFile(
            @RequestParam List<Long> dataIds) {
        String loginId = (String) StpUtil.getLoginId();
        DataQueryRequest dataQueryRequest = new DataQueryRequest(dataIds);
        List<Long> response = dataFacadeService.deleteData(
                dataQueryRequest, Long.valueOf(loginId)).getData();
        return Result.success(new DeleteResponseVO(response, dataIds));
    }
}
