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
package io.linfeng.common.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 系统消息响应VO
 *
 * @author linfeng
 */
@Data
@Schema(description = "系统消息响应")
public class AppSystemMessageResponse {

    @Schema(description = "消息id")
    private Integer mId;

    @Schema(description = "推送标题")
    private String title;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "0未读，1已读")
    private Integer status;

    @Schema(description = "创建时间")
    private Date createTime;
}

