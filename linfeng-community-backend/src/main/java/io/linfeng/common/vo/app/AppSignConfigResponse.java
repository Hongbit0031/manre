package io.linfeng.common.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 移动端签到配置响应
 */
@Data
@Schema(description = "移动端签到配置响应体")
public class AppSignConfigResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "配置ID")
    private Integer id;

    @Schema(description = "连续签到天数")
    private String day;

    @Schema(description = "签到可得积分")
    private String signNum;

    @Schema(description = "显示排序")
    private Integer sort;
}


