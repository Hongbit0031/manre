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

import io.linfeng.common.exception.LinfengException;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import io.linfeng.modules.admin.entity.LuckdrawEntity;
import io.linfeng.modules.admin.param.LuckdrawParam;
import io.linfeng.modules.admin.service.LuckdrawService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.validator.group.AddGroup;
import io.linfeng.common.validator.group.UpdateGroup;



/**
 * 
 * 抽奖物品管理
 * @author linfeng
 * @email 3582996245@qq.com
 * @date 2022-08-14 14:28:48
 */
@RestController
@RequestMapping("admin/luckdraw")
@Tag(name = "管理端——抽奖物品")
public class LuckdrawController {
    @Autowired
    private LuckdrawService luckdrawService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("admin:luckdraw:list")
    @Operation(summary = "抽奖物品列表")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = luckdrawService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("admin:luckdraw:info")
    @Operation(summary = "抽奖物品详情")
    public R info(@PathVariable("id") Integer id){
		LuckdrawEntity luckdraw = luckdrawService.getById(id);

        return R.ok().put("luckdraw", luckdraw);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("admin:luckdraw:save")
    @Operation(summary = "抽奖物品保存")
    public R save(@RequestBody LuckdrawParam param){
        ValidatorUtils.validateEntity(param, AddGroup.class);
        LuckdrawEntity luckdraw = new LuckdrawEntity();
        BeanUtils.copyProperties(param, luckdraw);
        Long count = luckdrawService.lambdaQuery().eq(LuckdrawEntity::getStatus, 1).count();
        if(count>=8){
            throw new LinfengException("抽奖物品数量必须为8个");
        }
        luckdrawService.save(luckdraw);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("admin:luckdraw:update")
    @Operation(summary = "抽奖物品修改")
    public R update(@RequestBody LuckdrawParam param){
		ValidatorUtils.validateEntity(param, UpdateGroup.class);
		LuckdrawEntity luckdraw = new LuckdrawEntity();
		BeanUtils.copyProperties(param, luckdraw);
		luckdrawService.updateById(luckdraw);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("admin:luckdraw:delete")
    @Operation(summary = "抽奖物品删除")
    public R delete(@RequestBody Integer[] ids){
		luckdrawService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
