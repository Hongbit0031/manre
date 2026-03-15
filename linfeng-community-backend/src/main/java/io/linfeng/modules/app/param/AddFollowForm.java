
package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "关注")
public class AddFollowForm {

    @Schema(description = "用户id")
    private Integer id;

}
