package io.linfeng.modules.admin.param;

import io.linfeng.common.validator.group.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 帖子管理更新参数
 *
 * @author linfeng
 */
@Data
@Schema(description = "帖子管理更新请求体")
public class PostParam {

    @Schema(description = "帖子ID，更新时必填")
    @NotNull(message = "帖子ID不能为空", groups = UpdateGroup.class)
    private Integer id;

    @Schema(description = "用户ID")
    private Integer uid;

    @Schema(description = "话题ID")
    private Integer topicId;

    @Schema(description = "讨论ID")
    private Integer discussId;

    @Schema(description = "投票ID")
    private Integer voteId;

    @Schema(description = "帖子标题")
    private String title;

    @Schema(description = "帖子内容")
    private String content;

    @Schema(description = "媒体资源地址，逗号分隔")
    private String media;

    @Schema(description = "阅读量")
    private Integer readCount;

    @Schema(description = "是否置顶：0否1是")
    private Integer postTop;

    /**
     * 帖子类型：1图文 2视频 3文章 4投票
     */
    @Schema(description = "帖子类型：1图文 2视频 3文章 4投票")
    private Integer type;

    @Schema(description = "定位地址")
    private String address;

    @Schema(description = "经度")
    private Double longitude;

    @Schema(description = "纬度")
    private Double latitude;

    /**
     * 0正常 1待审核 2已拒绝
     */
    @Schema(description = "帖子状态：0正常 1待审核 2已拒绝")
    private Integer status;

    /**
     * 0 普通贴  1 付费贴
     */
    @Schema(description = "是否付费贴：0普通贴 1付费贴")
    private Integer cut;

    /**
     * 付费贴支付金额
     */
    @Schema(description = "付费贴支付金额")
    private BigDecimal pay;

    /**
     * 付费简介
     */
    @Schema(description = "付费简介")
    private String brief;

    /**
     * 关联私密圈:0公开1私密
     */
    @Schema(description = "是否关联私密圈：0公开 1私密")
    private Integer isPrivate;
}


