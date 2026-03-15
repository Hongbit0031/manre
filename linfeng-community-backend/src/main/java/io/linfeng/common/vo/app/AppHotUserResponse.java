package io.linfeng.common.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


@Data
@Schema(description = "热门博主响应体")
public class AppHotUserResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户id
	 */
	@Schema(description = "用户id")
	private Integer uid;
	/**
	 * 用户名
	 */
	@Schema(description = "用户名")
	private String username;

	/**
	 * 头像
	 */
	@Schema(description = "头像")
	private String avatar;
	/**
	 * 粉丝
	 */
	@Schema(description = "粉丝")
	private Integer fans;
	/**
	 * 动态数
	 */
	@Schema(description = "动态数")
	private Integer postNum;

	/**
	 * 是否关注
	 */
	@Schema(description = "是否关注")
	private Boolean isFollow;

	/**
	 * 是否为会员 0普通用户 1会员
	 */
	@Schema(description = "是否为会员 0普通用户 1会员")
	private Integer vip;

	/**
	 * 用户等级
	 */
	@Schema(description = "用户等级")
	private Integer level;
}
