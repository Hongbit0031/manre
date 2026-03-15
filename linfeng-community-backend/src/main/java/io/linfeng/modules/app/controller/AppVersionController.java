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
package io.linfeng.modules.app.controller;

import io.linfeng.common.utils.Result;
import io.linfeng.common.vo.app.AppVersionResponse;
import io.linfeng.modules.admin.service.AppVersionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("app/appversion")
@Tag(name = "移动端——APP版本管理模块")
public class AppVersionController {

    @Autowired
    private AppVersionService appVersionService;


    @Operation(summary = "APP版本检查")
    @GetMapping("/version")
    public Result<AppVersionResponse> checkVersion(@RequestParam("version") String version) {
        AppVersionResponse response = appVersionService.checkUpdate(version);
        return new Result<AppVersionResponse>().ok(response);
    }

}
