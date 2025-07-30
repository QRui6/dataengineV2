package com.urban.carbon.data.source.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.urban.carbon.api.data.source.request.DataSourceModifiedRequest;
import com.urban.carbon.api.data.source.request.DataSourcePageQueryRequest;
import com.urban.carbon.api.data.source.request.DataSourceQueryRequest;
import com.urban.carbon.api.data.source.response.data.DataSourceInfo;
import com.urban.carbon.api.data.source.service.DataSourceFacadeService;
import com.urban.carbon.base.response.OperateResponse;
import com.urban.carbon.base.response.PageResponse;
import com.urban.carbon.base.response.QueryResponse;
import com.urban.carbon.data.source.params.DataSourceModifiedParam;
import com.urban.carbon.data.source.vo.DeleteResponseVO;
import com.urban.carbon.web.util.MultiResultConvertor;
import com.urban.carbon.web.vo.MultiResult;
import com.urban.carbon.web.vo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 创建数据源
     *
     * @param dsInfo 数据源修改参数，包含数据源名称、类型、描述等信息
     * @return Result<DataSourceInfo> 操作结果，包含创建成功的数据源信息
     */
    @PostMapping("/add")
    public Result<DataSourceInfo> createDataSource(@RequestBody DataSourceModifiedParam dsInfo) {
        // 获取当前登录用户ID
        String loginId = (String) StpUtil.getLoginId();

        // 构建数据源创建请求对象
        DataSourceModifiedRequest request = new DataSourceModifiedRequest();
        request.setDsName(dsInfo.getName());
        request.setDsDesc(dsInfo.getDescription());
        request.setLoginId(Long.valueOf(loginId));

        // 调用数据源服务创建数据源
        OperateResponse<DataSourceInfo> response = dataSourceFacadeService.createDataSource(request);
        return Result.success(response.getData());
    }


    /**
     * 修改数据源
     *
     * @param dsInfo 数据源信息
     * @return 修改结果
     */
    @PutMapping("/update")
    public Result<DataSourceInfo> update(@RequestBody DataSourceModifiedParam dsInfo) {
        String loginId = (String) StpUtil.getLoginId();
        DataSourceModifiedRequest request = new DataSourceModifiedRequest();
        request.setDsName(dsInfo.getName());
        request.setDsDesc(dsInfo.getDescription());
        request.setLoginId(Long.valueOf(loginId));
        OperateResponse<DataSourceInfo> response = dataSourceFacadeService.modifyDataSource(request);
        return Result.success(response.getData());
    }

    /**
     * 删除数据源
     *
     * @param ids 数据源ID列表
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public Result<DeleteResponseVO> delete(@RequestParam List<Long> ids) {
        String loginId = (String) StpUtil.getLoginId();
        DataSourceQueryRequest request = new DataSourceQueryRequest(ids, Long.valueOf(loginId));
        OperateResponse<List<Long>> response = dataSourceFacadeService.deleteDataSources(request);
        return Result.success(new DeleteResponseVO(response.getData(), ids));
    }

    /**
     * 获取当前登录用户所拥有的数据源列表
     *
     * @param page     页码
     * @param pageSize 每页大小
     * @param name     数据源名称
     * @return 数据源列表
     */
    @GetMapping("/query")
    public MultiResult<DataSourceInfo> pageQuery(
            @RequestParam int page, @RequestParam int pageSize, @RequestParam String name) {
        String loginId = (String) StpUtil.getLoginId();
        DataSourcePageQueryRequest request;
        if (name != null && !name.isEmpty()) {
            request = new DataSourcePageQueryRequest(page, pageSize, name, Long.valueOf(loginId));
        } else {
            request = new DataSourcePageQueryRequest(page, pageSize, Long.valueOf(loginId));
        }
        PageResponse<DataSourceInfo> pageResponse = dataSourceFacadeService.pageQuery(request);
        return MultiResultConvertor.convert(pageResponse);
    }

    /**
     * 根据ID查询数据源信息
     *
     * @param id 数据源ID
     * @return 数据源信息查询结果
     */
    @GetMapping("/query/{id}")
    public Result<DataSourceInfo> query(@PathVariable Long id) {
        // 获取当前登录用户ID
        String loginId = (String) StpUtil.getLoginId();
        // 构造数据源查询请求
        DataSourceQueryRequest request = new DataSourceQueryRequest(id, Long.valueOf(loginId));
        // 调用门面服务查询数据源信息
        QueryResponse<DataSourceInfo> pageResponse = dataSourceFacadeService.queryDataSource(request);
        return Result.success(pageResponse.getData());
    }

}
