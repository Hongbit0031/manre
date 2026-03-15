package io.linfeng.common.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 热门话题列表响应
 */
@Data
@Schema(description = "热门话题响应体")
public class AppDiscussSimpleResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "话题id")
    private Integer id;

    @Schema(description = "发起人id")
    private Integer uid;

    @Schema(description = "圈子id")
    private Integer topicId;

    @Schema(description = "话题标题")
    private String title;

    @Schema(description = "话题描述")
    private String introduce;

    @Schema(description = "浏览量")
    private Integer readCount;

    @Schema(description = "推荐位置：0不推荐 1首页推荐")
    private Integer topType;

    @Schema(description = "创建时间")
    private Date createTime;
}


