/**
 * -----------------------------------
 *  Copyright (c) 2022-2026
 *  All rights reserved, Designed By www.linfengtech.cn
 *  林风社交论坛商业版本请务必保留此注释头信息
 *  商业版授权联系技术客服	 QQ:  3582996245
 *  严禁分享、盗用、转卖源码或非法牟利！
 *  版权所有 ，侵权必究！
 * -----------------------------------
 */
package io.linfeng.modules.admin.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 提现
 * 
 * @author JL.Yu
 * @email linfengtech001@163.com
 * @date 2023-02-01 11:43:29
 */
@Data
@TableName("lf_cash_out")
public class CashOutEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Integer id;
	/**
	 * 用户ID
	 */
	private Integer uid;
	/**
	 * 申请提现金额
	 */
	private BigDecimal moneyNumber;
	/**
	 * 收款码
	 */
	private String url;
	/**
	 * 审核反馈
	 */
	private String feedback;
	/**
	 * 状态 0待审核 1已完成 2已驳回
	 */
	private Integer status;
	/**
	 * 打款类型 0支付宝1微信
	 */
	private Integer type;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 修改时间
	 */
	private Date updateTime;

}
