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

import io.linfeng.common.utils.AppPageUtils;
import io.linfeng.common.utils.Result;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.entity.LuckdrawRecordEntity;
import io.linfeng.modules.admin.service.LuckdrawService;
import io.linfeng.modules.app.annotation.Login;
import io.linfeng.modules.app.annotation.LoginUser;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * 抽奖模块
 * @author linfeng
 */
@RestController
@RequestMapping("app/luckDraw")
@Tag(name = "移动端——抽奖模块")
public class AppLuckDrawController {

    @Autowired
    private LuckdrawService luckdrawService;


    @Login
    @GetMapping("/getPrize")
    @Operation(summary = "获取奖品信息")
    public Result<Map<String,Object>> getPrize(@Parameter(hidden = true) @LoginUser AppUserEntity user){
        Map<String,Object> map=luckdrawService.getPrize(user);
        return new Result<Map<String,Object>>().ok(map);
    }


    @Login
    @GetMapping("/start")
    @Operation(summary = "用户抽奖")
    public Result<Map<String,Object>> start(@Parameter(hidden = true) @LoginUser AppUserEntity user){

        Map<String,Object> map=luckdrawService.startLuckDraw(user);
        return new Result<Map<String,Object>>().ok(map);
    }


    @Login
    @GetMapping("/record/{page}")
    @Operation(summary = "抽奖记录")
    public Result<AppPageUtils<LuckdrawRecordEntity>> record(@Parameter(hidden = true) @LoginUser AppUserEntity user,@PathVariable("page") Integer page){

        AppPageUtils<LuckdrawRecordEntity> pages=luckdrawService.record(user,page);
        return new Result<AppPageUtils<LuckdrawRecordEntity>>().ok(pages);
    }

}
