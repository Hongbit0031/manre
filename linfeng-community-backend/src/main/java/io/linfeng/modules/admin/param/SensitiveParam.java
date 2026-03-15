/**
 * -----------------------------------
 * Copyright (c) 2022-2026
 * All rights reserved, Designed By www.linfengtech.cn
 * 林风社交论坛商业版本请务必保留此注释头信息
 * 商业版授权联系技术客服	 QQ:  3582996245
 * 严禁分享、盗用、转卖源码或非法牟利！
 * 版权所有 ，侵权必究！
 * -----------------------------------
 */
package io.linfeng.modules.admin.param;

import io.linfeng.common.validator.group.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "敏感词库请求体")
public class SensitiveParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键，更新时必填")
    @NotNull(message = "ID不能为空", groups = UpdateGroup.class)
    private Long id;

    @Schema(description = "敏感词")
    private String sensitiveWord;

    @Schema(description = "是否开启 1-是 0-否")
    private Integer state;

    @Schema(description = "处理措施 1-禁止发布 2-需审核")
    private Integer handleMeasures;
}


