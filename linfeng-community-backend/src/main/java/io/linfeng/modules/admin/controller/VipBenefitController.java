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

import io.linfeng.common.utils.ConfigConstant;
import io.linfeng.common.utils.RedisUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.linfeng.modules.admin.entity.VipBenefitEntity;
import io.linfeng.modules.admin.param.VipBenefitParam;
import io.linfeng.modules.admin.service.VipBenefitService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.validator.group.AddGroup;
import io.linfeng.common.validator.group.UpdateGroup;



/**
 * 会员权益
 *
 * @author Pity
 * @email linfengtech002@163.com
 * @date 2023-12-11 22:07:11
 */
@RestController
@RequestMapping("admin/vipbenefit")
@Tag(name = "管理端——会员权益")
public class VipBenefitController {

    @Autowired
    private VipBenefitService vipBenefitService;

    @Autowired
    private RedisUtils redisUtils;


    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("admin:vipbenefit:list")
    @Operation(summary = "会员权益列表分页")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = vipBenefitService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("admin:vipbenefit:info")
    @Operation(summary = "会员权益详情")
    public R info(@PathVariable("id") Integer id){
		VipBenefitEntity vipBenefit = vipBenefitService.getById(id);

        return R.ok().put("vipBenefit", vipBenefit);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("admin:vipbenefit:save")
    @Operation(summary = "会员权益保存")
    public R save(@RequestBody VipBenefitParam param){
        ValidatorUtils.validateEntity(param, AddGroup.class);

        VipBenefitEntity vipBenefit = new VipBenefitEntity();
        BeanUtils.copyProperties(param, vipBenefit);
		vipBenefitService.save(vipBenefit);
        redisUtils.delete(ConfigConstant.VIP_BENEFIT_KEY);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("admin:vipbenefit:update")
    @Operation(summary = "会员权益修改")
    public R update(@RequestBody VipBenefitParam param){
        ValidatorUtils.validateEntity(param, UpdateGroup.class);

        VipBenefitEntity vipBenefit = new VipBenefitEntity();
        BeanUtils.copyProperties(param, vipBenefit);
		vipBenefitService.updateById(vipBenefit);
        redisUtils.delete(ConfigConstant.VIP_BENEFIT_KEY);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("admin:vipbenefit:delete")
    @Operation(summary = "会员权益删除")
    public R delete(@RequestBody Integer[] ids){
		vipBenefitService.removeByIds(Arrays.asList(ids));
        redisUtils.delete(ConfigConstant.VIP_BENEFIT_KEY);
        return R.ok();
    }

}
