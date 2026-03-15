package io.linfeng.common.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Jl.Yu
 * @email linfengtech001@163.com
 */
@Data
@Schema(description = "用户排行榜单响应体")
public class AppUserRankResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户id
	 */
	@Schema(description = "用户id")
	private Integer uid;

	/**
	 *
	 */
	@Schema(description = "用户名")
	private String username;

	/**
	 * 头像
	 */
	@Schema(description = "头像")
	private String avatar;

	/**
	 * 性别(0未知，1男，2女)
	 */
	@Schema(description = "性别(0未知，1男，2女)")
	private Integer gender;

	/**
	 * 个性签名
	 */
	@Schema(description = "个性签名")
	private String intro;

	/**
	 * 贴子数
	 */
	@Schema(description = "贴子数")
	private Integer postNumber;

}
