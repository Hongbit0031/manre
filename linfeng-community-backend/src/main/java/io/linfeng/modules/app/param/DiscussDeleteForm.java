
package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "话题删除请求")
public class DiscussDeleteForm {

    @Schema(description = "话题id", required = true)
    private Integer id;


}
