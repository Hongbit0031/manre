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
 * APP版本管理
 * 
 * @author JL.Yu
 * @email linfengtech001@163.com
 * @date 2025-03-14 12:55:42
 */
@Data
@TableName("lf_app_version")
public class AppVersionEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	@TableId
	private Integer id;
	/**
	 * 版本号(如1.0.1)
	 */
	private String version;
	/**
	 * 最低要求版本号(用于强制更新判断)
	 */
	private String minVersion;
	/**
	 * 是否强制更新(1:强制更新,0:非强制)
	 */
	private Integer isForce;
	/**
	 * 更新内容描述
	 */
	private String content;
	/**
	 * 通用下载地址
	 */
	private String downloadUrl;
	/**
	 * Android应用市场地址
	 */
	private String androidUrl;
	/**
	 * iOS应用市场地址
	 */
	private String iosUrl;
	/**
	 * 版本状态(1:启用,0:禁用)
	 */
	private Integer status;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
