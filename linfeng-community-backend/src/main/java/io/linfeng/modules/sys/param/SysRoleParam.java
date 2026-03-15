package io.linfeng.modules.sys.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 系统角色参数类
 */
@Schema(description = "系统角色请求体")
@Data
public class SysRoleParam {

    @Schema(description = "角色ID，更新时必填")
    private Long roleId;

    @Schema(description = "角色名称", required = true)
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "菜单ID列表")
    private List<Long> menuIdList;

}

