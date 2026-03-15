package io.linfeng.common.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 移动端用户菜单响应
 */
@Data
@Schema(description = "移动端用户菜单响应体")
public class AppUserMenuResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "菜单ID")
    private Integer id;

    @Schema(description = "跳转路径")
    private String url;

    @Schema(description = "图片地址")
    private String img;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "排序")
    private Integer sort;
}


