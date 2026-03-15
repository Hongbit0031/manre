package io.linfeng.modules.admin.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.linfeng.common.validator.group.UpdateGroup;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户充值请求体
 */
@Data
@Schema(description = "用户充值请求体")
public class UserRechargeParam implements Serializable {

    @Schema(description = "主键，更新时必填")
    @NotNull(message = "ID不能为空", groups = UpdateGroup.class)
    private Integer id;

    @Schema(description = "充值用户UID")
    private Integer uid;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "订单号")
    private String orderId;

    @Schema(description = "充值金额")
    private BigDecimal price;

    /**
     * 钱包充值中表示购买赠送金额
     * 会员充值中表示选择的会员套餐ID，即vipOptionId
     */
    @Schema(description = "赠送金额/会员套餐ID")
    private BigDecimal givePrice;

    @Schema(description = "充值类型 app/weixin/h5")
    private String rechargeType;

    @Schema(description = "是否已支付")
    private Integer paid;

    @Schema(description = "支付时间")
    private Date payTime;

    @Schema(description = "创建时间")
    private Date addTime;

    @Schema(description = "退款金额")
    private BigDecimal refundPrice;

    @Schema(description = "支付订单号")
    private String transactionId;

    @Schema(description = "订单支付编号")
    private String outTradeNo;

    @Schema(description = "类型 0 钱包充值 1 会员充值")
    private Integer type;
}

