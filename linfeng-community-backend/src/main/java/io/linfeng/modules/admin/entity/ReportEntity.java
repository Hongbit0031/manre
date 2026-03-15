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
 * 用户举报
 * 
 * @author linfeng
 * @email 3582996245@qq.com
 * @date 2022-09-01 12:55:12
 */
@Data
@TableName("lf_report")
public class ReportEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Integer id;
	/**
	 * 文件
	 */
	private String media;
	/**
	 * 描述
	 */
	private String content;
	/**
	 * 用户id
	 */
	private Integer uid;
	/**
	 * 类型1帖子 2评论 3用户 4圈子
	 */
	private Integer type;
	/**
	 * 状态0待审核 1已处理 2已驳回
	 */
	private Integer status;
	/**
	 * 平台反馈
	 */
	private String feedback;
	/**
	 * 关联id
	 */
	private Integer linkId;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
