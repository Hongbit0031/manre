
package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户删除自己评论")
public class DelCommentForm {

    @Schema(description = "用户评论id")
    private Integer id;

}
