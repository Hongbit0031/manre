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
import io.linfeng.common.utils.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.linfeng.modules.admin.entity.LinkEntity;
import io.linfeng.modules.admin.param.LinkParam;
import io.linfeng.modules.admin.service.LinkService;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.validator.group.AddGroup;
import io.linfeng.common.validator.group.UpdateGroup;
import org.springframework.beans.BeanUtils;


/**
 * 
 *
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-26 14:05:38
 */
@RestController
@RequestMapping("admin/link")
@Tag(name = "管理端——轮播图管理")
public class LinkController {

    @Autowired
    private LinkService linkService;
    @Autowired
    private RedisUtils redisUtils;


    @GetMapping("/list")
    @RequiresPermissions("admin:link:list")
    @Operation(summary = "轮播图列表")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = linkService.queryPage(params);

        return R.ok().put("page", page);
    }



    @GetMapping("/info/{id}")
    @RequiresPermissions("admin:link:info")
    @Operation(summary = "轮播图详情")
    public R info(@PathVariable("id") Integer id){
		LinkEntity link = linkService.getById(id);

        return R.ok().put("link", link);
    }


    @SysLog("新增轮播图")
    @PostMapping("/save")
    @RequiresPermissions("admin:link:save")
    @Operation(summary = "新增轮播图")
    public R save(@RequestBody LinkParam param){
		ValidatorUtils.validateEntity(param, AddGroup.class);
		
		LinkEntity link = new LinkEntity();
		BeanUtils.copyProperties(param, link);
        link.setCreateTime(DateUtil.nowDateTime());
		linkService.save(link);
        redisUtils.delete(ConfigConstant.BANNER_LIST_KEY_MINE);
        redisUtils.delete(ConfigConstant.BANNER_LIST_KEY_SQUARE);
        return R.ok();
    }



    @SysLog("修改轮播图")
    @PostMapping("/update")
    @RequiresPermissions("admin:link:update")
    @Operation(summary = "修改轮播图")
    public R update(@RequestBody LinkParam param){
		ValidatorUtils.validateEntity(param, UpdateGroup.class);
		
		LinkEntity link = new LinkEntity();
		BeanUtils.copyProperties(param, link);
		linkService.updateById(link);
        redisUtils.delete(ConfigConstant.BANNER_LIST_KEY_MINE);
        redisUtils.delete(ConfigConstant.BANNER_LIST_KEY_SQUARE);
        return R.ok();
    }

    /**
     * 删除
     */
    @SysLog("删除轮播图")
    @PostMapping("/delete")
    @RequiresPermissions("admin:link:delete")
    @Operation(summary = "删除轮播图")
    public R delete(@RequestBody Integer[] ids){
		linkService.removeByIds(Arrays.asList(ids));
        redisUtils.delete(ConfigConstant.BANNER_LIST_KEY_MINE);
        redisUtils.delete(ConfigConstant.BANNER_LIST_KEY_SQUARE);
        return R.ok();
    }

}
