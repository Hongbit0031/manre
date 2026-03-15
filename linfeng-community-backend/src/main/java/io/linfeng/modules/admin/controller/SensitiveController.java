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
import io.linfeng.common.utils.ConfigConstant;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.utils.RedisUtils;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.validator.group.AddGroup;
import io.linfeng.common.validator.group.UpdateGroup;
import io.linfeng.modules.admin.entity.SensitiveEntity;
import io.linfeng.modules.admin.param.SensitiveParam;
import io.linfeng.modules.admin.service.SensitiveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



/**
 * 敏感词库
 *
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-28 13:40:57
 */
@RestController
@RequestMapping("admin/sensitive")
@Tag(name = "管理端——敏感词库管理")
public class SensitiveController {

    @Autowired
    private SensitiveService sensitiveService;
    @Autowired
    private RedisUtils redisUtils;
    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("admin:sensitive:list")
    @Operation(summary = "敏感词库列表分页")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = sensitiveService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("admin:sensitive:info")
    @Operation(summary = "敏感词库详情")
    public R info(@PathVariable("id") Long id){
		SensitiveEntity sensitive = sensitiveService.getById(id);

        return R.ok().put("sensitive", sensitive);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("admin:sensitive:save")
    @Operation(summary = "敏感词库新增")
    public R save(@RequestBody SensitiveParam param){
        ValidatorUtils.validateEntity(param, AddGroup.class);

        SensitiveEntity sensitive = new SensitiveEntity();
        BeanUtils.copyProperties(param, sensitive);
		sensitiveService.save(sensitive);
        redisUtils.delete(ConfigConstant.SENSETIVE_LIST_KEY);
        return R.ok();
    }

    /**
     * 修改
     */
    @SysLog("修改敏感词库")
    @PostMapping("/update")
    @RequiresPermissions("admin:sensitive:update")
    @Operation(summary = "修改敏感词库")
    public R update(@RequestBody SensitiveParam param){
        ValidatorUtils.validateEntity(param, UpdateGroup.class);

        SensitiveEntity sensitive = new SensitiveEntity();
        BeanUtils.copyProperties(param, sensitive);
		sensitiveService.updateById(sensitive);
        redisUtils.delete(ConfigConstant.SENSETIVE_LIST_KEY);
        return R.ok();
    }

    /**
     * 删除
     */
    @SysLog("删除敏感词库")
    @PostMapping("/delete")
    @RequiresPermissions("admin:sensitive:delete")
    @Operation(summary = "删除敏感词库")
    public R delete(@RequestBody Long[] ids){
		sensitiveService.removeByIds(Arrays.asList(ids));
        redisUtils.delete(ConfigConstant.SENSETIVE_LIST_KEY);
        return R.ok();
    }

}
