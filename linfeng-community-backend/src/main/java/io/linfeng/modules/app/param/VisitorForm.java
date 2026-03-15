package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "浏览统计请求体")
public class VisitorForm {


    /**
     * 访问终端：
     * miniapp
     * H5
     * App
     */
    @Schema(description = "访问终端：miniapp H5 App")
    private String terminal;

}
