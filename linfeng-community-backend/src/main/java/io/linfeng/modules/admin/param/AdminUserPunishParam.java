
package io.linfeng.modules.admin.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "处罚用户内容请求体")
public class AdminUserPunishParam {

    @Schema(description = "用户id")
    private Integer uid;

    @Schema(description = "帖子：0不处理1全部下架2全部删除")
    private Integer resetPost;

    @Schema(description = "头像：0不处理1重置处理")
    private Integer resetAvatar;

    @Schema(description = "签名：0不处理1重置处理")
    private Integer resetIntro;

    @Schema(description = "用户名：0不处理1重置处理")
    private Integer resetUsername;

    @Schema(description = "背景图：0不处理1重置处理")
    private Integer resetBg;


}
