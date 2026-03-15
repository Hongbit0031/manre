package io.linfeng.modules.sys.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(description = "密码修改请求体")
@Data
public class PasswordForm {

    @Schema(description = "原密码", required = true)
    private String password;

    @Schema(description = "新密码", required = true)
    private String newPassword;

}
