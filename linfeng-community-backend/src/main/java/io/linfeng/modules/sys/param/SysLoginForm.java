package io.linfeng.modules.sys.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "登录请求体")
public class SysLoginForm {

    @Schema(description = "用户名", required = true)
    private String username;

    @Schema(description = "密码", required = true)
    private String password;

    @Schema(description = "验证码", required = true)
    private String captcha;

    @Schema(description = "UUID", required = true)
    private String uuid;

}
