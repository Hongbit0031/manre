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
import java.util.Date;

@Data
@Schema(description = "抽奖物品请求体")
public class LuckdrawParam implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Schema(description = "ID，更新时必填")
    @NotNull(message = "ID不能为空", groups = UpdateGroup.class)
    private Integer id;

    /**
     * 奖品类型[1积分2谢谢惠顾3红包4自定义物品]
     */
    @Schema(description = "奖品类型[1积分2谢谢惠顾3红包4自定义物品]")
    private Integer prizeType;

    /**
     * 名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 图片
     */
    @Schema(description = "图片")
    private String image;

    /**
     * 奖品数量
     */
    @Schema(description = "奖品数量")
    private Integer number;

    /**
     * 抽奖几率
     */
    @Schema(description = "抽奖几率")
    private Double probability;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sort;

    /**
     * 抽奖状态[0禁用1使用]
     */
    @Schema(description = "抽奖状态[0禁用1使用]")
    private Integer status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private Date updateTime;
}


