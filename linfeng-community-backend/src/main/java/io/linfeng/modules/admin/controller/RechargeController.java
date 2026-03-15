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

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.linfeng.modules.admin.entity.RechargeEntity;
import io.linfeng.modules.admin.param.RechargeParam;
import io.linfeng.modules.admin.service.RechargeService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.validator.group.AddGroup;
import io.linfeng.common.validator.group.UpdateGroup;



/**
 * 
 * 充值方案管理
 * @author linfeng
 * @email linfengtech001@163.com
 * @date 2022-04-21 17:05:58
 */
@RestController
@RequestMapping("admin/recharge")
@Tag(name = "管理端——充值方案管理")
public class RechargeController {
    @Autowired
    private RechargeService rechargeService;


    @GetMapping("/list")
    @RequiresPermissions("admin:recharge:list")
    @Operation(summary = "充值方案列表")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = rechargeService.queryPage(params);

        return R.ok().put("page", page);
    }



    @GetMapping("/info/{id}")
    @RequiresPermissions("admin:recharge:info")
    @Operation(summary = "充值方案详情")
    public R info(@PathVariable("id") Integer id){
		RechargeEntity recharge = rechargeService.getById(id);

        return R.ok().put("recharge", recharge);
    }


    @PostMapping("/save")
    @RequiresPermissions("admin:recharge:save")
    @Operation(summary = "充值方案保存")
    public R save(@RequestBody RechargeParam param){
        ValidatorUtils.validateEntity(param, AddGroup.class);

        RechargeEntity recharge = new RechargeEntity();
        BeanUtils.copyProperties(param, recharge);
		rechargeService.save(recharge);

        return R.ok();
    }


    @PostMapping("/update")
    @RequiresPermissions("admin:recharge:update")
    @Operation(summary = "充值方案修改")
    public R update(@RequestBody RechargeParam param){
        ValidatorUtils.validateEntity(param, UpdateGroup.class);

        RechargeEntity recharge = new RechargeEntity();
        BeanUtils.copyProperties(param, recharge);
		rechargeService.updateById(recharge);

        return R.ok();
    }


    @PostMapping("/delete")
    @RequiresPermissions("admin:recharge:delete")
    @Operation(summary = "充值方案删除")
    public R delete(@RequestBody Integer[] ids){
		rechargeService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
