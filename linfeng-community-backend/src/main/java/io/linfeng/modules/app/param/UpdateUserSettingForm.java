package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "更新用户隐私设置")
public class UpdateUserSettingForm {

    @Schema(description = "隐藏粉丝")
    private Boolean isFan;

    @Schema(description = "隐藏关注")
    private Boolean isWatch;

    @Schema(description = "隐藏作品")
    private Boolean isPost;

}
