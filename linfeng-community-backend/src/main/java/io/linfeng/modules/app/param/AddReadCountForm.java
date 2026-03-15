package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "视频浏览量")
public class AddReadCountForm {

    @Schema(description = "帖子id")
    private Integer postId;


}
