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


@Data
@Schema(description = "分类请求体")
public class CategoryParam implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 分类id
	 */
	@Schema(description = "分类id，更新时必填")
	@NotNull(message = "分类id不能为空", groups = UpdateGroup.class)
	private Integer cateId;

	/**
	 * 分类名称
	 */
	@Schema(description = "分类名称")
	private String cateName;

	/**
	 * 是否推荐(1推荐)
	 */
	@Schema(description = "是否推荐(1推荐)")
	private Integer isTop;

	/**
	 * 图片
	 */
	@Schema(description = "图片")
	private String coverImage;

}

