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

import io.linfeng.common.annotation.NoRepeatSubmit;
import io.linfeng.common.annotation.SysLog;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.modules.admin.param.AddPostByAdminParam;
import io.linfeng.modules.admin.param.DeletePostParam;
import io.linfeng.modules.admin.param.DownPostParam;
import io.linfeng.modules.admin.param.VideoPostForm;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.linfeng.modules.admin.entity.PostEntity;
import io.linfeng.modules.admin.param.PostParam;
import io.linfeng.modules.admin.service.PostService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.validator.group.UpdateGroup;


/**
 *
 * 帖子管理
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-23 20:49:55
 */
@RestController
@RequestMapping("admin/post")
@Tag(name = "管理端——帖子管理")
public class PostController {
    @Autowired
    private PostService postService;


    @GetMapping("/list")
    @RequiresPermissions("admin:post:list")
    @Operation(summary = "帖子列表")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = postService.queryPage(params);

        return R.ok().put("page", page);
    }


    @GetMapping("/info/{id}")
    @RequiresPermissions("admin:post:info")
    @Operation(summary = "帖子详情")
    public R info(@PathVariable("id") Integer id) {
        PostEntity post = postService.getById(id);

        return R.ok().put("post", post);
    }

    @SysLog("修改帖子")
    @PostMapping("/update")
    @RequiresPermissions("admin:post:update")
    @Operation(summary = "修改帖子")
    @NoRepeatSubmit
    public R update(@RequestBody PostParam param) {
        ValidatorUtils.validateEntity(param, UpdateGroup.class);

        PostEntity post = new PostEntity();
        BeanUtils.copyProperties(param, post);
        postService.updateById(post);

        return R.ok();
    }

    @SysLog("批量删除帖子")
    @PostMapping("/delete")
    @RequiresPermissions("admin:post:delete")
    @Operation(summary = "批量删除帖子")
    @NoRepeatSubmit
    public R delete(@RequestBody Integer[] ids) {
        postService.deleteByAdmin(Arrays.asList(ids));
        return R.ok();
    }


    @SysLog("删除帖子")
    @PostMapping("/deleteById")
    @RequiresPermissions("admin:post:delete")
    @Operation(summary = "删除帖子")
    @NoRepeatSubmit
    public R deleteById(@RequestBody DeletePostParam param) {
        postService.deletePostIdByAdmin(param);
        return R.ok();
    }


    @SysLog("下架帖子")
    @PostMapping("/down")
    @RequiresPermissions("admin:post:update")
    @Operation(summary = "下架帖子")
    @NoRepeatSubmit
    public R down(@RequestBody DownPostParam param) {
        postService.downByAdmin(param);
        return R.ok();
    }


    @SysLog("上架帖子")
    @PostMapping("/up")
    @RequiresPermissions("admin:post:update")
    @Operation(summary = "上架帖子")
    @NoRepeatSubmit
    public R up(@RequestBody Integer[] ids) {
        postService.upByAdmin(Arrays.asList(ids));
        return R.ok();
    }


    @SysLog("批量执行视频贴上下架")
    @PostMapping("/doJob")
    @RequiresPermissions("admin:post:update")
    @Operation(summary = "批量执行视频贴上下架")
    @NoRepeatSubmit
    public R doVideoPostJob(@RequestBody VideoPostForm param) {
        postService.doVideoPostJob(param);
        return R.ok();
    }


    @PostMapping("/postSubmit")
    @RequiresPermissions("admin:post:save")
    @Operation(summary = "发帖子")
    @NoRepeatSubmit
    public R postSubmit(@RequestBody AddPostByAdminParam request) {
        ValidatorUtils.validateEntity(request);
        postService.postAddByAdmin(request);

        return R.ok();
    }
}
