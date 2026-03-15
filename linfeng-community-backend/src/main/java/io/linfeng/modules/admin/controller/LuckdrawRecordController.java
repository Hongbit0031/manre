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

import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.validator.group.AddGroup;
import io.linfeng.common.validator.group.UpdateGroup;
import io.linfeng.modules.admin.param.LuckdrawRecordParam;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.linfeng.modules.admin.entity.LuckdrawRecordEntity;
import io.linfeng.modules.admin.service.LuckdrawRecordService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;



/**
 * 
 * 抽奖记录管理
 * @author linfeng
 * @email 3582996245@qq.com
 * @date 2022-08-14 14:28:48
 */
@RestController
@RequestMapping("admin/luckdrawrecord")
@Tag(name = "管理端——抽奖记录")
public class LuckdrawRecordController {
    @Autowired
    private LuckdrawRecordService luckdrawRecordService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("admin:luckdrawrecord:list")
    @Operation(summary = "抽奖记录列表")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = luckdrawRecordService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("admin:luckdrawrecord:info")
    @Operation(summary = "抽奖记录详情")
    public R info(@PathVariable("id") Integer id){
		LuckdrawRecordEntity luckdrawRecord = luckdrawRecordService.getById(id);

        return R.ok().put("luckdrawRecord", luckdrawRecord);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("admin:luckdrawrecord:save")
    @Operation(summary = "抽奖记录保存")
    public R save(@RequestBody LuckdrawRecordParam param){
        ValidatorUtils.validateEntity(param, AddGroup.class);
        LuckdrawRecordEntity luckdrawRecord = new LuckdrawRecordEntity();
        BeanUtils.copyProperties(param, luckdrawRecord);
		luckdrawRecordService.save(luckdrawRecord);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("admin:luckdrawrecord:update")
    @Operation(summary = "抽奖记录修改")
    public R update(@RequestBody LuckdrawRecordParam param){
        ValidatorUtils.validateEntity(param, UpdateGroup.class);
        LuckdrawRecordEntity luckdrawRecord = new LuckdrawRecordEntity();
        BeanUtils.copyProperties(param, luckdrawRecord);
		luckdrawRecordService.updateById(luckdrawRecord);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("admin:luckdrawrecord:delete")
    @Operation(summary = "抽奖记录删除")
    public R delete(@RequestBody Integer[] ids){
		luckdrawRecordService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
