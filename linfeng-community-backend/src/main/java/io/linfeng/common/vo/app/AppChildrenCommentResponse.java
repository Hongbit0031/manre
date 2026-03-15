package io.linfeng.common.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-25 18:51:22
 */
@Data
@Schema(description = "子评论内容响应体")
public class AppChildrenCommentResponse implements Serializable {
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
	 * 评论状态
	 */
	@Schema(description = "评论状态")
	private Integer status;
	/**
	 * 创建时间
	 */
	@Schema(description = "创建时间")
	private Date createTime;

	/**
     * 被回复用户信息
	 */
	@Schema(description = "被回复用户信息")
	private AppUserShortInfoResponse toUser;

	/**
	 * 评论用户信息
	 */
	@Schema(description = "评论用户信息")
	private AppUserShortInfoResponse userInfo;

	/**
	 * 点赞数
	 */
	@Schema(description = "点赞数")
	private Integer thumbs;

	/**
	 * 评论是否点赞
	 */
	@Schema(description = "评论是否点赞")
	private Boolean isThumbs;
	/**
	 * 评论图片
	 */
	@Schema(description = "评论图片")
	private String img;
}
