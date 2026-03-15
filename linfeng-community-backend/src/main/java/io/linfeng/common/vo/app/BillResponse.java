package io.linfeng.common.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author linfeng
 * @date 2022/5/2 20:09
 */
@Data
@Schema(description = "账单信息响应体")
public class BillResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "明细数字")
    private BigDecimal number;

    @Schema(description = "添加时间")
    private Date addTime;

    @Schema(description = "0 = 支出 1 = 获得")
    private Integer pm;

    @Schema(description = "账单标题")
    private String title;

    @Schema(description = "时间")
    private String time;
}
