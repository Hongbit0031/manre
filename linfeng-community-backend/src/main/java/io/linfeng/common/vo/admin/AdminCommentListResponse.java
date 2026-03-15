package io.linfeng.common.vo.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Schema(description = "评论列表响应体")
public class AdminCommentListResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@Schema(description = "ID")
	private Long id;
	/**
	 * 父级id
	 */
	@Schema(description = "父级id")
	private Integer pid;
	/**
	 * 评论类型:1帖子
	 */
	@Schema(description = "评论类型")
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
	 * 评论图片
	 */
	@Schema(description = "评论图片")
	private String img;
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

	@Schema(description = "评论用户头像")
	private String avatar;

	@Schema(description = "评论用户名")
	private String username;

	@Schema(description = "被回复用户头像")
	private String toUserAvatar;

	@Schema(description = "被回复用户用户名")
	private String toUsername;

	@Schema(description = "父评论内容")
	private String parentComment;

	@Schema(description = "父评论图片")
	private String parentImg;

}
