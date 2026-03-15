package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "拉黑请求体")
public class BlockForm {

    @Schema(description = "用户id")
    private Integer uid;

    @Schema(description = "圈子id")
    private Integer topicId;
}
