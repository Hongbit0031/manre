
package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * app用户信息修改
 *
 */
@Data
@Schema(description = "app用户信息修改")
public class AppUserUpdateForm {

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "标签")
    private List<String> tagStr;

    @Schema(description = "个性签名")
    @Length(max = 20, message = "个性签名不能超过20个字符")
    private String intro;

    @Schema(description = "用户名")
    @Length(max = 12, message = "用户名不能超过12个字符")
    private String username;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "背景图")
    private String bgImg;

}
