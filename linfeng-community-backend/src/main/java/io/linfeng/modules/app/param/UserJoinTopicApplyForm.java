package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户加入圈子申请请求体")
public class UserJoinTopicApplyForm {

    @Schema(description = "圈子id")
    private Integer topicId;

    @Schema(description = "问题")
    private String question;

    @Schema(description = "回答")
    private String answer;

}
