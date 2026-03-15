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
package io.linfeng.modules.app.controller;

import io.linfeng.common.annotation.NoRepeatSubmit;
import io.linfeng.common.utils.Result;
import io.linfeng.common.vo.app.AppCashInfoResponse;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.service.CashOutService;
import io.linfeng.modules.app.annotation.Login;
import io.linfeng.modules.app.annotation.LoginUser;
import io.linfeng.modules.app.param.AddCashOutForm;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 提现模块
 *
 * @author JL.Yu
 * @date 2023-02-01 12:42:16
 */
@RestController
@RequestMapping("app/cashOut")
@Tag(name = "移动端——提现模块")
public class AppCashOutController {

    @Autowired
    private CashOutService cashOutService;


    @Login
    @PostMapping("/submit")
    @Operation(summary = "用户提交提现申请")
    @NoRepeatSubmit
    public Result save(@RequestBody AddCashOutForm param,@Parameter(hidden = true) @LoginUser AppUserEntity user){

        cashOutService.submit(param,user.getUid());
        return new Result();
    }


    @Login
    @GetMapping("/getAccountBasicInfo")
    @Operation(summary = "查询账户基本信息")
    public Result<AppCashInfoResponse> getAccountBasicInfo(@Parameter(hidden = true) @LoginUser AppUserEntity user){
        AppCashInfoResponse vo =cashOutService.getAccountBasicInfo(user.getUid());
        return new Result<AppCashInfoResponse>().ok(vo);
    }

}
