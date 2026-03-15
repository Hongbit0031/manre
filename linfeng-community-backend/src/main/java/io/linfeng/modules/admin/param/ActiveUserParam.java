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
@Schema(description = "用户访问统计请求体")
public class ActiveUserParam implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@Schema(description = "ID，更新时必填")
	@NotNull(message = "ID不能为空", groups = UpdateGroup.class)
	private Long id;

	/**
	 * IP
	 */
	@Schema(description = "IP")
	private String ip;

	/**
	 * 用户ID
	 */
	@Schema(description = "用户ID")
	private Integer uid;

	/**
	 * 活跃频率
	 */
	@Schema(description = "活跃频率")
	private Integer activeCount;

	/**
	 * 访问终端：
	 * miniapp
	 * H5
	 * App
	 */
	@Schema(description = "访问终端")
	private String terminal;

	/**
	 * 访问类型:
	 * 0 未登录游客
	 * 1 登录用户
	 */
	@Schema(description = "访问类型")
	private Integer type;

	/**
	 * 访问IP地址
	 */
	@Schema(description = "访问IP地址")
	private String address;

}

