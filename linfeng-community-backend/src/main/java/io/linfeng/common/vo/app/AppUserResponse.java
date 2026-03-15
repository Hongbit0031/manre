package io.linfeng.common.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Data
@Schema(description = "用户信息响应体")
public class AppUserResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户id
	 */
	@Schema(description = "用户id")
	private Integer uid;
	/**
	 * 手机号
	 */
	@Schema(description = "手机号")
	private String mobile;
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
	 * 省份
	 */
	@Schema(description = "省份")
	private String province;
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
	 * 积分
	 */
	@Schema(description = "积分")
	private Integer integral;
	/**
	 * 余额
	 */
	@Schema(description = "余额")
	private BigDecimal money;
	/**
	 * 最后登录ip
	 */
	@Schema(description = "最后登录ip")
	private String lastLoginIp;
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
	 * 邮箱
	 */
	@Schema(description = "邮箱")
	private String email;
	/**
	 * 会员过期时间
	 */
	@Schema(description = "会员过期时间")
	private Date vipExpireTime;
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
	/**
	 * 背景图
	 */
	@Schema(description = "背景图")
	private String bgImg;
}
