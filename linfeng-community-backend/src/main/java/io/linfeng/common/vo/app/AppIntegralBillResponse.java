package io.linfeng.common.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 移动端积分流水响应
 */
@Data
@Schema(description = "移动端积分流水响应体")
public class AppIntegralBillResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "账单ID")
    private Integer id;

    @Schema(description = "积分变更类型（0支出 1获得）")
    private Integer pm;

    @Schema(description = "账单标题")
    private String title;

    @Schema(description = "类别")
    private String category;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "积分变动值")
    private BigDecimal number;

    @Schema(description = "剩余积分")
    private BigDecimal balance;

    @Schema(description = "备注")
    private String mark;

    @Schema(description = "变动时间")
    private Date addTime;

    @Schema(description = "状态（0待确定 1有效 -1无效）")
    private Integer status;
}


