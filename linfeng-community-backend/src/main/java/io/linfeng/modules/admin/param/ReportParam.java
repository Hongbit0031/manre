/**
 * -----------------------------------
 * Copyright (c) 2022-2026
 * All rights reserved, Designed By www.linfengtech.cn
 * 林风社交论坛商业版本请务必保留此注释头信息
 * 商业版授权联系技术客服	 QQ:  3582996245
 * 严禁分享、盗用、转卖源码或非法牟利！
 * 版权所有 ，侵权必究！
 * -----------------------------------
 */
package io.linfeng.modules.admin.param;

import io.linfeng.common.validator.group.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Schema(description = "用户举报请求体")
public class ReportParam implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Schema(description = "ID，更新时必填")
    @NotNull(message = "ID不能为空", groups = UpdateGroup.class)
    private Integer id;

    /**
     * 文件
     */
    @Schema(description = "文件")
    private String media;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String content;

    /**
     * 用户id
     */
    @Schema(description = "用户ID")
    private Integer uid;

    /**
     * 类型1帖子 2评论 3用户 4圈子
     */
    @Schema(description = "类型1帖子 2评论 3用户 4圈子")
    private Integer type;

    /**
     * 状态0待审核 1已处理 2已驳回
     */
    @Schema(description = "状态0待审核 1已处理 2已驳回")
    private Integer status;

    /**
     * 平台反馈
     */
    @Schema(description = "平台反馈")
    private String feedback;

    /**
     * 关联id
     */
    @Schema(description = "关联ID")
    private Integer linkId;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private Date updateTime;
}

