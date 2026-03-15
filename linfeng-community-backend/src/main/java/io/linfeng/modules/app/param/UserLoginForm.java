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
package io.linfeng.modules.app.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 第三方自动登录请求体
 * @author Jl.Yu
 * @date 2024-10-25 14:28:48
 */
@Data
@Schema(description = "第三方自动登录请求参数")
public class UserLoginForm {

    @Schema(description = "请求密钥(必填)")
    private String secret;

    @Schema(description = "第三方应用手机号(必填)")
    private String thirdPhone;

    @Schema(description = "第三方应用用户名(可选)")
    private String thirdName;

    @Schema(description = "第三方应用头像(可选)")
    private String avatar;

    @Schema(description = "第三方应用简介(可选)")
    private String remark;

    @Schema(description = "第三方应用性别(0未知，1男，2女)(可选)")
    private Integer gender;
}
