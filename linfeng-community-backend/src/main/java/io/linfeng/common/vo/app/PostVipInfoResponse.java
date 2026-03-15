package io.linfeng.common.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@Schema(description = "付费帖子内容")
public class PostVipInfoResponse implements Serializable {
	private static final long serialVersionUID = 1L;


	/**
	 * 标题
	 */
	@Schema(description = "标题")
	private String title;


	/**
	 * 付费贴支付金额
	 */
	@Schema(description = "付费贴支付金额")
	private BigDecimal pay;


	/**
	 * 是否已购买
	 */
	@Schema(description = "是否已购买")
	private Boolean isBuy;

	/**
	 * 付费简介
	 */
	@Schema(description = "付费简介")
	private String brief;


}
