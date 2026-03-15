package io.linfeng.common.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 移动端会员权益响应
 */
@Data
@Schema(description = "移动端会员权益响应体")
public class AppVipBenefitResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Integer id;

    @Schema(description = "会员权益标题")
    private String title;

    @Schema(description = "会员权益描述")
    private String describes;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "排序")
    private Integer sort;
}


