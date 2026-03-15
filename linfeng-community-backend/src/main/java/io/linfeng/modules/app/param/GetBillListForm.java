
package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户账单请求体")
public class GetBillListForm {

    @Schema(description = "分页")
    private Integer page;

    @Schema(description = "分页数")
    private Integer limit;

    @Schema(description = "分类")
    private Integer type;

}
