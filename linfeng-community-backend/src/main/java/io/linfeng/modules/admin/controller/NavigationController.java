package io.linfeng.modules.admin.controller;

import java.util.Arrays;
import java.util.Map;

import io.linfeng.common.utils.DateUtil;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.validator.group.AddGroup;
import io.linfeng.common.validator.group.UpdateGroup;
import io.linfeng.modules.admin.param.NavigationParam;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.linfeng.modules.admin.entity.NavigationEntity;
import io.linfeng.modules.admin.service.NavigationService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;



/**
 * 导航栏模块
 *
 * @author JL.Yu
 * @email linfengtech001@163.com
 * @date 2023-02-04 22:14:59
 */
@RestController
@RequestMapping("admin/navigation")
@Tag(name = "管理端——导航栏管理")
public class NavigationController {


    @Autowired
    private NavigationService navigationService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("admin:navigation:list")
    @Operation(summary = "导航栏列表")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = navigationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("admin:navigation:info")
    @Operation(summary = "导航栏详情")
    public R info(@PathVariable("id") Integer id){
		NavigationEntity navigation = navigationService.getById(id);

        return R.ok().put("navigation", navigation);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("admin:navigation:save")
    @Operation(summary = "导航栏新增")
    public R save(@RequestBody NavigationParam param){
        ValidatorUtils.validateEntity(param, AddGroup.class);
        NavigationEntity navigation = new NavigationEntity();
        BeanUtils.copyProperties(param, navigation);
        navigation.setCreateTime(DateUtil.nowDateTime());
        navigation.setUpdateTime(DateUtil.nowDateTime());
		navigationService.save(navigation);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("admin:navigation:update")
    @Operation(summary = "导航栏修改")
    public R update(@RequestBody NavigationParam param){
        ValidatorUtils.validateEntity(param, UpdateGroup.class);
        NavigationEntity navigation = new NavigationEntity();
        BeanUtils.copyProperties(param, navigation);
        navigation.setUpdateTime(DateUtil.nowDateTime());
		navigationService.updateById(navigation);

        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("admin:navigation:delete")
    @Operation(summary = "导航栏删除")
    public R delete(@RequestBody Integer[] ids){
		navigationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }


    @PostMapping("/downBatch")
    @RequiresPermissions("admin:navigation:update")
    @Operation(summary = "导航栏批量下架")
    public R downBatch(@RequestBody Integer[] ids){
		navigationService.downBatch(Arrays.asList(ids));

        return R.ok();
    }


    @PostMapping("/upBatch")
    @RequiresPermissions("admin:navigation:update")
    @Operation(summary = "导航栏批量上架")
    public R upBatch(@RequestBody Integer[] ids){
		navigationService.upBatch(Arrays.asList(ids));

        return R.ok();
    }

}
