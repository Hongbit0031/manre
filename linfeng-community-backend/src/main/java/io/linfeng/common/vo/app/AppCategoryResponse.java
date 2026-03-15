package io.linfeng.common.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 移动端分类列表响应
 */
@Data
@Schema(description = "移动端分类响应体")
public class AppCategoryResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "分类id")
    private Integer cateId;

    @Schema(description = "分类名称")
    private String cateName;

    @Schema(description = "是否推荐(1推荐)")
    private Integer isTop;

    @Schema(description = "分类封面图")
    private String coverImage;
}

