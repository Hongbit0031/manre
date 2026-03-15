package io.linfeng.modules.sys.param;

import io.linfeng.common.validator.group.AddGroup;
import io.linfeng.common.validator.group.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 系统用户参数类
 */
@Schema(description = "系统用户请求体")
@Data
public class SysUserParam {

    @Schema(description = "用户ID，更新时必填")
    private Long userId;

    @Schema(description = "用户名", required = true)
    @NotBlank(message = "用户名不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String username;

    @Schema(description = "密码，新增时必填")
    @NotBlank(message = "密码不能为空", groups = AddGroup.class)
    private String password;

    @Schema(description = "邮箱", required = true)
    @NotBlank(message = "邮箱不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Email(message = "邮箱格式不正确", groups = {AddGroup.class, UpdateGroup.class})
    private String email;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "状态  0：禁用   1：正常")
    private Integer status;

    @Schema(description = "角色ID列表")
    private List<Long> roleIdList;

}

