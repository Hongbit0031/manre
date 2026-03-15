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

import io.linfeng.common.annotation.SysLog;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.validator.group.AddGroup;
import io.linfeng.common.validator.group.UpdateGroup;
import io.linfeng.modules.admin.entity.SignConfigEntity;
import io.linfeng.modules.admin.param.SignConfigParam;
import io.linfeng.modules.admin.service.SignConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



/**
 * 
 *
 * @author linfeng
 * @email linfengtech001@163.com
 * @date 2022-05-07 20:28:46
 */
@RestController
@RequestMapping("admin/signconfig")
@Tag(name = "管理端——签到配置管理")
public class SignConfigController {
    @Autowired
    private SignConfigService signConfigService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("admin:signconfig:list")
    @Operation(summary = "签到配置列表分页")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = signConfigService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("admin:signconfig:info")
    @Operation(summary = "签到配置详情")
    public R info(@PathVariable("id") Integer id){
		SignConfigEntity signConfig = signConfigService.getById(id);

        return R.ok().put("signConfig", signConfig);
    }

    /**
     * 保存
     */
    @SysLog("保存签到配置")
    @PostMapping("/save")
    @RequiresPermissions("admin:signconfig:save")
    @Operation(summary = "签到配置保存")
    public R save(@RequestBody SignConfigParam param){
        ValidatorUtils.validateEntity(param, AddGroup.class);

        SignConfigEntity signConfig = new SignConfigEntity();
        BeanUtils.copyProperties(param, signConfig);
		signConfigService.save(signConfig);

        return R.ok();
    }

    /**
     * 修改
     */
    @SysLog("修改签到配置")
    @PostMapping("/update")
    @RequiresPermissions("admin:signconfig:update")
    @Operation(summary = "签到配置修改")
    public R update(@RequestBody SignConfigParam param){
        ValidatorUtils.validateEntity(param, UpdateGroup.class);

        SignConfigEntity signConfig = new SignConfigEntity();
        BeanUtils.copyProperties(param, signConfig);
		signConfigService.updateById(signConfig);

        return R.ok();
    }

    /**
     * 删除
     */
    @SysLog("删除签到配置")
    @PostMapping("/delete")
    @RequiresPermissions("admin:signconfig:delete")
    @Operation(summary = "签到配置删除")
    public R delete(@RequestBody Integer[] ids){
		signConfigService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
