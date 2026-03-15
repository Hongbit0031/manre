package io.linfeng.common.vo.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 话题列表响应体vo
 * @author Jl.Yu
 * @date 2024/12/30 15:46
 */
@Data
@Schema(description = "话题列表响应体")
public class AdminDiscussListResponse  implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Integer id;
    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private Integer uid;
    /**
     * 圈子id
     */
    @Schema(description = "圈子id")
    private Integer topicId;
    /**
     * 标题
     */
    @Schema(description = "标题")
    private String title;
    /**
     * 描述
     */
    @Schema(description = "描述")
    private String introduce;
    /**
     * 浏览量
     */
    @Schema(description = "浏览量")
    private Integer readCount;
    /**
     * 推荐位置：0 不推荐  1 首页推荐
     */
    @Schema(description = "推荐位置：0 不推荐  1 首页推荐")
    private Integer topType;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;


    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "圈子名称")
    private String topicName;

    @Schema(description = "圈子logo")
    private String coverImage;
}
