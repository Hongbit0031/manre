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
@Schema(description = "评论请求体")
public class CommentParam implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@Schema(description = "id，更新时必填")
	@NotNull(message = "id不能为空", groups = UpdateGroup.class)
	private Long id;

	/**
	 * 父级id
	 */
	@Schema(description = "父级id")
	private Integer pid;

	/**
	 * 评论类型:1帖子
	 */
	@Schema(description = "评论类型:1帖子")
	private Integer type;

	/**
	 * 评论作者ID
	 */
	@Schema(description = "评论作者ID")
	private Long uid;

	/**
	 * 被回复用户ID
	 */
	@Schema(description = "被回复用户ID")
	private Integer toUid;

	/**
	 * 评论帖子ID
	 */
	@Schema(description = "评论帖子ID")
	private Long postId;

	/**
	 * 评论内容
	 */
	@Schema(description = "评论内容")
	private String content;

	/**
	 * 评论状态 1正常显示  0不显示 2待审核
	 */
	@Schema(description = "评论状态 1正常显示  0不显示 2待审核")
	private Integer status;

	/**
	 * 创建时间
	 */
	@Schema(description = "创建时间")
	private Date createTime;

	/**
	 * 评论图片
	 */
	@Schema(description = "评论图片")
	private String img;

}

