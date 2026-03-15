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

import io.linfeng.common.utils.ConfigConstant;
import io.linfeng.common.utils.RedisUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.linfeng.modules.admin.entity.UserMenuEntity;
import io.linfeng.modules.admin.param.UserMenuParam;
import io.linfeng.modules.admin.service.UserMenuService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.validator.group.AddGroup;
import io.linfeng.common.validator.group.UpdateGroup;



/**
 * 用户菜单
 *
 * @author linfeng
 * @email 3582996245@qq.com
 * @date 2022-07-22 09:33:30
 */
@RestController
@RequestMapping("admin/usermenu")
@Tag(name = "管理端——用户菜单管理")
public class UserMenuController {

    @Autowired
    private UserMenuService userMenuService;
    @Autowired
    private RedisUtils redisUtils;



    @GetMapping("/list")
    @RequiresPermissions("admin:usermenu:list")
    @Operation(summary = "用户菜单列表")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = userMenuService.queryPage(params);

        return R.ok().put("page", page);
    }




    @GetMapping("/info/{id}")
    @RequiresPermissions("admin:usermenu:info")
    @Operation(summary = "用户菜单详情")
    public R info(@PathVariable("id") Integer id){
		UserMenuEntity userMenu = userMenuService.getById(id);

        return R.ok().put("userMenu", userMenu);
    }



    @PostMapping("/save")
    @RequiresPermissions("admin:usermenu:save")
    @Operation(summary = "用户菜单保存")
    public R save(@RequestBody UserMenuParam param){
        ValidatorUtils.validateEntity(param, AddGroup.class);
        UserMenuEntity userMenu = new UserMenuEntity();
        BeanUtils.copyProperties(param, userMenu);
        redisUtils.delete(ConfigConstant.USER_MENU_CONFIG_KEY);
		userMenuService.save(userMenu);

        return R.ok();
    }



    @PostMapping("/update")
    @RequiresPermissions("admin:usermenu:update")
    @Operation(summary = "用户菜单修改")
    public R update(@RequestBody UserMenuParam param){
        ValidatorUtils.validateEntity(param, UpdateGroup.class);
        UserMenuEntity userMenu = new UserMenuEntity();
        BeanUtils.copyProperties(param, userMenu);
        redisUtils.delete(ConfigConstant.USER_MENU_CONFIG_KEY);
		userMenuService.updateById(userMenu);

        return R.ok();
    }



    @PostMapping("/delete")
    @RequiresPermissions("admin:usermenu:delete")
    @Operation(summary = "用户菜单删除")
    public R delete(@RequestBody Integer[] ids){
        redisUtils.delete(ConfigConstant.USER_MENU_CONFIG_KEY);
		userMenuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @PostMapping("/downBatch")
    @RequiresPermissions("admin:usermenu:update")
    @Operation(summary = "用户菜单批量下架")
    public R downBatch(@RequestBody Integer[] ids){
        redisUtils.delete(ConfigConstant.USER_MENU_CONFIG_KEY);
		userMenuService.downBatch(Arrays.asList(ids));

        return R.ok();
    }

    @PostMapping("/upBatch")
    @RequiresPermissions("admin:usermenu:update")
    @Operation(summary = "用户菜单批量上架")
    public R upBatch(@RequestBody Integer[] ids){
        redisUtils.delete(ConfigConstant.USER_MENU_CONFIG_KEY);
		userMenuService.upBatch(Arrays.asList(ids));

        return R.ok();
    }

}
