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
package io.linfeng.modules.app.controller;

import cn.hutool.core.util.ObjectUtil;
import io.linfeng.common.annotation.NoRepeatSubmit;
import io.linfeng.common.utils.DataConvertUtils;
import io.linfeng.common.utils.Result;
import io.linfeng.common.vo.app.*;
import io.linfeng.common.utils.AppPageUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.entity.PostEntity;
import io.linfeng.modules.admin.service.PostService;
import io.linfeng.modules.app.annotation.Login;
import io.linfeng.modules.app.annotation.LoginUser;
import io.linfeng.modules.app.param.*;
import io.linfeng.modules.app.service.PostCollectionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 
 * 帖子APP端
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-24 20:09:55
 */
@Tag(name = "移动端——帖子模块")
@RestController
@RequestMapping("app/post")
public class AppPostController {

    @Autowired
    private PostService postService;

    @Autowired
    private PostCollectionService postCollectionService;


    /**
     * 删除
     */
    @Login
    @PostMapping("/del")
    @Operation(summary = "用户删除帖子")
    public Result delete(@RequestBody AddPostDeleteForm request,
                    @Parameter(hidden = true) @LoginUser AppUserEntity user){

		postService.del(request.getId(),user.getUid());

        return new Result();
    }

    @GetMapping("/detail")
    @Operation(summary = "获取帖子详情")
    public Result<PostDetailResponse> detail(@Parameter(description = "帖子id", required = true) @RequestParam Integer id){

        PostDetailResponse response=postService.detail(id);
        return new Result<PostDetailResponse>().ok(response);
    }



    /**
     * 根据话题ID查询帖子列表分页
     */
    @PostMapping("/getPostListByDiscussId")
    @Operation(summary = "根据话题ID查询帖子列表分页")
    public Result<AppPageUtils<PostListResponse>> getPostListByDiscussId(@RequestBody PostListForm request){

        AppPageUtils<PostListResponse> page = postService.getPostListByDiscussId(request);

        return new Result<AppPageUtils<PostListResponse>>().ok(page);
    }

    /**
     * 根据圈子ID查询帖子列表分页
     */
    @PostMapping("/getListByTopicId")
    @Operation(summary = "根据圈子ID查询帖子列表分页")
    public Result<AppPageUtils<PostListResponse>> getListByTopicId(@RequestBody PostListForm request){

        AppPageUtils<PostListResponse> page = postService.getListByTopicId(request);

        return new Result<AppPageUtils<PostListResponse>>().ok(page);
    }

    /**
     * 根据用户ID查询帖子列表分页
     */
    @PostMapping("/getListByUid")
    @Operation(summary = "根据用户ID查询帖子列表分页")
    public Result<AppPageUtils<PostListResponse>> getListByUid(@RequestBody PostListForm request){

        AppPageUtils<PostListResponse> page = postService.getListByUid(request);

        return new Result<AppPageUtils<PostListResponse>>().ok(page);
    }

    /**
     * 短视频列表分页
     */
    @Login
    @PostMapping("/videoList")
    @Operation(summary = "短视频列表分页")
    public Result<AppPageUtils<ShortVideoListResponse>> videoList(@RequestBody VideoListForm request){
        AppPageUtils<ShortVideoListResponse> page = postService.queryShortVideoPageList(request);

        return new Result<AppPageUtils<ShortVideoListResponse>>().ok(page);
    }

    @PostMapping("/addReadCount")
    @Operation(summary = "添加浏览量")
    public Result addReadCount(@RequestBody AddReadCountForm request){
        postService.addReadCount(request.getPostId());

        return new Result();
    }

    @Login
    @PostMapping("/addComment")
    @NoRepeatSubmit
    @Operation(summary = "添加评论")
    public Result<Boolean> addComment(@RequestBody AddCommentForm request, @Parameter(hidden = true) @LoginUser AppUserEntity user){
        ValidatorUtils.validateEntity(request);
        Integer value = postService.addComment(request, user);
        if(value==1){
            return new Result<Boolean>().ok(true);
        }
        return new Result<Boolean>().ok(false);
    }


    @Login
    @PostMapping("/addCollection")
    @NoRepeatSubmit
    @Operation(summary = "帖子点赞收藏")
    public Result addCollection(@RequestBody AddCollectionForm request, @Parameter(hidden = true) @LoginUser AppUserEntity user){
        postService.addCollection(request,user);

        return new Result();
    }


