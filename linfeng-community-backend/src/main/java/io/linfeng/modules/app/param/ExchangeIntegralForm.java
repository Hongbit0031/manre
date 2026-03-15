
package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "余额兑换积分请求体")
public class ExchangeIntegralForm {

    @Schema(description = "需兑换金额")
    private Integer rechargeValue;

}
