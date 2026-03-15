
package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "投票选项")
public class VoteOptionForm {

    @Schema(description = "值")
    private String value;

}
