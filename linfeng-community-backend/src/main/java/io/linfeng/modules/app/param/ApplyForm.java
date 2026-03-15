
package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "审核进圈")
public class ApplyForm {

    @Schema(description = "id")
    private Integer id;

}
