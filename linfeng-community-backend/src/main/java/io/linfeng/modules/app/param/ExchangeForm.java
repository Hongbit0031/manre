
package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "积分兑换余额请求体")
public class ExchangeForm {

    @Schema(description = "需兑换金额")
    private double rechargeValue;

}
