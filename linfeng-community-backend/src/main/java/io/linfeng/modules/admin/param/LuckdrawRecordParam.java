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
@Schema(description = "抽奖记录请求体")
public class LuckdrawRecordParam implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Schema(description = "ID，更新时必填")
    @NotNull(message = "ID不能为空", groups = UpdateGroup.class)
    private Integer id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Integer userId;

    /**
     * 奖品ID
     */
    @Schema(description = "奖品ID")
    private Integer prizeId;

    /**
     * 奖品类型
     */
    @Schema(description = "奖品类型")
    private Integer prizeType;

    /**
     * 奖品名称
     */
    @Schema(description = "奖品名称")
    private String prizeName;

    /**
     * 奖品图片
     */
    @Schema(description = "奖品图片")
    private String prizeImage;

    /**
     * 获得数量
     */
    @Schema(description = "获得数量")
    private Integer number;

    /**
     * 抽奖时间
     */
    @Schema(description = "抽奖时间")
    private Date createTime;
}

