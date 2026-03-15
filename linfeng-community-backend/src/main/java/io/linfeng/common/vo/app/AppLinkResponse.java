package io.linfeng.common.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 移动端轮播图响应
 */
@Data
@Schema(description = "移动端轮播图响应体")
public class AppLinkResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "轮播图ID")
    private Integer id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "跳转路径")
    private String url;

    @Schema(description = "展示图片")
    private String img;

    @Schema(description = "跳转类型：3页面内 1外链")
    private Integer type;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "所属分类：0圈子页 1个人页")
    private Integer cateId;
}

