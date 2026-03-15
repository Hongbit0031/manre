package io.linfeng.modules.admin.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.linfeng.common.validator.group.UpdateGroup;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户菜单请求体
 */
@Data
@Schema(description = "用户菜单请求体")
public class UserMenuParam implements Serializable {

    @Schema(description = "主键，更新时必填")
    @NotNull(message = "ID不能为空", groups = UpdateGroup.class)
    private Integer id;

    @Schema(description = "跳转路径")
    private String url;

    @Schema(description = "图片地址")
    private String img;

    @Schema(description = "菜单名称")
    private String name;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态 0正常 1下架")
    private Integer status;
}

