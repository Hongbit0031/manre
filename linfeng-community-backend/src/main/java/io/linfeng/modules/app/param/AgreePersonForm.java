package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "同意好友请求体")
public class AgreePersonForm {


    @Schema(description = "uid")
    private Integer id;


}
