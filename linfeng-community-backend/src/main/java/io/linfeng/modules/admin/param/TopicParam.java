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
@Schema(description = "圈子请求体")
public class TopicParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键，更新时必填")
    @NotNull(message = "ID不能为空", groups = UpdateGroup.class)
    private Integer id;

    @Schema(description = "创建用户id")
    private Integer uid;

    @Schema(description = "分类id")
    private Integer cateId;

    @Schema(description = "圈子名称")
    private String topicName;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "logo")
    private String coverImage;

    @Schema(description = "背景图")
    private String bgImage;

    @Schema(description = "推荐类型：0 不推荐， 1首页推荐， 2圈子页推荐")
    private Integer topType;

    @Schema(description = "圈子状态：0 正常 ，1禁用  2待审核")
    private Integer status;

    @Schema(description = "是否首页推荐圈子内容")
    private Integer indexRecommend;

    @Schema(description = "加入人数")
    private Integer userNum;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "进圈条件0无限制1答题并审核")
    private Integer rest;

    @Schema(description = "问题内容设置")
    private String question;

    @Schema(description = "是否私密：0公开 1私密")
    private Integer isPrivacy;
}

