package io.linfeng.common.vo.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author linfeng
 * @date 2026/2/27 11:42
 */
@Data
@Schema(description = "私信消息列表响应体")
public class AdminChatMessageResponse {

    @Schema(description = "id")
    private String id;

    @Schema(description = "用户session")
    private String sessionId;

    @Schema(description = "发送者id")
    private String senderId;

    @Schema(description = "发送者用户名")
    private String senderUsername;

    @Schema(description = "发送者头像")
    private String senderAvatar;

    @Schema(description = "接收者id")
    private String receiverId;

    @Schema(description = "接收者用户名")
    private String receiverUsername;

    @Schema(description = "接收者头像")
    private String receiverAvatar;

    @Schema(description = "发送时间")
    private String sendTime;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "类型")
    private String messageType;

    @Schema(description = "是否撤回")
    private Integer isWithdrawn;

    @Schema(description = "更新时间")
    private Date updateTime;
}
