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
@Schema(description = "APP版本管理请求体")
public class AppVersionParam implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID，更新时必填")
    @NotNull(message = "ID不能为空", groups = UpdateGroup.class)
    private Integer id;

    /**
     * 版本号
     */
    @Schema(description = "版本号(如1.0.1)")
    private String version;

    /**
     * 最低要求版本号
     */
    @Schema(description = "最低要求版本号，用于强制更新判断")
    private String minVersion;

    /**
     * 是否强制更新
     */
    @Schema(description = "是否强制更新(1:强制,0:非强制)")
    private Integer isForce;

    /**
     * 更新内容
     */
    @Schema(description = "更新内容描述")
    private String content;

    /**
     * 下载地址
     */
    @Schema(description = "通用下载地址")
    private String downloadUrl;

    /**
     * Android应用市场地址
     */
    @Schema(description = "Android应用市场地址")
    private String androidUrl;

    /**
     * iOS应用市场地址
     */
    @Schema(description = "iOS应用市场地址")
    private String iosUrl;

    /**
     * 版本状态
     */
    @Schema(description = "版本状态(1:启用,0:禁用)")
    private Integer status;

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


