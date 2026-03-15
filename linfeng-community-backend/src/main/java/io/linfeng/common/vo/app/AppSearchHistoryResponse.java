package io.linfeng.common.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 移动端搜索历史响应
 */
@Data
@Schema(description = "移动端搜索历史响应体")
public class AppSearchHistoryResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "搜索记录ID")
    private Long searchId;

    @Schema(description = "搜索内容")
    private String content;

    @Schema(description = "最后更新时间")
    private Date updateTime;
}


