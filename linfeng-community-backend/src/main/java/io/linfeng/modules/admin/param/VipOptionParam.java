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
import java.math.BigDecimal;

@Data
@Schema(description = "会员充值选项请求体")
public class VipOptionParam implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Schema(description = "主键ID，更新时必填")
    @NotNull(message = "ID不能为空", groups = UpdateGroup.class)
    private Integer id;

    /**
     * 名称
     */
    @Schema(description = "充值选项名称")
    private String name;

    /**
     * 有效天数
     */
    @Schema(description = "会员有效天数")
    private Integer validDays;

    /**
     * 价格
     */
    @Schema(description = "充值价格")
    private BigDecimal price;

    /**
     * 描述
     */
    @Schema(description = "补充描述")
    private String remark;

    /**
     * 排序
     */
    @Schema(description = "排序值，数字越小越靠前")
    private Integer sort;
}

