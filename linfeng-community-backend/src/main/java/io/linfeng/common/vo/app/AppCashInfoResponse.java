package io.linfeng.common.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@Schema(description = "提现基本信息")
public class AppCashInfoResponse implements Serializable {
	private static final long serialVersionUID = 1L;



	@Schema(description = "账户余额")
	private BigDecimal nowMoney;

	@Schema(description = "能否申请提现")
	private boolean canSubmit;

	@Schema(description = "系统提现按钮是否开启")
	private boolean cashOpen;

}
