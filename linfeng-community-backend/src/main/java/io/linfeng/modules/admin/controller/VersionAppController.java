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
import java.util.Date;
import java.util.Map;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.linfeng.modules.admin.entity.AppVersionEntity;
import io.linfeng.modules.admin.param.AppVersionParam;
import io.linfeng.modules.admin.service.AppVersionService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.validator.group.AddGroup;
import io.linfeng.common.validator.group.UpdateGroup;



/**
 * APP版本管理模块
 *
 * @author JL.Yu
 * @email linfengtech001@163.com
 * @date 2025-03-14 12:55:42
 */
@RestController
@RequestMapping("admin/appversion")
@Tag(name = "管理端——APP版本管理模块")
public class VersionAppController {
    @Autowired
    private AppVersionService appVersionService;


    @GetMapping("/list")
    @RequiresPermissions("admin:appversion:list")
    @Operation(summary = "APP版本管理列表分页")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = appVersionService.queryPage(params);

        return R.ok().put("page", page);
    }


    @GetMapping("/info/{id}")
    @RequiresPermissions("admin:appversion:info")
    @Operation(summary = "APP版本管理详情")
    public R info(@PathVariable("id") Integer id){
		AppVersionEntity appVersion = appVersionService.getById(id);

        return R.ok().put("appVersion", appVersion);
    }


    @PostMapping("/save")
    @RequiresPermissions("admin:appversion:save")
    @Operation(summary = "APP版本管理保存")
    public R save(@RequestBody AppVersionParam param){
        ValidatorUtils.validateEntity(param, AddGroup.class);

        AppVersionEntity appVersion = new AppVersionEntity();
        BeanUtils.copyProperties(param, appVersion);
        appVersion.setCreateTime(new Date());
        appVersion.setUpdateTime(new Date());
		appVersionService.save(appVersion);

        return R.ok();
    }


    @PostMapping("/update")
    @RequiresPermissions("admin:appversion:update")
    @Operation(summary = "APP版本管理更新")
    public R update(@RequestBody AppVersionParam param){
        ValidatorUtils.validateEntity(param, UpdateGroup.class);

        AppVersionEntity appVersion = new AppVersionEntity();
        BeanUtils.copyProperties(param, appVersion);
        appVersion.setUpdateTime(new Date());
		appVersionService.updateById(appVersion);

        return R.ok();
    }


    @PostMapping("/delete")
    @RequiresPermissions("admin:appversion:delete")
    @Operation(summary = "APP版本管理删除")
    public R delete(@RequestBody Integer[] ids){
		appVersionService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
