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
import io.linfeng.common.vo.app.AppReportListResponse;
import io.linfeng.common.utils.AppPageUtils;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.service.ReportService;
import io.linfeng.modules.app.annotation.Login;
import io.linfeng.modules.app.annotation.LoginUser;
import io.linfeng.modules.app.param.ReportAddForm;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 用户举报
 *
 * @author linfeng
 * @email 3582996245@qq.com
 * @date 2022-09-01 12:55:12
 */
@RestController
@RequestMapping("app/report")
@Tag(name = "移动端——举报模块")
public class AppReportController {

    @Autowired
    private ReportService reportService;


    @Login
    @PostMapping("/addReport")
    @Operation(summary = "用户提交举报")
    public Result save(@RequestBody ReportAddForm request, @Parameter(hidden = true) @LoginUser AppUserEntity user){
        ValidatorUtils.validateEntity(request);
		reportService.addReport(request,user);

        return new Result();
    }

    @Login
    @GetMapping("/list")
    @Operation(summary = "用户举报分页")
    public Result<AppPageUtils<AppReportListResponse>> list(@Parameter(description = "分页页码", required = true) @RequestParam("page") Integer page,
                                     @Parameter(description = "状态0待审核 1已处理 2已驳回", required = true) @RequestParam("status") Integer status,
                                     @Parameter(hidden = true) @LoginUser AppUserEntity user){

        AppPageUtils<AppReportListResponse> pages =reportService.listByUser(page,status,user);
        return new Result<AppPageUtils<AppReportListResponse>>().ok(pages);
    }


    @Login
    @GetMapping("/detail")
    @Operation(summary = "用户举报详情")
    public Result<AppReportListResponse> detail(@Parameter(description = "举报单号id", required = true) @RequestParam("id") Integer id){

        AppReportListResponse report = reportService.detail(id);
        return new Result<AppReportListResponse>().ok(report);
    }

}
