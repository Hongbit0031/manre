package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "删除指定好友")
public class DeleteFriendForm {

    @Schema(description = "用户id")
    private Integer id;

}
