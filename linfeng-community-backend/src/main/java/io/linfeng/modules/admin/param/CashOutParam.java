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
import java.util.Date;


@Data
@Schema(description = "提现请求体")
public class CashOutParam implements Serializable {
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
	private Integer uid;

	/**
	 * 申请提现金额
	 */
	@Schema(description = "申请提现金额")
	private BigDecimal moneyNumber;

	/**
	 * 收款码
	 */
	@Schema(description = "收款码")
	private String url;

	/**
	 * 审核反馈
	 */
	@Schema(description = "审核反馈")
	private String feedback;

	/**
	 * 状态 0待审核 1已完成 2已驳回
	 */
	@Schema(description = "状态 0待审核 1已完成 2已驳回")
	private Integer status;

	/**
	 * 打款类型 0支付宝1微信
	 */
	@Schema(description = "打款类型 0支付宝1微信")
	private Integer type;

	/**
	 * 创建时间
	 */
	@Schema(description = "创建时间")
	private Date createTime;

	/**
	 * 修改时间
	 */
	@Schema(description = "修改时间")
	private Date updateTime;

}