    @Login
    @PostMapping("/cancelCollection")
    @NoRepeatSubmit
    @Operation(summary = "帖子取消点赞收藏")
    public Result cancelCollection(@RequestBody AddCollectionForm request, @Parameter(hidden = true) @LoginUser AppUserEntity user){
        postCollectionService.cancelCollection(request,user);
        return new Result();
    }


    @Login
    @GetMapping("/joinTopicPost")
    @Operation(summary = "用户加入圈子的动态列表")
    public Result<AppPageUtils<PostListResponse>> joinTopicPost(@Parameter(description = "分页页码", required = true) @RequestParam Integer page, @Parameter(hidden = true) @LoginUser AppUserEntity user){

        AppPageUtils<PostListResponse> pages = postService.joinTopicPost(page,user);
        return new Result<AppPageUtils<PostListResponse>>().ok(pages);
    }

    @GetMapping("/lastPost")
    @Operation(summary = "最新动态列表")
    public Result<AppPageUtils<PostListResponse>> lastPost(@Parameter(description = "分页页码", required = true) @RequestParam Integer page){

        AppPageUtils<PostListResponse> pages =postService.lastPost(page);
        return new Result<AppPageUtils<PostListResponse>>().ok(pages);
    }

    @GetMapping("/lastPostByFilter")
    @Operation(summary = "最新动态列表(按发布时间和评论时间)")
    public Result<AppPageUtils<PostListResponse>> lastPostByFilter(@Parameter(description = "分页页码", required = true) @RequestParam Integer page, 
                                                  @Parameter(description = "每页数量", required = true) @RequestParam Integer limit, 
                                                  @Parameter(description = "展示规则", required = true) @RequestParam Integer filter, 
                                                  @Parameter(description = "帖子类型", required = true) @RequestParam Integer type){

        AppPageUtils<PostListResponse> pages =postService.lastPostByFilter(page,limit,filter,type);
        return new Result<AppPageUtils<PostListResponse>>().ok(pages);
    }



    @Login
    @GetMapping("/followUserPost")
    @Operation(summary = "获取关注用户帖子")
    public Result<Object> followUserPost(@Parameter(description = "分页页码", required = true) @RequestParam Integer page, @Parameter(hidden = true) @LoginUser AppUserEntity user){

        AppPageUtils pages =postService.followUserPost(page,user);
        if(ObjectUtil.isNull(pages)){
            return new Result<>().error("您没有关注的用户");
        }
        return new Result<>().ok(pages);
    }


    @Login
    @PostMapping("/addPost")
    @Operation(summary = "发帖子")
    public Result<Object> addPost(@RequestBody AddPostForm request, @Parameter(hidden = true) @LoginUser AppUserEntity user){
        ValidatorUtils.validateEntity(request);
        Integer id=postService.addPost(request,user);
        if(id==0){
            return new Result<>().error("发布帖子失败");
        }
        return new Result<>().ok(id);
    }

    @Login
    @GetMapping("/qrCode")
    @Operation(summary = "分享二维码")
    public Result<String> qrCode(@Parameter(description = "帖子id", required = true) @RequestParam("postId") Integer postId,
                    @Parameter(description = "来源", required = true) @RequestParam("origin") String origin,
                    @Parameter(description = "url分享路径", required = true) @RequestParam("url") String url,
                    @Parameter(hidden = true) @LoginUser AppUserEntity user) throws Exception {


        String resultUrl = postService.getSharePic(postId,origin,url,user);
        return new Result<String>().ok(resultUrl);
    }

    @Login
    @GetMapping("/myCollectPost")
    @Operation(summary = "我点赞收藏的帖子")
    public Result<AppPageUtils<PostListResponse>> myCollectPost(@Parameter(description = "分页页码", required = true) @RequestParam("page") Integer page, @Parameter(hidden = true) @LoginUser AppUserEntity user){

        AppPageUtils<PostListResponse> pages =postService.myCollectPost(page,user);
        return new Result<AppPageUtils<PostListResponse>>().ok(pages);
    }

    @Login
    @GetMapping("/myPost")
    @Operation(summary = "我的帖子")
    public Result<AppPageUtils<PostListResponse>> myPost(@Parameter(description = "分页页码", required = true) @RequestParam("page") Integer page, @Parameter(hidden = true) @LoginUser AppUserEntity user){

        AppPageUtils<PostListResponse> pages =postService.myPost(page,user);
        return new Result<AppPageUtils<PostListResponse>>().ok(pages);
    }

