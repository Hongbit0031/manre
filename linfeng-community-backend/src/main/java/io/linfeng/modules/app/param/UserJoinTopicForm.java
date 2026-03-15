
package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户加入圈子请求体")
public class UserJoinTopicForm {

    @Schema(description = "topicClassId")
    private Integer classId;

    @Schema(description = "page")
    private Integer page;

    @Schema(description = "uid")
    private Integer uid;

}
