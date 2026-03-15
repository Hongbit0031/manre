
package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "评论点赞")
public class AddThumbsForm {

    @Schema(description = "评论id")
    private Integer id;

}
