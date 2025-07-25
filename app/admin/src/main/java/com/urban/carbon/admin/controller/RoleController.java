package com.urban.carbon.admin.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.urban.carbon.admin.domain.service.RoleService;
import com.urban.carbon.admin.params.RoleModifiedParam;
import com.urban.carbon.api.user.exception.UserException;
import com.urban.carbon.api.admin.response.data.RoleInfo;
import com.urban.carbon.base.response.MultiResponse;
import com.urban.carbon.base.response.OperateResponse;
import com.urban.carbon.web.vo.MultiResult;
import com.urban.carbon.web.vo.Result;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * 获取所有角色信息
     * <p>
     * 此接口用于分页查询系统中的所有角色信息，包括角色的详细权限和属性
     * 它通过接收页码和页面大小参数，返回对应的角色列表以及总角色数
     *
     * @return 返回一个MultiResult对象，其中包含角色信息列表、总角色数、每页数量和当前页码
     */
    @GetMapping("/allRole")
    public MultiResult<RoleInfo> getAllRole() {
        MultiResponse<RoleInfo> activeRole = roleService.getAllRole();
        return MultiResult.success(activeRole.getDatas());
    }

    /**
     * 创建一个新的角色。
     *
     * @param param 包含新角色信息的请求对象
     * @return 创建结果
     * @throws UserException 如果创建失败
     */
    @PostMapping("/create")
    public Result<RoleInfo> createRole(@Valid @RequestBody RoleModifiedParam param) {
        String loginId = (String) StpUtil.getLoginId();
        OperateResponse<RoleInfo> result = roleService.roleCreate(
                param.getRolePermission(), param.getRoleName(), param.getRoleDesc(),
                param.getRoleActive(), Long.valueOf(loginId));
        return Result.success(result.getData());
    }

    /**
     * 修改一个已存在的角色。
     *
     * @param param 包含要修改的角色信息的请求对象
     * @return 修改结果
     * @throws UserException 如果修改失败
     */
    @PostMapping("/modify")
    public Result<RoleInfo> modifyRole(@Valid @RequestBody RoleModifiedParam param) {
        String loginId = (String) StpUtil.getLoginId();
        OperateResponse<RoleInfo> roleInfoOperateResponse = roleService.modifyRole(
                param.getId(), param.getRoleName(), param.getRoleActive(), param.getRolePermission(),
                param.getRoleDesc(), Long.valueOf(loginId));
        return Result.success(roleInfoOperateResponse.getData());
    }

    /**
     * 删除一个角色。
     *
     * @param roleId 包含要删除的角色ID的请求对象
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public Result<Boolean> deleteRole(@RequestParam Long roleId) {
        String loginId = (String) StpUtil.getLoginId();
        OperateResponse<Boolean> result = roleService.deleteRole(roleId, Long.valueOf(loginId));
        return Result.success(result.getData());
    }
}
