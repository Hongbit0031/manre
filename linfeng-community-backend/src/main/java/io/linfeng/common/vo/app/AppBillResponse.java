package io.linfeng.common.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户账单响应体
 */
@Data
@Schema(description = "用户账单响应体")
public class AppBillResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "账单ID")
    private Integer id;

    @Schema(description = "积分/余额变动类型（0支出 1获得）")
    private Integer pm;

    @Schema(description = "账单标题")
    private String title;

    @Schema(description = "类别")
    private String category;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "变动数值")
    private BigDecimal number;

    @Schema(description = "剩余余额")
    private BigDecimal balance;

    @Schema(description = "备注")
    private String mark;

    @Schema(description = "变动时间")
    private Date addTime;

    @Schema(description = "状态（0待确定 1有效 -1无效）")
    private Integer status;
}

