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

import io.linfeng.common.annotation.NoRepeatSubmit;
import io.linfeng.common.utils.DataConvertUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.utils.Result;
import io.linfeng.common.vo.app.SignResponse;
import io.linfeng.common.vo.app.SignUserResponse;
import io.linfeng.common.vo.app.AppIntegralBillResponse;
import io.linfeng.common.vo.app.AppSignConfigResponse;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.service.BillService;
import io.linfeng.modules.admin.service.UserSignService;
import io.linfeng.modules.app.annotation.Login;
import io.linfeng.modules.app.annotation.LoginUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 积分签到
 *
 * @author linfeng
 * @email linfengtech001@163.com
 */
@RestController
@RequestMapping("app/sign")
@Tag(name = "移动端——积分签到模块")
public class AppUserSignController {

    @Autowired
    private UserSignService userSignService;
    @Autowired
    private BillService billService;


    @Login
    @PostMapping("/userInfo")
    @Operation(summary = "获取签到用户信息")
    public Result<SignUserResponse> userInfo(@Parameter(hidden = true) @LoginUser AppUserEntity user){
        SignUserResponse vo=userSignService.getUserInfo(user);
        return new Result<SignUserResponse>().ok(vo);
    }


    @Login
    @PostMapping("/sign")
    @Operation(summary = "用户签到")
    @NoRepeatSubmit
    public Result<Integer> sign(@Parameter(hidden = true) @LoginUser AppUserEntity user){
        int num = userSignService.sign(user);
        return new Result<Integer>().ok(num);
    }


    @Login
    @GetMapping("/signList")
    @Operation(summary = "用户签到信息列表")
	@Parameters({
			@Parameter(name = "page", description = "分页页码", in = ParameterIn.QUERY, required = true,
					schema = @Schema(implementation = Integer.class)),
			@Parameter(name = "limit", description = "每页数量", in = ParameterIn.QUERY, required = true,
					schema = @Schema(implementation = Integer.class))
	})
    public Result<List<SignResponse>> signList(@Parameter(hidden = true) @LoginUser AppUserEntity user,
                      @RequestParam("page") Integer page,
                      @RequestParam("limit") Integer limit){
        List<SignResponse> list=userSignService.getSignList(user.getUid(),page,limit);
        return new Result<List<SignResponse>>().ok(list);
    }


    @GetMapping("/config")
    @Operation(summary = "签到配置")
    public Result<List<AppSignConfigResponse>> signConfig(){
        List<AppSignConfigResponse> list = DataConvertUtils.sourceToTarget(
                userSignService.getConfigList(), AppSignConfigResponse.class
        );
        return new Result<List<AppSignConfigResponse>>().ok(list);
    }


    @Login
    @GetMapping("/integralList")
    @Operation(summary = "用户签到积分列表")
	@Parameters({
			@Parameter(name = "page", description = "分页页码", in = ParameterIn.QUERY, required = true,
					schema = @Schema(implementation = Integer.class)),
			@Parameter(name = "limit", description = "每页数量", in = ParameterIn.QUERY, required = true,
					schema = @Schema(implementation = Integer.class)),
			@Parameter(name = "type", description = "类型", in = ParameterIn.QUERY, required = true,
					schema = @Schema(implementation = Integer.class))
	})
    public Result<List<AppIntegralBillResponse>> integralList(@Parameter(hidden = true) @LoginUser AppUserEntity user,
                          @RequestParam("page") Integer page,
                          @RequestParam("limit") Integer limit,
                          @RequestParam("type") Integer type){
        List<AppIntegralBillResponse> list = DataConvertUtils.sourceToTarget(
                billService.getIntegralList(user.getUid(),page,limit,type),
                AppIntegralBillResponse.class
        );
        return new Result<List<AppIntegralBillResponse>>().ok(list);
    }

}
