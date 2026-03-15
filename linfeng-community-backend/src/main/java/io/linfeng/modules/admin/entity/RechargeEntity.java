package io.linfeng.modules.admin.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author linfeng
 * @email linfengtech001@163.com
 * @date 2022-04-21 17:05:58
 */
@Data
@TableName("lf_recharge")
public class RechargeEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Integer id;
	/**
	 * 充值金额
	 */
	private BigDecimal price;
	/**
	 * 赠送金额
	 */
	private BigDecimal givePrice;
	/**
	 * 排序
	 */
	private Integer sort;
	/**
	 * 状态
	 */
	private Integer status;

}
