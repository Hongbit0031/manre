package io.linfeng.common.vo.admin;

import io.linfeng.modules.admin.entity.AppUserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@Data
@Schema(description = "账单记录响应体")
public class AdminBillInfoResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@Schema(description = "ID")
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
	private Date addTime;
	/**
	 * 0待确定 1有效 -1无效
	 */
	@Schema(description = "0待确定 1有效 -1无效")
	private Integer status;

	@Schema(description = "用户信息")
	private AppUserEntity user;

	@Schema(description = "头像")
	private String avatar;

	@Schema(description = "昵称")
	private String username;
}
