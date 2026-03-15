/**
 * -----------------------------------
 *  Copyright (c) 2022-2026
 *  All rights reserved, Designed By www.linfengtech.cn
 * 林风社交论坛商业版本请务必保留此注释头信息
 * 商业版授权联系技术客服	 QQ:  3582996245
 * 严禁分享、盗用、转卖源码或非法牟利！
 * 版权所有 ，侵权必究！
 * -----------------------------------
 */
package io.linfeng.modules.app.controller;

import io.linfeng.common.annotation.NoRepeatSubmit;
import io.linfeng.common.utils.AppPageUtils;
import io.linfeng.common.utils.Result;
import io.linfeng.common.vo.app.AppChildrenCommentResponse;
import io.linfeng.common.vo.app.AppCommentResponse;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.service.CommentService;
import io.linfeng.modules.app.annotation.Login;
import io.linfeng.modules.app.annotation.LoginUser;
import io.linfeng.modules.app.param.AddThumbsForm;
import io.linfeng.modules.app.param.DelCommentForm;
import io.linfeng.modules.app.service.CommentThumbsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 
 * APP评论接口
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-24 21:29:22
 */
@RestController
@RequestMapping("app/comment")
@Tag(name = "移动端——评论模块")
public class AppCommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentThumbsService commentThumbsService;

    /**
     * 评论列表
     */
    @GetMapping("/list")
    @Operation(summary = "评论列表")
    public Result<AppPageUtils<AppCommentResponse>> list(@Parameter(description = "帖子id", required = true) @RequestParam("postId")Integer postId, @Parameter(description = "分页页码", required = true) @RequestParam("page")Integer page){
        if(page==1){
            return new Result<AppPageUtils<AppCommentResponse>>().ok(commentService.getCommentListInCache(postId));
        }
        AppPageUtils<AppCommentResponse> pages = commentService.queryCommentPage(postId, page);
        return new Result<AppPageUtils<AppCommentResponse>>().ok(pages);
    }

    /**
     * 子评论列表
     */
    @GetMapping("/remainComment")
    @Operation(summary = "子评论列表")
    public Result<List<AppChildrenCommentResponse>> remainComment(@Parameter(description = "评论id", required = true) @RequestParam("id")Long id){

        List<AppChildrenCommentResponse> list =commentService.remainComment(id);
        return new Result<List<AppChildrenCommentResponse>>().ok(list);
    }

    /**
     * 单条评论删除
     */
    @Login
    @PostMapping("/del")
    @Operation(summary = "单条评论删除")
    public Result del(@RequestBody DelCommentForm request, @Parameter(hidden = true) @LoginUser AppUserEntity user){
		commentService.del(request,user);

        return new Result();
    }


    /**
     * 评论区的点赞
     */
    @Login
    @PostMapping("/thumbs")
    @NoRepeatSubmit
    @Operation(summary = "评论区的点赞")
    public Result thumbs(@RequestBody AddThumbsForm request,@Parameter(hidden = true) @LoginUser AppUserEntity user){

        commentThumbsService.addThumbs(request,user);
        return new Result();
    }

    /**
     * 取消评论区的点赞
     */
    @Login
    @PostMapping("/cancelThumbs")
    @NoRepeatSubmit
    @Operation(summary = "取消评论区的点赞")
    public Result cancelThumbs(@RequestBody AddThumbsForm request,@Parameter(hidden = true) @LoginUser AppUserEntity user){

        commentThumbsService.cancelThumbs(request,user);
        return new Result();
    }

}
