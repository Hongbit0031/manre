package io.linfeng.common.vo.app;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 好友列表响应体
 */
@Data
@Schema(description = "好友列表响应体")
public class AppFriendResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "好友关系ID")
    @JsonProperty("id")
    private Integer id;

    @Schema(description = "好友用户ID")
    @JsonProperty("friend_id")
    private Integer friendId;

    @Schema(description = "好友备注")
    @JsonProperty("notation")
    private String notation;

    @Schema(description = "会话ID")
    @JsonProperty("session_id")
    private Long sessionId;

    @Schema(description = "最后一条消息")
    @JsonProperty("last_message")
    private String lastMessage;

    @Schema(description = "未读数量")
    @JsonProperty("unread")
    private Integer unread;

    @Schema(description = "是否隐藏会话")
    @JsonProperty("is_hidden")
    private Boolean isHidden;

    @Schema(description = "好友头像")
    @JsonProperty("avatar")
    private String avatar;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("update_time")
    private LocalDateTime updateTime;
}


