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

import io.linfeng.common.annotation.NoRepeatSubmit;
import io.linfeng.common.annotation.SysLog;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.linfeng.modules.admin.entity.CommentEntity;
import io.linfeng.modules.admin.param.CommentParam;
import io.linfeng.modules.admin.service.CommentService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.validator.group.AddGroup;
import io.linfeng.common.validator.group.UpdateGroup;
import org.springframework.beans.BeanUtils;



/**
 * 
 * 评论管理
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-24 21:29:22
 */
@RestController
@RequestMapping("admin/comment")
@Tag(name = "管理端——评论管理")
public class CommentController {
    @Autowired
    private CommentService commentService;



    @GetMapping("/list")
    @RequiresPermissions("admin:comment:list")
    @Operation(summary = "评论列表")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = commentService.queryPage(params);

        return R.ok().put("page", page);
    }



    @GetMapping("/info/{id}")
    @RequiresPermissions("admin:comment:info")
    @Operation(summary = "评论详情")
    public R info(@PathVariable("id") Long id){
		CommentEntity comment = commentService.getById(id);

        return R.ok().put("comment", comment);
    }




    @PostMapping("/save")
    @RequiresPermissions("admin:comment:save")
    @Operation(summary = "评论保存")
    public R save(@RequestBody CommentParam param){
		ValidatorUtils.validateEntity(param, AddGroup.class);
		
		CommentEntity comment = new CommentEntity();
		BeanUtils.copyProperties(param, comment);
		commentService.save(comment);

        return R.ok();
    }


    @NoRepeatSubmit
    @SysLog("修改评论")
    @PostMapping("/update")
    @RequiresPermissions("admin:comment:update")
    @Operation(summary = "修改评论")
    public R update(@RequestBody CommentParam param){
		ValidatorUtils.validateEntity(param, UpdateGroup.class);
		
		CommentEntity comment = new CommentEntity();
		BeanUtils.copyProperties(param, comment);
		commentService.updateCommentById(comment);

        return R.ok();
    }



    @SysLog("删除评论")
    @PostMapping("/delete")
    @RequiresPermissions("admin:comment:delete")
    @Operation(summary = "删除评论")
    @NoRepeatSubmit
    public R delete(@RequestBody Long[] ids){
        commentService.deleteByAdmin(Arrays.asList(ids));
        return R.ok();
    }

}
