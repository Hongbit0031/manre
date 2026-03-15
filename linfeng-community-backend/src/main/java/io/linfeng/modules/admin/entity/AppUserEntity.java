package io.linfeng.modules.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 
 * 
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-20 12:10:43
 */
@Data
@TableName("lf_user")
@JsonIgnoreProperties(value = {"password"})
public class AppUserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户id
	 */
	@TableId(value = "uid",type = IdType.AUTO)
	private Integer uid;
	/**
	 * 手机号
	 */
	private String mobile;
	/**
	 * 邮箱
	 */
	private String email;
	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 用户组
	 */
	private Integer groupId;
	/**
	 * 头像
	 */
	private String avatar;
	/**
	 * 性别(0未知，1男，2女)
	 */
	private Integer gender;
	/**
	 * 省份
	 */
	private String province;
	/**
	 * 城市
	 */
	private String city;
	/**
	 * 小程序openid
	 */
	private String openid;
	/**
	 * 公众号openid
	 */
	private String mpOpenid;
	/**
	 * unionid
	 */
	private String unionid;
	/**
	 * 状态 0正常 1禁用 2禁言 3已注销
	 */
	private Integer status;
	/**
	 * 个性签名
	 */
	private String intro;
	/**
	 * 用户余额
	 */
	private BigDecimal money;
	/**
	 * 积分
	 */
	private Integer integral;
	/**
	 * 连续签到天数
	 */
	private Integer signNum;
	/**
	 * 最后登录ip
	 */
	private String lastLoginIp;
	/**
	 * 用户标签
	 */
	private String tagStr;
	/**
	 * 会员过期时间
	 */
	private Date vipExpireTime;
	/**
	 * 是否为会员 0普通用户 1会员
	 */
	private Integer vip;
	/**
	 * 0为普通用户  1官方账号 2马甲虚拟用户
	 */
	private Integer type;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 用户等级
	 */
	private Integer level;
	/**
	 * 背景图
	 */
	private String bgImg;

}
