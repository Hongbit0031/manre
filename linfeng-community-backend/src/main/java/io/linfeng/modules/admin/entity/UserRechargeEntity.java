package io.linfeng.modules.admin.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户充值实体类
 * 
 * @author linfeng
 * @email linfengtech001@163.com
 * @date 2022-04-19 19:27:33
 */
@Data
@TableName("lf_user_recharge")
public class UserRechargeEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 充值用户UID
	 */
	private Integer uid;
	/**
	 * 
	 */
	private String nickname;
	/**
	 * 订单号
	 */
	private String orderId;
	/**
	 * 充值金额
	 */
	private BigDecimal price;
	/**
	 * 钱包充值中表示购买赠送金额
	 * 会员充值中表示选择的会员套餐ID，即vipOptionId
	 */
	private BigDecimal givePrice;
	/**
	 * 充值类型 app weixin h5
	 */
	private String rechargeType;
	/**
	 * 是否充值
	 */
	private Integer paid;
	/**
	 * 充值支付时间
	 */
	private Date payTime;
	/**
	 * 充值时间
	 */
	private Date addTime;
	/**
	 * 退款金额
	 */
	private BigDecimal refundPrice;

	/**
	 * 支付生成的订单号
	 */
	private String transactionId;

	/**
	 * 订单支付编号
	 */
	private String outTradeNo;

	/**
	 * 类型 0 钱包充值  1 会员充值
	 */
	private Integer type;
}
