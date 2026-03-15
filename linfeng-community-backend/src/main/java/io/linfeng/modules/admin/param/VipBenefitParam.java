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
@Schema(description = "会员权益请求体")
public class VipBenefitParam implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Schema(description = "主键ID，更新时必填")
    @NotNull(message = "ID不能为空", groups = UpdateGroup.class)
    private Integer id;

    /**
     * 会员权益标题
     */
    @Schema(description = "会员权益标题")
    private String title;

    /**
     * 会员权益描述
     */
    @Schema(description = "会员权益描述")
    private String describes;

    /**
     * 图标
     */
    @Schema(description = "图标")
    private String icon;

    /**
     * 状态0有效1无效
     */
    @Schema(description = "状态(0:有效,1:无效)")
    private Integer status;

    /**
     * 排序
     */
    @Schema(description = "排序值，数字越小越靠前")
    private Integer sort;
}

