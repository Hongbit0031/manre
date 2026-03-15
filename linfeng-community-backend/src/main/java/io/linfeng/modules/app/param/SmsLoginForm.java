
package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "手机验证码登录")
public class SmsLoginForm {

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "验证码")
    private String code;

    @Schema(description = "邮箱")
    private String email;
}
