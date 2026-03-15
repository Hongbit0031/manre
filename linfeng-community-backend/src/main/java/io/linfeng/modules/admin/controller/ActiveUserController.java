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

import io.linfeng.common.utils.IPUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.linfeng.modules.admin.entity.ActiveUserEntity;
import io.linfeng.modules.admin.param.ActiveUserParam;
import io.linfeng.modules.admin.service.ActiveUserService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.validator.group.AddGroup;
import io.linfeng.common.validator.group.UpdateGroup;



/**
 * 用户访问统计
 *
 * @author JL.Yu
 * @email linfengtech001@163.com
 * @date 2024-05-29 12:25:03
 */
@RestController
@RequestMapping("admin/activeuser")
@Tag(name = "管理端——用户访问统计管理")
public class ActiveUserController {

    @Autowired
    private ActiveUserService activeUserService;


    @GetMapping("/list")
    @RequiresPermissions("admin:activeuser:list")
    @Operation(summary = "用户访问统计列表")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = activeUserService.queryPage(params);

        return R.ok().put("page", page);
    }

    @GetMapping("/getAddressByIp")
    @RequiresPermissions("admin:activeuser:list")
    @Operation(summary = "用户IP查询")
    public R getAddressByIp(@RequestParam Map<String, Object> params){
        String address = activeUserService.getAddressByIp(params);
        if(address.equals("")){
            return R.error();
        }
        return R.ok().put("result", address);
    }



    @GetMapping("/info/{id}")
    @RequiresPermissions("admin:activeuser:info")
    @Operation(summary = "用户访问统计详细信息")
    public R info(@PathVariable("id") Long id){
		ActiveUserEntity activeUser = activeUserService.getById(id);

        return R.ok().put("activeUser", activeUser);
    }


    @PostMapping("/save")
    @RequiresPermissions("admin:activeuser:save")
    @Operation(summary = "用户访问统计保存")
    public R save(@RequestBody ActiveUserParam param){
		ValidatorUtils.validateEntity(param, AddGroup.class);
		
		ActiveUserEntity activeUser = new ActiveUserEntity();
		BeanUtils.copyProperties(param, activeUser);
		activeUserService.save(activeUser);

        return R.ok();
    }


    @PostMapping("/update")
    @RequiresPermissions("admin:activeuser:update")
    @Operation(summary = "用户访问统计修改")
    public R update(@RequestBody ActiveUserParam param){
		ValidatorUtils.validateEntity(param, UpdateGroup.class);
		
		ActiveUserEntity activeUser = new ActiveUserEntity();
		BeanUtils.copyProperties(param, activeUser);
		activeUserService.updateById(activeUser);

        return R.ok();
    }


    @PostMapping("/delete")
    @RequiresPermissions("admin:activeuser:delete")
    @Operation(summary = "用户访问统计删除")
    public R delete(@RequestBody Long[] ids){
		activeUserService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
