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
package io.linfeng.modules.admin.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户访问统计
 * 
 * @author JL.Yu
 * @email linfengtech001@163.com
 * @date 2024-05-29 12:25:03
 */
@Data
@TableName("lf_active_user")
public class ActiveUserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * IP
	 */
	private String ip;
	/**
	 * 用户ID
	 */
	private Integer uid;
	/**
	 * 活跃频率
	 */
	private Integer activeCount;
	/**
	 * 访问终端：
	 * miniapp
	 * H5
	 * App
	 */
	private String terminal;
	/**
	 * 访问类型:
	 * 0 未登录游客
	 * 1 登录用户
	 */
	private Integer type;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 访问IP地址
	 */
	private String address;

}
