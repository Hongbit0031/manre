package io.linfeng.modules.admin.param;

import io.linfeng.common.validator.group.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author linfeng
 * @date 2026/2/27 11:00
 */
@Data
@Schema(description = "私信请求体")
public class ChatMessageParam  implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "id，更新时必填")
    @NotNull(message = "id不能为空", groups = UpdateGroup.class)
    private String id;
    /**
     * 用户session
     */
    @Schema(description = "用户session")
    private String sessionId;
    /**
     * 发送者id
     */
    @Schema(description = "发送者id")
    private String senderId;
    /**
     * 接收者id
     */
    @Schema(description = "接收者id")
    private String receiverId;
    /**
     * 发送时间
     */
    @Schema(description = "发送时间")
    private String sendTime;
    /**
     * 内容
     */
    @Schema(description = "内容")
    private String content;
    /**
     * 类型
     */
    @Schema(description = "类型")
    private String messageType;
    /**
     * 是否撤回
     */
    @Schema(description = "是否撤回")
    private Integer isWithdrawn;
    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private Date updateTime;
}
