package io.linfeng.modules.sys.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 系统菜单参数类
 */
@Schema(description = "系统菜单请求体")
@Data
public class SysMenuParam {

    @Schema(description = "菜单ID，更新时必填")
    private Long menuId;

    @Schema(description = "父菜单ID，一级菜单为0", required = true)
    @NotNull(message = "上级菜单不能为空")
    private Long parentId;

    @Schema(description = "菜单名称", required = true)
    @NotBlank(message = "菜单名称不能为空")
    private String name;

    @Schema(description = "菜单URL")
    private String url;

    @Schema(description = "授权码(格式举例：user:list,user:create)")
    private String perms;

    @Schema(description = "类型：0目录、1菜单、2按钮", required = true)
    @NotNull(message = "菜单类型不能为空")
    private Integer type;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "排序")
    private Integer orderNum;

}

