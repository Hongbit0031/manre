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
@Schema(description = "话题请求体")
public class DiscussParam implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 话题标签id
	 */
	@Schema(description = "话题标签id，更新时必填")
	@NotNull(message = "id不能为空", groups = UpdateGroup.class)
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
	 * 标题
	 */
	@Schema(description = "标题")
	private String title;

	/**
	 * 描述
	 */
	@Schema(description = "描述")
	private String introduce;

	/**
	 * 浏览量
	 */
	@Schema(description = "浏览量")
	private Integer readCount;

	/**
	 * 推荐位置：0 不推荐  1 首页推荐
	 */
	@Schema(description = "推荐位置：0 不推荐  1 首页推荐")
	private Integer topType;

	/**
	 * 创建时间
	 */
	@Schema(description = "创建时间")
	private Date createTime;

}

