package io.linfeng.common.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


@Data
@Schema(description = "圈内帖子响应对象")
public class TopicUserShortResponse implements Serializable {
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
	 * 0为普通用户  1官方账号 2马甲虚拟用户
	 */
	@Schema(description = "0为普通用户  1官方账号 2马甲虚拟用户")
	private Integer type;


	/**
	 * 是否被拉黑
	 */
	@Schema(description = "是否被拉黑")
	private Boolean isBlock;

	/**
	 * 用户等级
	 */
	@Schema(description = "用户等级")
	private Integer level;
}
