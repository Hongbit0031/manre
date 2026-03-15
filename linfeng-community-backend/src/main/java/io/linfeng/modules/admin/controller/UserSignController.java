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

import io.linfeng.modules.admin.entity.UserSignEntity;
import io.linfeng.modules.admin.param.UserSignParam;
import io.linfeng.modules.admin.service.UserSignService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.validator.group.AddGroup;
import io.linfeng.common.validator.group.UpdateGroup;



/**
 * 签到记录
 *
 * @author linfeng
 * @email linfengtech001@163.com
 * @date 2022-05-07 15:01:03
 */
@RestController
@RequestMapping("admin/usersign")
@Tag(name = "管理端——签到记录")
public class UserSignController {
    @Autowired
    private UserSignService userSignService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("admin:usersign:list")
    @Operation(summary = "签到记录列表分页")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = userSignService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("admin:usersign:info")
    @Operation(summary = "签到记录详情")
    public R info(@PathVariable("id") Long id){
		UserSignEntity userSign = userSignService.getById(id);

        return R.ok().put("userSign", userSign);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("admin:usersign:save")
    @Operation(summary = "签到记录保存")
    public R save(@RequestBody UserSignParam param){
        ValidatorUtils.validateEntity(param, AddGroup.class);

        UserSignEntity userSign = new UserSignEntity();
        BeanUtils.copyProperties(param, userSign);
		userSignService.save(userSign);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("admin:usersign:update")
    @Operation(summary = "签到记录修改")
    public R update(@RequestBody UserSignParam param){
        ValidatorUtils.validateEntity(param, UpdateGroup.class);

        UserSignEntity userSign = new UserSignEntity();
        BeanUtils.copyProperties(param, userSign);
		userSignService.updateById(userSign);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("admin:usersign:delete")
    @Operation(summary = "签到记录删除")
    public R delete(@RequestBody Long[] ids){
		userSignService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
