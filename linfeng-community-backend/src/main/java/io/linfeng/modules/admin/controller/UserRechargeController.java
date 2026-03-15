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

import io.linfeng.modules.admin.entity.UserRechargeEntity;
import io.linfeng.modules.admin.param.UserRechargeParam;
import io.linfeng.modules.admin.service.UserRechargeService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.validator.group.AddGroup;
import io.linfeng.common.validator.group.UpdateGroup;



/**
 * 用户充值
 *
 * @author linfeng
 * @email linfengtech001@163.com
 * @date 2022-04-19 19:27:33
 */
@RestController
@RequestMapping("admin/userrecharge")
@Tag(name = "管理端——用户充值管理")
public class UserRechargeController {

    @Autowired
    private UserRechargeService userRechargeService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("admin:userrecharge:list")
    @Operation(summary = "用户充值列表分页")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = userRechargeService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("admin:userrecharge:info")
    @Operation(summary = "用户充值详情")
    public R info(@PathVariable("id") Integer id){
		UserRechargeEntity userRecharge = userRechargeService.getById(id);

        return R.ok().put("userRecharge", userRecharge);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("admin:userrecharge:save")
    @Operation(summary = "用户充值保存")
    public R save(@RequestBody UserRechargeParam param){
        ValidatorUtils.validateEntity(param, AddGroup.class);
        UserRechargeEntity userRecharge = new UserRechargeEntity();
        BeanUtils.copyProperties(param, userRecharge);
		userRechargeService.save(userRecharge);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("admin:userrecharge:update")
    @Operation(summary = "用户充值修改")
    public R update(@RequestBody UserRechargeParam param){
        ValidatorUtils.validateEntity(param, UpdateGroup.class);
        UserRechargeEntity userRecharge = new UserRechargeEntity();
        BeanUtils.copyProperties(param, userRecharge);
		userRechargeService.updateById(userRecharge);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("admin:userrecharge:delete")
    @Operation(summary = "用户充值删除")
    public R delete(@RequestBody Integer[] ids){
		userRechargeService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