    @GetMapping("/getPostListByType")
    @Operation(summary = "帖子分类列表")
    public Result<AppPageUtils<PostListResponse>> getPostListByType(@Parameter(description = "分页页码", required = true) @RequestParam("page") Integer page,
                                                  @Parameter(description = "类型", required = true) @RequestParam("type") Integer type){

        AppPageUtils<PostListResponse> pages =postService.getPostListByType(page,type);
        return new Result<AppPageUtils<PostListResponse>>().ok(pages);
    }


    @Login
    @GetMapping("/search")
    @Operation(summary = "搜索帖子")
    @NoRepeatSubmit(lockTime = 2000)
    public Result<AppPageUtils<PostListResponse>> search(@Parameter(description = "分页页码", required = true) @RequestParam("page") Integer page,
                                       @Parameter(description = "搜索关键词") @RequestParam("keyword") String keyword){

        AppPageUtils<PostListResponse> pages =postService.search(page,keyword);
        return new Result<AppPageUtils<PostListResponse>>().ok(pages);
    }

    @Login
    @PostMapping("/setAdmin")
    @Operation(summary = "设置圈子管理员")
    public Result setAdmin(@RequestBody SetAdminForm request, @Parameter(hidden = true) @LoginUser AppUserEntity user){
        Boolean setAdmin=postService.setAdmin(request,user);
        if(!setAdmin){
            return new Result().error("设置管理员失败");
        }
        return new Result();
    }

    @Login
    @PostMapping("/setPostTop")
    @Operation(summary = "设置圈内置顶")
    public Result setPostTop(@RequestBody SetPostTopForm request, @Parameter(hidden = true) @LoginUser AppUserEntity user){
        Boolean setAdmin=postService.setPostTop(request,user);
        if(!setAdmin){
            return new Result().error("设置圈内置顶失败");
        }
        return new Result();
    }

    @Login
    @PostMapping("/topPostDel")
    @Operation(summary = "解除圈内置顶")
    public Result topPostDel(@RequestBody SetPostTopForm request, @Parameter(hidden = true) @LoginUser AppUserEntity user){
        Boolean cancelAdmin=postService.topPostDel(request,user);
        if(!cancelAdmin){
            return new Result().error("解除圈内置顶失败");
        }
        return new Result();
    }

    @Login
    @PostMapping("/cancelAdmin")
    @Operation(summary = "解除圈子管理员")
    public Result cancelAdmin(@RequestBody SetAdminForm request, @Parameter(hidden = true) @LoginUser AppUserEntity user){
        Boolean cancelAdmin=postService.cancelAdmin(request,user);
        if(!cancelAdmin){
            return new Result().error("解除圈子管理员失败");
        }
        return new Result();
    }

    @Login
    @PostMapping("/getVipPostInfo")
    @Operation(summary = "获取用户是否付费")
    public Result<PostVipInfoResponse> getVipPostInfo(@RequestBody VipPostInfoForm request,@Parameter(hidden = true) @LoginUser AppUserEntity user){
        request.setUid(user.getUid());
        PostVipInfoResponse response=postService.getVipPostInfo(request);
        return new Result<PostVipInfoResponse>().ok(response);
    }


    @Login
    @PostMapping("/voteAdd")
    @Operation(summary = "发起投票")
    public Result voteAdd(@RequestBody AddVoteForm request, @Parameter(hidden = true) @LoginUser AppUserEntity user){
        ValidatorUtils.validateEntity(request);
        Integer id=postService.voteAdd(request,user);
        if(id==0){
            return new Result().error("发起投票失败");
        }
        return new Result<>().ok(id);
    }

    @Login
    @PostMapping("/vote/userVote")
    @Operation(summary = "用户投票")
    public Result userVote(@RequestBody UserVoteForm request,@Parameter(hidden = true) @LoginUser AppUserEntity user){
        postService.userVote(request,user);

        return new Result();
    }

    @GetMapping("/hotPost")
    @Operation(summary = "帖子热榜")
    public Result<List<HotPostResponse>> hotPost(){
        List<PostEntity> hotPost= postService.getHotPost();
        List<HotPostResponse> responses = DataConvertUtils.sourceToTarget(hotPost, HotPostResponse.class);
        return new Result<List<HotPostResponse>>().ok(responses);
    }

    @Login
    @PostMapping("/addArticle")
    @Operation(summary = "发文章")
    public Result<Object> addArticle(@RequestBody AddArticleForm request, @Parameter(hidden = true) @LoginUser AppUserEntity user){
        ValidatorUtils.validateEntity(request);
        Integer id=postService.addArticle(request,user.getUid());
        if(id==0){
            return new Result<>().error("发布文章失败");
        }
        return new Result<>().ok(id);
    }

}
