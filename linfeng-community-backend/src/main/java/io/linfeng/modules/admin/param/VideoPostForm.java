package io.linfeng.modules.admin.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "视频帖子状态管理")
public class VideoPostForm {

    @Schema(description = "帖子状态")
    private Integer status;


}
