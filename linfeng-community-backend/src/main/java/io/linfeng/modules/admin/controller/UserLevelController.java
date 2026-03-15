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

import io.linfeng.common.exception.LinfengException;
import io.linfeng.common.utils.ConfigConstant;
import io.linfeng.common.utils.RedisUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.linfeng.modules.admin.entity.UserLevelEntity;
import io.linfeng.modules.admin.param.UserLevelParam;
import io.linfeng.modules.admin.service.UserLevelService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.validator.group.AddGroup;
import io.linfeng.common.validator.group.UpdateGroup;



/**
 * 用户经验值设置
 *
 * @author pity
 * @email linfengtech002@163.com
 * @date 2023-08-02 15:05:25
 */
@RestController
@RequestMapping("admin/userlevel")
@Tag(name = "管理端——用户经验值设置管理")
public class UserLevelController {

    @Autowired
    private UserLevelService userLevelService;

    @Autowired
    private RedisUtils redisUtils;


    @GetMapping("/list")
    @RequiresPermissions("admin:userlevel:list")
    @Operation(summary = "用户经验值列表")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = userLevelService.queryPage(params);

        return R.ok().put("page", page);
    }



    @GetMapping("/info/{id}")
    @RequiresPermissions("admin:userlevel:info")
    @Operation(summary = "用户经验值信息")
    public R info(@PathVariable("id") Integer id){
        UserLevelEntity userLevel = userLevelService.getById(id);

        return R.ok().put("userLevel", userLevel);
    }


    @PostMapping("/save")
    @RequiresPermissions("admin:userlevel:save")
    @Operation(summary = "用户经验值保存")
    public R save(@RequestBody UserLevelParam param){
        ValidatorUtils.validateEntity(param, AddGroup.class);

        UserLevelEntity userLevel = new UserLevelEntity();
        BeanUtils.copyProperties(param, userLevel);
        if(userLevel.getLevelId()<1||userLevel.getLevelId()>5){
            throw new LinfengException("用户等级ID需要在1到5之间");
        }
        redisUtils.delete(ConfigConstant.USER_LEVEL_KEY);
        userLevelService.save(userLevel);


        return R.ok();
    }


    @PostMapping("/update")
    @RequiresPermissions("admin:userlevel:update")
    @Operation(summary = "用户经验值修改")
    public R update(@RequestBody UserLevelParam param){
        ValidatorUtils.validateEntity(param, UpdateGroup.class);

        UserLevelEntity userLevel = new UserLevelEntity();
        BeanUtils.copyProperties(param, userLevel);
        if(userLevel.getLevelId()<1||userLevel.getLevelId()>5){
            throw new LinfengException("用户等级ID需要在1到5之间");
        }
        redisUtils.delete(ConfigConstant.USER_LEVEL_KEY);
        userLevelService.updateById(userLevel);

        return R.ok();
    }


    @PostMapping("/delete")
    @RequiresPermissions("admin:userlevel:delete")
    @Operation(summary = "用户经验值删除")
    public R delete(@RequestBody Integer[] ids){
        redisUtils.delete(ConfigConstant.USER_LEVEL_KEY);
        userLevelService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
