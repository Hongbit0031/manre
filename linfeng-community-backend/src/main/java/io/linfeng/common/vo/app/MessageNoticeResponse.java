package io.linfeng.common.vo.app;

import io.linfeng.modules.admin.entity.AppUserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author linfeng
 * @date 2022/2/8 21:25
 */
@Data
@Schema(description = "系统推送消息")
public class MessageNoticeResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "消息id")
    private Integer mId;
    /**
     * 发送者uid
     */
    @Schema(description = "发送者uid")
    private Integer fromUid;
    /**
     * 接收者uid
     */
    @Schema(description = "接收者uid")
    private Integer toUid;
    /**
     * 帖子id
     */
    @Schema(description = "帖子id")
    private Integer postId;
    /**
     * 推送标题
     */
    @Schema(description = "推送标题")
    private String title;
    /**
     * 消息内容
     */
    @Schema(description = "消息内容")
    private String content;
    /**
     * 0未读，1已读
     */
    @Schema(description = "0未读，1已读")
    private Integer status;
    /**
     * 1为点赞，2为评论  3为收藏 4为关注  5为系统通知
     */
    @Schema(description = "1为点赞，2为评论  3为收藏 4为关注  5为系统通知")
    private Integer type;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "用户信息")
    private AppUserEntity userInfo;

}
