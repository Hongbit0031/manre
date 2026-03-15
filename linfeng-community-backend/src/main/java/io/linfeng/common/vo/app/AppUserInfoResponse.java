package io.linfeng.common.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
@Schema(description = "用户主页信息响应体")
public class AppUserInfoResponse implements Serializable {
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
	 * 状态 0正常 1禁用 2禁言 3已注销
	 */
	@Schema(description = "状态 0正常 1禁用 2禁言 3已注销")
	private Integer status;
	/**
	 * 城市
	 */
	@Schema(description = "城市")
	private String city;
	/**
	 * 个性签名
	 */
	@Schema(description = "个性签名")
	private String intro;
	/**
	 * 用户标签
	 */
	@Schema(description = "用户标签")
	private List<String> tagStr;
	/**
	 * 0为普通用户  1官方账号 2马甲虚拟用户
	 */
	@Schema(description = "0为普通用户  1官方账号 2马甲虚拟用户")
	private Integer type;
	/**
	 * 创建的圈子
	 */
	@Schema(description = "创建的圈子")
	private List<TopicListResponse> createTopicList;
	/**
	 * 关注
	 */
	@Schema(description = "关注")
	private Integer follow;
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
	 * 是否为好友
	 */
	@Schema(description = "是否为好友")
	private Boolean isFriend;
	/**
	 * 隐藏粉丝
	 */
	@Schema(description = "隐藏粉丝")
	private Boolean isFan;
	/**
	 * 隐藏关注
	 */
	@Schema(description = "隐藏关注")
	private Boolean isWatch;
	/**
	 * 隐藏作品
	 */
	@Schema(description = "隐藏作品")
	private Boolean isPost;
	/**
	 * 用户等级
	 */
	@Schema(description = "用户等级")
	private Integer level;
	/**
	 * 是否开启好友聊天模块
	 */
	@Schema(description = "是否开启好友聊天模块")
	private Boolean socialOpen;
	/**
	 * 背景图
	 */
	@Schema(description = "背景图")
	private String bgImg;
}
