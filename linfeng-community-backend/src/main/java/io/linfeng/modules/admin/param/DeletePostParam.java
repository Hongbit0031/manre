package io.linfeng.modules.admin.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "删除帖子")
public class DeletePostParam {

    @Schema(description = "帖子id")
    private Integer id;

    @Schema(description = "是否发送消息给用户 1发送0不发送")
    private Integer isSendDeleteInfo;

    @Schema(description = "原因")
    private String deleteReason;

}
