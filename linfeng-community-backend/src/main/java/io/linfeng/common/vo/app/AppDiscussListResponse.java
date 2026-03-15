package io.linfeng.common.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author linfeng
 * @date 2025/5/25 15:23
 */
@Data
@Schema(description = "话题列表响应体")
public class AppDiscussListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Integer id;

    @Schema(description = "用户ID")
    private Integer uid;

    @Schema(description = "圈子id")
    private Integer topicId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "描述")
    private String introduce;

    @Schema(description = "浏览量")
    private Integer readCount;

    @Schema(description = "发起人信息")
    private AppUserShortInfoResponse userInfo;

    @Schema(description = "作品数量")
    private Integer postNumber;
}
