package io.linfeng.common.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 移动端会员充值选项响应
 */
@Data
@Schema(description = "移动端会员充值选项响应体")
public class AppVipOptionResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Integer id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "有效天数")
    private Integer validDays;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "描述")
    private String remark;

    @Schema(description = "排序")
    private Integer sort;
}


