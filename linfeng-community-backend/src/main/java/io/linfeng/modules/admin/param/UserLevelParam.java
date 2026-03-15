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
@Schema(description = "用户经验值请求体")
public class UserLevelParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键，更新时必填")
    @NotNull(message = "ID不能为空", groups = UpdateGroup.class)
    private Integer id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "最小要求值")
    private Integer minNum;

    @Schema(description = "最大要求值")
    private Integer maxNum;

    @Schema(description = "用户等级id")
    private Integer levelId;
}

