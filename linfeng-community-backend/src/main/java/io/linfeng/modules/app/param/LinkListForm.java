
package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "轮播图列表请求体")
public class LinkListForm {

    @Schema(description = "type")
    private Integer type;

    @Schema(description = "page")
    private Integer page;

    @Schema(description = "cateId")
    private Integer cateId;

}
