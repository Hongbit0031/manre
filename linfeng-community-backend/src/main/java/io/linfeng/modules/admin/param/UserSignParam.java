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
@Schema(description = "签到记录请求体")
public class UserSignParam implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @Schema(description = "主键ID，更新时必填")
    @NotNull(message = "ID不能为空", groups = UpdateGroup.class)
    private Long id;

    /**
     * 用户id
     */
    @Schema(description = "用户ID")
    private Long uid;

    /**
     * 签到说明
     */
    @Schema(description = "签到说明")
    private String title;

    /**
     * 获得积分
     */
    @Schema(description = "获得积分")
    private Integer number;

    /**
     * 剩余积分
     */
    @Schema(description = "剩余积分")
    private Integer balance;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;
}


