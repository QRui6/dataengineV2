package com.urban.carbon.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.urban.carbon.user.domain.entity.Role;
import com.urban.carbon.user.domain.entity.convertor.UserConvertor;
import com.urban.carbon.user.domain.service.RoleService;
import com.urban.carbon.user.params.RoleCreateParam;
import com.urban.carbon.user.params.RoleModifiedParam;
import com.urban.carbon.api.user.response.data.RoleInfo;
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
     * @return 返回一个 {@link MultiResult} 对象，其中包含角色信息列表、总角色数、每页数量和当前页码
     */
    @GetMapping("/allRole")
    public MultiResult<RoleInfo> getAllRole() {
        MultiResponse<Role> activeRole = roleService.getAllRole();
        return MultiResult.success(UserConvertor.INSTANCE.listMapToVo(activeRole.getDatas()));
    }

    /**
     * 创建一个新的角色。
     *
     * @param param 包含新角色信息的请求对象
     * @return 创建结果
     */
    @PostMapping("/create")
    public Result<RoleInfo> createRole(@Valid @RequestBody RoleCreateParam param) {
        String loginId = (String) StpUtil.getLoginId();
        OperateResponse<Role> result = roleService.roleCreate(
                param.getRoleName(), param.getRoleDesc(),
                param.convertor(), Long.valueOf(loginId));
        return Result.success(
                UserConvertor.INSTANCE.mapToVo(result.getData()));
    }

    /**
     * 修改一个已存在的角色。
     *
     * @param param 包含要修改的角色信息的请求对象
     * @return 修改结果
     */
    @PostMapping("/modify")
    public Result<RoleInfo> modifyRole(@Valid @RequestBody RoleModifiedParam param) {
        String loginId = (String) StpUtil.getLoginId();
        OperateResponse<Role> response = roleService.modifyRole(
                param.getId(), param.getRoleName(), param.getRoleDesc(),
                param.convertor(), param.getRoleActive(),
                Long.valueOf(loginId));
        return Result.success(
                UserConvertor.INSTANCE.mapToVo(response.getData()));
    }

    /**
     * 删除一个角色。
     *
     * @param roleId 包含要删除的角色ID的请求对象
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public Result<RoleInfo> deleteRole(@RequestParam Long roleId) {
        String loginId = (String) StpUtil.getLoginId();
        OperateResponse<Role> result = roleService.deleteRole(roleId, Long.valueOf(loginId));
        return Result.success(UserConvertor.INSTANCE.mapToVo(result.getData()));
    }
}
