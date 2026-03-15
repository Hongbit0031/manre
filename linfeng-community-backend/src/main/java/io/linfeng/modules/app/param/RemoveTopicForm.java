package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户移除圈子请求体")
public class RemoveTopicForm {


    @Schema(description = "用户id")
    private Integer id;

    @Schema(description = "圈子Id")
    private Integer topicId;

}
