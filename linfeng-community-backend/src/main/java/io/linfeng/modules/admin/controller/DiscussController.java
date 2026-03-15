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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.linfeng.common.annotation.SysLog;
import io.linfeng.common.exception.LinfengException;
import io.linfeng.modules.admin.entity.PostEntity;
import io.linfeng.modules.admin.service.PostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.linfeng.modules.admin.entity.DiscussEntity;
import io.linfeng.modules.admin.param.DiscussParam;
import io.linfeng.modules.admin.service.DiscussService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.validator.group.AddGroup;
import io.linfeng.common.validator.group.UpdateGroup;
import org.springframework.beans.BeanUtils;



/**
 * 
 *
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-24 13:44:37
 */
@RestController
@RequestMapping("admin/discuss")
@Tag(name = "管理端——话题管理")
public class DiscussController {

    @Autowired
    private DiscussService discussService;
    @Autowired
    private PostService postService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("admin:discuss:list")
    @Operation(summary = "话题列表分页")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = discussService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("admin:discuss:info")
    @Operation(summary = "话题详情")
    public R info(@PathVariable("id") Integer id){
		DiscussEntity discuss = discussService.getById(id);

        return R.ok().put("discuss", discuss);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("admin:discuss:save")
    @Operation(summary = "话题保存")
    public R save(@RequestBody DiscussParam param){
		ValidatorUtils.validateEntity(param, AddGroup.class);
		
		DiscussEntity discuss = new DiscussEntity();
		BeanUtils.copyProperties(param, discuss);
		discussService.saveDiscussByAdmin(discuss);

        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("admin:discuss:update")
    @Operation(summary = "话题修改")
    public R update(@RequestBody DiscussParam param){
		ValidatorUtils.validateEntity(param, UpdateGroup.class);
		
		DiscussEntity discuss = new DiscussEntity();
		BeanUtils.copyProperties(param, discuss);
		discussService.updateById(discuss);

        return R.ok();
    }

    /**
     * 删除
     */
    @SysLog("删除话题")
    @PostMapping("/delete")
    @RequiresPermissions("admin:discuss:delete")
    @Operation(summary = "删除话题")
    public R delete(@RequestBody Integer[] ids){
        List<Integer> list = Arrays.asList(ids);
        list.forEach(id->{
            List<PostEntity> entityList = postService.lambdaQuery().eq(PostEntity::getDiscussId, id).list();
            if(entityList.size()>0){
                throw new LinfengException("请先删除该话题下的帖子");
            }
            discussService.removeById(id);
        });
        return R.ok();
    }

    @SysLog("删除话题下的帖子")
    @PostMapping("/deletePostInDiscuss")
    @RequiresPermissions("admin:discuss:delete")
    @Operation(summary = "删除话题下的帖子")
    public R deletePostInDiscuss(@RequestBody Integer[] ids){
        List<Integer> list = Arrays.asList(ids);
        list.forEach(id->{
            List<PostEntity> entityList = postService.lambdaQuery().eq(PostEntity::getDiscussId, id).list();
            if(entityList.size()>0){
                List<Integer> idList = entityList.stream().map(PostEntity::getId).collect(Collectors.toList());
                postService.removeByIds(idList);
            }
        });
        return R.ok();
    }

    @GetMapping("/getDiscussList/{id}")
    @Operation(summary = "查询话题列表")
    public R getDiscussList(@PathVariable("id") Integer id){
        List<DiscussEntity> list = discussService.getListByTopicId(id);
        return R.ok().put("result",list);
    }
}
