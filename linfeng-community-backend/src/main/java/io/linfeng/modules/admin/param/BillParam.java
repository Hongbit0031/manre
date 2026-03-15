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
@Schema(description = "用户账单请求体")
public class BillParam implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户账单id
	 */
	@Schema(description = "用户账单id，更新时必填")
	@NotNull(message = "ID不能为空", groups = UpdateGroup.class)
	private Integer id;

	/**
	 * 用户uid
	 */
	@Schema(description = "用户uid")
	private Integer uid;

	/**
	 * 关联id
	 */
	@Schema(description = "关联id")
	private String linkId;

	/**
	 * 0 = 支出 1 = 获得
	 */
	@Schema(description = "0 = 支出 1 = 获得")
	private Integer pm;

	/**
	 * 账单标题
	 */
	@Schema(description = "账单标题")
	private String title;

	/**
	 * 明细种类
	 */
	@Schema(description = "明细种类")
	private String category;

	/**
	 * 明细类型
	 */
	@Schema(description = "明细类型")
	private String type;

	/**
	 * 明细数字
	 */
	@Schema(description = "明细数字")
	private BigDecimal number;

	/**
	 * 剩余
	 */
	@Schema(description = "剩余")
	private BigDecimal balance;

	/**
	 * 备注
	 */
	@Schema(description = "备注")
	private String mark;

	/**
	 * 添加时间
	 */
	@Schema(description = "添加时间")
	private java.util.Date addTime;

	/**
	 * 0待确定 1有效 -1无效
	 */
	@Schema(description = "0待确定 1有效 -1无效")
	private Integer status;

}

