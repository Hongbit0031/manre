
package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "更新私聊消息状态请求体")
public class UpdateChatStatusForm {

    @Schema(description = "用户id")
    private String uid;

}
