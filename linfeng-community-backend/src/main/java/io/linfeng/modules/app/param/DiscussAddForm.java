
package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

@Data
@Schema(description = "话题添加请求")
public class DiscussAddForm {

    @Schema(description = "描述介绍")
    @NotBlank
    @Length(max = 50, message = "描述不能超过50个字符")
    private String introduce;

    @Schema(description = "标题")
    @Length(max = 15, message = "标题不能超过15个字符")
    private String title;

    @Schema(description = "圈子id")
    private Integer topicId;

}
