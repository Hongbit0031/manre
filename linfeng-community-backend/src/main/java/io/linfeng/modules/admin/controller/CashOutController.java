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
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.linfeng.modules.admin.entity.CashOutEntity;
import io.linfeng.modules.admin.param.CashOutParam;
import io.linfeng.modules.admin.service.CashOutService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.validator.group.AddGroup;
import io.linfeng.common.validator.group.UpdateGroup;
import org.springframework.beans.BeanUtils;



/**
 * 提现
 *
 * @author JL.Yu
 * @email linfengtech001@163.com
 * @date 2023-02-01 11:43:29
 */
@RestController
@RequestMapping("admin/cashout")
@Tag(name = "管理端——提现")
public class CashOutController {
    @Autowired
    private CashOutService cashOutService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("admin:cashout:list")
    @Operation(summary = "提现列表")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = cashOutService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("admin:cashout:info")
    @Operation(summary = "提现详情")
    public R info(@PathVariable("id") Integer id){
		CashOutEntity cashOut = cashOutService.getById(id);

        return R.ok().put("cashOut", cashOut);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("admin:cashout:save")
    @Operation(summary = "提现保存")
    public R save(@RequestBody CashOutParam param){
		ValidatorUtils.validateEntity(param, AddGroup.class);
		
		CashOutEntity cashOut = new CashOutEntity();
		BeanUtils.copyProperties(param, cashOut);
		cashOutService.save(cashOut);

        return R.ok();
    }

    /**
     * 修改
     */
    @NoRepeatSubmit
    @PostMapping("/update")
    @RequiresPermissions("admin:cashout:update")
    @Operation(summary = "提现修改")
    public R update(@RequestBody CashOutParam param){
		ValidatorUtils.validateEntity(param, UpdateGroup.class);
		
		CashOutEntity cashOut = new CashOutEntity();
		BeanUtils.copyProperties(param, cashOut);
		cashOutService.updateCash(cashOut);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("admin:cashout:delete")
    @Operation(summary = "提现删除")
    public R delete(@RequestBody Integer[] ids){
		cashOutService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
