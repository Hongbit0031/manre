package io.linfeng.common.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author linfeng
 * @date 2025/3/14 13:10
 */
@Data
@Schema(description = "APP版本管理响应体")
public class AppVersionResponse {

    @Schema(description = "是否需要更新")
    private Boolean update;

    @Schema(description = "是否强制更新")
    private Integer isForce;

    @Schema(description = "最新版本号")
    private String version;

    @Schema(description = "更新内容")
    private String content;

    @Schema(description = "下载地址")
    private String url;

    @Schema(description = "安卓市场地址")
    private String androidUrl;

    @Schema(description = "iOS市场地址")
    private String iosUrl;

}
