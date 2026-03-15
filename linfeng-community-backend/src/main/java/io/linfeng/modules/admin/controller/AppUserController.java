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

import java.util.Arrays;
import java.util.Map;

import io.linfeng.common.annotation.NoRepeatSubmit;
import io.linfeng.common.annotation.SysLog;
import io.linfeng.modules.admin.service.PostService;
import io.linfeng.modules.admin.param.AdminUserInfoParam;
import io.linfeng.modules.admin.param.AdminUserPunishParam;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.service.AppUserService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;



/**
 * 
 * 用户管理模块
 * @author linfeng
 */
@RestController
@RequestMapping("admin/user")
@Tag(name = "管理端——用户管理")
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private PostService postService;


    @GetMapping("/list")
    @RequiresPermissions("admin:user:list")
    @Operation(summary = "用户列表")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = appUserService.queryPage(params);

        return R.ok().put("page", page);
    }


    @GetMapping("/info/{uid}")
    @RequiresPermissions("admin:user:info")
    @Operation(summary = "用户详情")
    public R info(@PathVariable("uid") Integer uid){
		AppUserEntity user = appUserService.getById(uid);

        return R.ok().put("user", user);
    }


    @SysLog("修改会员信息")
    @NoRepeatSubmit
    @PostMapping("/update")
    @RequiresPermissions("admin:user:update")
    @Operation(summary = "修改会员信息")
    public R update(@RequestBody AdminUserInfoParam user){
		appUserService.updateUser(user);
        return R.ok();
    }


    @SysLog("处罚用户账号")
    @NoRepeatSubmit
    @PostMapping("/punish")
    @RequiresPermissions("admin:user:update")
    @Operation(summary = "处罚用户账号")
    public R punish(@RequestBody AdminUserPunishParam param){
        appUserService.punishUser(param);
        return R.ok();
    }


    @PostMapping("/delete")
    @RequiresPermissions("admin:user:delete")
    @Operation(summary = "用户删除")
    public R delete(@RequestBody Integer[] uids){
		appUserService.removeByIds(Arrays.asList(uids));

        return R.ok();
    }


    @SysLog("禁用会员")
    @PostMapping("/ban/{id}")
    @RequiresPermissions("admin:user:update")
    @Operation(summary = "禁用会员")
    public R ban(@PathVariable("id") Integer id){
		appUserService.ban(id);

        return R.ok();
    }


    @SysLog("解除会员禁用")
    @PostMapping("/openBan/{id}")
    @RequiresPermissions("admin:user:update")
    @Operation(summary = "解除会员禁用")
    public R openBan(@PathVariable("id") Integer id){
		appUserService.openBan(id);

        return R.ok();
    }


    @SysLog("虚拟人机账户创建")
    @PostMapping("/createVirtualUser")
    @RequiresPermissions("admin:user:update")
    @Operation(summary = "虚拟人机账户创建")
    public R createVirtualUser(){

        postService.virtualUser();
        return R.ok();
    }



}
