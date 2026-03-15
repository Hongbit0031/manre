package io.linfeng.modules.sys.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 系统配置参数类
 */
@Schema(description = "系统配置请求体")
@Data
public class SysConfigParam {

    @Schema(description = "配置ID，更新时必填")
    private Long id;

    @Schema(description = "参数名", required = true)
    @NotBlank(message = "参数名不能为空")
    private String paramKey;

    @Schema(description = "参数值", required = true)
    @NotBlank(message = "参数值不能为空")
    private String paramValue;

    @Schema(description = "备注")
    private String remark;

}

