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
@Schema(description = "轮播图请求体")
public class LinkParam implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@Schema(description = "id，更新时必填")
	@NotNull(message = "id不能为空", groups = UpdateGroup.class)
	private Integer id;

	/**
	 * 标题
	 */
	@Schema(description = "标题")
	private String title;

	/**
	 * 路径
	 */
	@Schema(description = "路径")
	private String url;

	/**
	 * 图片
	 */
	@Schema(description = "图片")
	private String img;

	/**
	 * 跳转类型
	 * 3 页面内   1外链
	 */
	@Schema(description = "跳转类型 3 页面内   1外链")
	private Integer type;

	/**
	 * 创建时间
	 */
	@Schema(description = "创建时间")
	private Date createTime;

	/**
	 * 分类
	 * 0 圈子页
	 * 1 个人页
	 */
	@Schema(description = "分类 0 圈子页 1 个人页")
	private Integer cateId;

}

