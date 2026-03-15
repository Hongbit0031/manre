package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "申请成为好友")
public class ApplyFriendForm {


    @Schema(description = "消息")
    private Object data;


}
