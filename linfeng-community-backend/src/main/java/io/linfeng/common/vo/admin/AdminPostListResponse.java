package io.linfeng.common.vo.admin;

import io.linfeng.common.vo.app.AppUserShortInfoResponse;
import io.linfeng.common.vo.app.VoteInfoResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Data
@Schema(description = "帖子列表响应体")
public class AdminPostListResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@Schema(description = "帖子id")
	private Integer id;
	/**
	 * 用户id
	 */
	@Schema(description = "用户id")
	private Integer uid;
	/**
	 * 圈子id
	 */
	@Schema(description = "圈子id")
	private Integer topicId;
	/**
	 * 话题id
	 */
	@Schema(description = "话题id")
	private Integer discussId;
	/**
	 * 投票id
	 */
	@Schema(description = "投票id")
	private Integer voteId;
	/**
	 * 标题
	 */
	@Schema(description = "标题")
	private String title;
	/**
	 * 内容
	 */
	@Schema(description = "内容")
	private String content;
	/**
	 * 文件
	 */
//	private String media;
	@Schema(description = "文件")
	List<String> media;
	/**
	 * 浏览量
	 */
	@Schema(description = "浏览量")
	private Integer readCount;
	/**
	 * 置顶
	 */
	@Schema(description = "置顶")
	private Integer postTop;
	/**
	 * 帖子类型：1 图文 ，2视频 ，3文章，4投票
	 */
	@Schema(description = "帖子类型：1 图文 ，2视频 ，3文章，4投票")
	private Integer type;
	/**
	 * 地址名称
	 */
	@Schema(description = "地址名称")
	private String address;
	/**
	 * 经度
	 */
	@Schema(description = "经度")
	private Double longitude;
	/**
	 * 纬度
	 */
	@Schema(description = "纬度")
	private Double latitude;
	/**
	 * 创建时间
	 */
	@Schema(description = "创建时间")
	private Date createTime;

	/**
	 * 点赞数
	 */
	@Schema(description = "点赞数")
	private Integer collectionCount;
	/**
	 * 评论数
	 */
	@Schema(description = "评论数")
	private Integer commentCount;

	/**
	 * 用户信息
	 */
	@Schema(description = "用户信息")
	private AppUserShortInfoResponse userInfo;

	/**
	 * 用户头像
	 */
	@Schema(description = "用户头像")
	private String avatar;

	/**
	 * 话题
	 */
	@Schema(description = "话题")
	private String discussTitle;

	/**
	 * 动态是否点赞
	 */
	@Schema(description = "动态是否点赞")
	private Boolean isCollection;
	/**
	 * 是否关注
	 */
	@Schema(description = "是否关注")
	private Boolean isFollow;
	/**
	 * 投票
	 */
	@Schema(description = "投票")
	private VoteInfoResponse voteInfo;

	/**
	 * 圈子名称
	 */
	@Schema(description = "圈子名称")
	private String topicName;

	/**
	 * 圈子logo
	 */
	@Schema(description = "圈子logo")
	private String coverImage;

	/**
	 * 0正常 1待审核 2已拒绝
	 */
	@Schema(description = "0正常 1待审核 2已拒绝")
	private Integer status;
	/**
	 * 0 普通贴  1 付费贴  2 红包贴
	 */
	@Schema(description = "0 普通贴  1 付费贴  2 红包贴")
	private Integer cut;

	/**
	 * 付费简介
	 */
	@Schema(description = "付费简介")
	private String brief;
	/**
	 * 关联私密圈:0公开1私密
	 */
	@Schema(description = "关联私密圈:0公开1私密")
	private Integer isPrivate;

}
