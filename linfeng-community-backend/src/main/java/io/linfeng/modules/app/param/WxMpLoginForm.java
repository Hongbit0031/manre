package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "微信公众号登录表单")
public class WxMpLoginForm {

    @Schema(description = "code", required = true)
    private String code;

}
