package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "预充值dto")
public class VipRechargeForm {

    @Schema(description = "会员充值选项id")
    private Integer vipId;

    @Schema(description = "充值类型app weixin h5 yue")
    private String payType;

}
