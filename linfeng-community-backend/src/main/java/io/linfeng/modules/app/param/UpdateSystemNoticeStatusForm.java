
package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "更新系统通知消息状态请求体")
public class UpdateSystemNoticeStatusForm {

    @Schema(description = "消息id")
    private Integer id;

}
