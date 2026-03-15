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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.linfeng.modules.admin.entity.BillEntity;
import io.linfeng.modules.admin.param.BillParam;
import io.linfeng.modules.admin.service.BillService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.validator.group.AddGroup;
import io.linfeng.common.validator.group.UpdateGroup;
import org.springframework.beans.BeanUtils;



/**
 * 用户账单
 *
 * @author linfeng
 * @email linfengtech001@163.com
 * @date 2022-04-20 20:46:48
 */
@RestController
@RequestMapping("admin/bill")
@Tag(name = "管理端——用户账单")
public class BillController {
    @Autowired
    private BillService billService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("admin:bill:list")
    @Operation(summary = "用户账单列表分页")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = billService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("admin:bill:info")
    @Operation(summary = "用户账单详情")
    public R info(@PathVariable("id") Integer id){
		BillEntity bill = billService.getById(id);

        return R.ok().put("bill", bill);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("admin:bill:save")
    @Operation(summary = "用户账单保存")
    public R save(@RequestBody BillParam param){
		ValidatorUtils.validateEntity(param, AddGroup.class);
		
		BillEntity bill = new BillEntity();
		BeanUtils.copyProperties(param, bill);
		billService.save(bill);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("admin:bill:update")
    @Operation(summary = "用户账单修改")
    public R update(@RequestBody BillParam param){
		ValidatorUtils.validateEntity(param, UpdateGroup.class);
		
		BillEntity bill = new BillEntity();
		BeanUtils.copyProperties(param, bill);
		billService.updateById(bill);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("admin:bill:delete")
    @Operation(summary = "用户账单删除")
    public R delete(@RequestBody Integer[] ids){
		billService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
