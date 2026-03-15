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

import io.linfeng.modules.admin.entity.VipOptionEntity;
import io.linfeng.modules.admin.param.VipOptionParam;
import io.linfeng.modules.admin.service.VipOptionService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.validator.group.AddGroup;
import io.linfeng.common.validator.group.UpdateGroup;



/**
 * 会员充值选项
 *
 * @author JL.Yu
 * @email linfengtech001@163.com
 * @date 2022-09-28 14:26:17
 */
@RestController
@RequestMapping("admin/vipoption")
@Tag(name = "管理端——会员充值选项")
public class VipOptionController {
    @Autowired
    private VipOptionService vipOptionService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("admin:vipoption:list")
    @Operation(summary = "会员充值选项列表分页")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = vipOptionService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("admin:vipoption:info")
    @Operation(summary = "会员充值选项详情")
    public R info(@PathVariable("id") Integer id){
		VipOptionEntity vipOption = vipOptionService.getById(id);

        return R.ok().put("vipOption", vipOption);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("admin:vipoption:save")
    @Operation(summary = "会员充值选项保存")
    public R save(@RequestBody VipOptionParam param){
        ValidatorUtils.validateEntity(param, AddGroup.class);

        VipOptionEntity vipOption = new VipOptionEntity();
        BeanUtils.copyProperties(param, vipOption);
		vipOptionService.save(vipOption);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("admin:vipoption:update")
    @Operation(summary = "会员充值选项修改")
    public R update(@RequestBody VipOptionParam param){
        ValidatorUtils.validateEntity(param, UpdateGroup.class);

        VipOptionEntity vipOption = new VipOptionEntity();
        BeanUtils.copyProperties(param, vipOption);
		vipOptionService.updateById(vipOption);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("admin:vipoption:delete")
    @Operation(summary = "会员充值选项删除")
    public R delete(@RequestBody Integer[] ids){
		vipOptionService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
