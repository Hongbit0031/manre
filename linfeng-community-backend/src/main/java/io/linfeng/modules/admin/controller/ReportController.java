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
package io.linfeng.modules.admin.controller;

import java.util.Arrays;
import java.util.Map;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.linfeng.modules.admin.entity.ReportEntity;
import io.linfeng.modules.admin.param.ReportParam;
import io.linfeng.modules.admin.service.ReportService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.validator.group.AddGroup;
import io.linfeng.common.validator.group.UpdateGroup;



/**
 * 用户举报
 *
 * @author linfeng
 * @email 3582996245@qq.com
 * @date 2022-09-01 12:55:12
 */
@RestController
@RequestMapping("admin/report")
@Tag(name = "管理端——用户举报管理")
public class ReportController {
    @Autowired
    private ReportService reportService;



    @GetMapping("/list")
    @RequiresPermissions("admin:report:list")
    @Operation(summary = "用户举报列表")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = reportService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("admin:report:info")
    @Operation(summary = "用户举报详情")
    public R info(@PathVariable("id") Integer id){
		ReportEntity report = reportService.getById(id);

        return R.ok().put("report", report);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("admin:report:save")
    @Operation(summary = "用户举报保存")
    public R save(@RequestBody ReportParam param){
        ValidatorUtils.validateEntity(param, AddGroup.class);

        ReportEntity report = new ReportEntity();
        BeanUtils.copyProperties(param, report);
		reportService.save(report);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("admin:report:update")
    @Operation(summary = "用户举报修改")
    public R update(@RequestBody ReportParam param){
        ValidatorUtils.validateEntity(param, UpdateGroup.class);

        ReportEntity report = new ReportEntity();
        BeanUtils.copyProperties(param, report);
//		reportService.updateById(report);
        reportService.dealByAdmin(report);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("admin:report:delete")
    @Operation(summary = "用户举报删除")
    public R delete(@RequestBody Integer[] ids){
		reportService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
