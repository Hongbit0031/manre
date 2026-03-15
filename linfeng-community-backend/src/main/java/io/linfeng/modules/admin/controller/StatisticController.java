/**
 * -----------------------------------
 *  Copyright (c) 2022-2026
 *  All rights reserved, Designed By www.linfengtech.cn
 *  林风社交论坛商业版本请务必保留此注释头信息
 *  商业版授权联系技术客服	 QQ:  3582996245
 *  严禁分享、盗用、转卖源码或非法牟利！
 *  版权所有 ，侵权必究！
 * -----------------------------------
 */
package io.linfeng.modules.admin.controller;

import io.linfeng.common.utils.R;
import io.linfeng.modules.admin.service.ActiveUserService;
import io.linfeng.modules.admin.service.AppUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台前端首页数据统计
 * @author linfeng
 * @date 2022/4/17 16:49
 */
@RestController
@RequestMapping("admin/statistics")
@Tag(name = "管理端——首页数据统计")
public class StatisticController {

    @Autowired
    private AppUserService userService;

    @Autowired
    private ActiveUserService activeUserService;


    @GetMapping("/home")
    @Operation(summary = "数据")
    public R index(){
        return R.ok().put("result",userService.indexDate());
    }


    @GetMapping("/visitor")
    @Operation(summary = "访问统计数据")
    public R visitor(){
        return R.ok().put("result",activeUserService.visitData());
    }


    @GetMapping("/chart")
    @Operation(summary = "统计用户")
    public R chart(){
        return R.ok().put("result",userService.chartCount());
    }


    @GetMapping("/chartPost")
    @Operation(summary = "统计发帖数")
    public R chartPost(){
        return R.ok().put("result",userService.chartPost());
    }


    @GetMapping("/chartMoney")
    @Operation(summary = "统计交易额")
    public R chartMoney(){
        return R.ok().put("result",userService.chartMoney());
    }

}
