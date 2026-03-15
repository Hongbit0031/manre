package io.linfeng.modules.admin.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 会员权益
 * 
 * @author Pity
 * @email linfengtech002@163.com
 * @date 2023-12-11 22:07:11
 */
@Data
@TableName("lf_vip_benefit")
public class VipBenefitEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Integer id;
	/**
	 * 会员权益标题
	 */
	private String title;
	/**
	 * 会员权益描述
	 */
	private String describes;
	/**
	 * 图标
	 */
	private String icon;
	/**
	 * 状态0有效1无效
	 */
	private Integer status;
	/**
	 * 排序
	 */
	private Integer sort;

}
