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
package io.linfeng.common.vo.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
@Schema(description = "用户访问统计响应体")
public class AdminActiveUserResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@Schema(description = "ID")
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
	 * 创建时间
	 */
	@Schema(description = "创建时间")
	private Date createTime;
	/**
	 * 更新时间
	 */
	@Schema(description = "更新时间")
	private Date updateTime;
	/**
	 * 访问IP地址
	 */
	@Schema(description = "访问IP地址")
	private String address;

	@Schema(description = "用户头像")
	private String avatar;

	@Schema(description = "用户名")
	private String username;

}
