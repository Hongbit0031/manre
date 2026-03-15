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

import io.linfeng.common.annotation.NoRepeatSubmit;
import io.linfeng.common.utils.*;
import io.linfeng.common.vo.app.ApplyInfoResponse;
import io.linfeng.common.vo.app.ClassTopicImageResponse;
import io.linfeng.common.vo.app.TopicDetailResponse;
import io.linfeng.common.vo.app.TopicListResponse;
import io.linfeng.common.vo.admin.UserInfoResponse;
import io.linfeng.common.vo.app.TopicUserShortResponse;
import io.linfeng.common.vo.app.AppUserShortInfoResponse;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.entity.CategoryEntity;
import io.linfeng.modules.admin.entity.TopicEntity;
import io.linfeng.modules.admin.service.AppUserService;
import io.linfeng.modules.admin.service.CategoryService;
import io.linfeng.modules.admin.service.TopicService;
import io.linfeng.modules.app.annotation.Login;
import io.linfeng.modules.app.annotation.LoginUser;
import io.linfeng.modules.app.param.*;
import io.linfeng.modules.app.service.TopicApplyService;
import io.linfeng.modules.sys.service.SysConfigService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * App端 圈子接口
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-21 17:01:12
 */
@RestController
@RequestMapping("app/topic")
@Tag(name = "移动端——圈子模块")
public class AppTopicController {


    @Autowired
    private TopicService topicService;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SysConfigService configService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private TopicApplyService topicApplyService;


    @GetMapping("/list")
    @Operation(summary = "圈子列表分页")
    @Parameters({
            @Parameter(name = Constant.PAGE, description = "当前页码，从1开始", required = true, schema = @Schema(type = "integer")),
            @Parameter(name = Constant.CLASSID, description = "分类id", required = true, schema = @Schema(type = "integer"))
    })
    public Result<AppPageUtils<TopicListResponse>> list(@Parameter(hidden = true) @RequestParam("page")Integer page,
                                                        @Parameter(description = "分类id", required = true) @RequestParam("classId")Integer classId){

        AppPageUtils<TopicListResponse> appPageInfo = topicService.queryByPageList(page,classId);
        return new Result<AppPageUtils<TopicListResponse>>().ok(appPageInfo);
    }


    @Login
    @PostMapping("/topicAdd")
    @Operation(summary = "创建圈子")
    @NoRepeatSubmit
    public Result save(@RequestBody TopicAddForm topic,@Parameter(hidden = true) @LoginUser AppUserEntity user){
        ValidatorUtils.validateEntity(topic);
        Integer id = topicService.topicAdd(topic, user);
        if(id!=0){
            return new Result<>().ok(id);
        }
        return new Result().error("创建圈子失败");
    }


    @Login
    @PostMapping("/topicEdit")
    @Operation(summary = "编辑圈子")
    public Result topicEdit(@RequestBody TopicUpdateForm topic,@Parameter(hidden = true) @LoginUser AppUserEntity user){
        ValidatorUtils.validateEntity(topic);
        Boolean update = topicService.topicEdit(topic, user);
        if(update){
            redisUtils.delete(ConfigConstant.H5_TOPIC_POSTER_KEY + topic.getId());
            redisUtils.delete(ConfigConstant.WX_TOPIC_POSTER_KEY + topic.getId());
            return new Result();
        }
        return new Result().error("圈子编辑失败");
    }


    @Operation(summary = "圈子详情")
    @GetMapping("/detail")
    public Result<TopicDetailResponse> detail(@Parameter(description = "圈子id", required = true) @RequestParam("id")Integer id){

        TopicDetailResponse topic=topicService.detail(id);
        return new Result<TopicDetailResponse>().ok(topic);
    }


    @Operation(summary = "圈子用户列表分页")
    @PostMapping("/user")
    public Result<AppPageUtils<UserInfoResponse>> user(@RequestBody TopicUserForm form){

        AppPageUtils<UserInfoResponse> page =appUserService.findTopicUserPage(form);

        return new Result<AppPageUtils<UserInfoResponse>>().ok(page);
    }


    @Operation(summary = "圈子成员管理-用户列表分页")
    @PostMapping("/userList")
    @Login
    public Result<AppPageUtils<TopicUserShortResponse>> userList(@RequestBody TopicUserForm form,@Parameter(hidden = true) @LoginUser AppUserEntity user){

        AppPageUtils<TopicUserShortResponse> page =appUserService.findUserPageByTopicAdmin(form,user);
        return new Result<AppPageUtils<TopicUserShortResponse>>().ok(page);
    }


    @Operation(summary = "圈子黑名单用户管理")
    @PostMapping("/blockUserList")
    @Login
    public Result<AppPageUtils<AppUserShortInfoResponse>> blockUserList(@RequestBody TopicUserForm form,@Parameter(hidden = true) @LoginUser AppUserEntity user){

        AppPageUtils<AppUserShortInfoResponse> page =appUserService.blockUserList(form,user);
        return new Result<AppPageUtils<AppUserShortInfoResponse>>().ok(page);
    }


    @Login
    @GetMapping("/joinTopic")
    @Operation(summary = "加入圈子")
    public Result<Integer> joinTopic(@Parameter(description = "圈子id", required = true) @RequestParam("id")Integer id,
                       @Parameter(hidden = true) @LoginUser AppUserEntity user){

        Integer result = topicService.joinTopic(id, user);
        return new Result<Integer>().ok(result);
    }


    @Operation(summary = "退出圈子")
    @GetMapping("/userTopicDel")
    @Login
    public Result userTopicDel(@Parameter(description = "圈子id", required = true) @RequestParam("id")Integer id,
                               @Parameter(hidden = true) @LoginUser AppUserEntity user){

        topicService.userTopicDel(id,user);
        return new Result();
    }


    @Operation(summary = "圈主解散圈子")
    @GetMapping("/topicDel")
    @Login
    public Result topicDel(@Parameter(description = "圈子id", required = true) @RequestParam("id")Integer id,
                           @Parameter(hidden = true) @LoginUser AppUserEntity user){

        topicService.topicDel(id,user);
        return new Result();
    }


    @Operation(summary = "查找用户加入的圈子")
    @PostMapping("/userJoinTopic")
    public Result<AppPageUtils<TopicListResponse>> userJoinTopic(@RequestBody UserJoinTopicForm request){

        AppPageUtils<TopicListResponse> page=topicService.userJoinTopic(request);
        return new Result<AppPageUtils<TopicListResponse>>().ok(page);
    }


    @Operation(summary = "用户进圈申请提交")
    @PostMapping("/joinTopicApply")
    @Login
    public Result joinTopicApply(@RequestBody UserJoinTopicApplyForm request,
                                              @Parameter(hidden = true) @LoginUser AppUserEntity user){

        topicService.joinTopicApply(request,user);
        return new Result<>().ok();
    }


    @Operation(summary = "热门圈子")
    @GetMapping("/hot")
    public Result<List<TopicEntity>> hot(){

        List<TopicEntity> list=topicService.hotTopic();
        return new Result<List<TopicEntity>>().ok(list);
    }


    @Operation(summary = "公告内容")
    @GetMapping("/notice")
    public Result<List<String>> notice(){
        String value = configService.getValue(Constant.NOTICE_CONTENT);
        List<String> list=new ArrayList<>();
        list.add(value);
        return new Result<List<String>>().ok(list);
    }


    @Operation(summary = "圈子列表分页展示并带三张图")
    @GetMapping("/classTopicAreImg")
    public Result<AppPageUtils<ClassTopicImageResponse>> classTopicAreImg(@Parameter(description = "分类id", required = false) @RequestParam(value = "classId", required = false) Integer classId,
                              @Parameter(description = "分页页码", required = true) @RequestParam("page")Integer page){

        AppPageUtils<ClassTopicImageResponse> pages=topicService.classTopicAreImg(classId,page);
        return new Result<AppPageUtils<ClassTopicImageResponse>>().ok(pages);
    }


    @Operation(summary = "用户自己的圈子列表")
    @Login
    @GetMapping("/myCreateTopic")
    public Result<AppPageUtils<TopicListResponse>> myCreateTopic(@Parameter(description = "分页页码", required = true) @RequestParam("page")Integer page,
                           @Parameter(hidden = true) @LoginUser AppUserEntity user){

        AppPageUtils<TopicListResponse> pages=topicService.myCreateTopic(page,user);
        return new Result<AppPageUtils<TopicListResponse>>().ok(pages);
    }


    @Operation(summary = "查找分类")
    @PostMapping("/classList")
    @Login
    public Result<List<CategoryEntity>> classList(){

        List<CategoryEntity> list = categoryService.list();
        return new Result<List<CategoryEntity>>().ok(list);
    }


    @Login
    @GetMapping("/search")
    @Operation(summary = "搜索圈子")
    @NoRepeatSubmit(lockTime = 3000)
    public Result<AppPageUtils<TopicListResponse>> search(@Parameter(description = "分页页码", required = true) @RequestParam("page") Integer page,
                                       @Parameter(description = "搜索关键词") @RequestParam("keyword") String keyword){

        AppPageUtils<TopicListResponse> pages =topicService.search(page,keyword);
        return new Result<AppPageUtils<TopicListResponse>>().ok(pages);
    }


    @Login
    @GetMapping("/detection")
    @Operation(summary = "检测用户是否可以创建圈子")
    public Result detection(@Parameter(hidden = true) @LoginUser AppUserEntity user){
        boolean result=topicService.detection(user.getUid());
        return new Result<>().ok(result);
    }


    @Login
    @GetMapping("/qrCode")
    @Operation(summary = "分享二维码")
    public Result<String> qrCode(@Parameter(description = "圈子id", required = true) @RequestParam("topicId") Integer topicId,
                    @Parameter(description = "来源", required = true) @RequestParam("origin") String origin,
                    @Parameter(description = "url分享路径", required = true) @RequestParam("url") String url,
                    @Parameter(hidden = true) @LoginUser AppUserEntity user) throws Exception {


        String resultUrl = topicService.getQrCode(topicId,origin,url,user);
        return new Result<String>().ok(resultUrl);
    }


    @Login
    @PostMapping("/giveTopic")
    @Operation(summary = "设置圈子管理员")
    public Result giveTopic(@RequestBody SetAdminForm request, @Parameter(hidden = true) @LoginUser AppUserEntity user){
        Boolean result=topicService.giveTopic(request,user);
        if(!result){
            return new Result().error("转让失败");
        }
        return new Result();
    }


    @Login
    @PostMapping("/doBlock")
    @Operation(summary = "圈子管理员拉黑圈子用户")
    public Result doBlock(@RequestBody BlockForm param,
                           @Parameter(hidden = true) @LoginUser AppUserEntity user){

        topicService.doBlock(param,user);
        return new Result();
    }


    @Login
    @PostMapping("/removeBlock")
    @Operation(summary = "圈子管理员解除黑名单")
    public Result removeBlock(@RequestBody BlockForm param,
                           @Parameter(hidden = true) @LoginUser AppUserEntity user){

        topicService.removeBlock(param,user);
        return new Result();
    }


    @Login
    @PostMapping("/applyInfoList")
    @Operation(summary = "用户入圈申请分页查询")
    public Result<AppPageUtils<ApplyInfoResponse>> applyInfoList(@RequestBody ApplyListForm request,@Parameter(hidden = true) @LoginUser AppUserEntity user){
        AppPageUtils<ApplyInfoResponse> page=topicApplyService.applyInfoList(request,user);
        return new Result<AppPageUtils<ApplyInfoResponse>>().ok(page);
    }


    @Login
    @PostMapping("/agreeApply")
    @Operation(summary = "圈子管理员审核进圈用户-通过")
    public Result agreeApply(@RequestBody ApplyForm param,
                          @Parameter(hidden = true) @LoginUser AppUserEntity user){

        topicApplyService.agreeApply(param,user);
        return new Result();
    }


    @Login
    @PostMapping("/rejectApply")
    @Operation(summary = "圈子管理员审核进圈用户-拒绝")
    public Result rejectApply(@RequestBody ApplyForm param,
                          @Parameter(hidden = true) @LoginUser AppUserEntity user){

        topicApplyService.rejectApply(param,user);
        return new Result();
    }


    @Operation(summary = "用户端——查询密圈子创建入口开关")
    @GetMapping("/privateCirclesOpen")
    public Result privateCirclesOpen(){
        boolean result = topicService.privateCirclesOpen();

        return new Result<>().ok(result);
    }


    @Login
    @GetMapping("/checkTopicApplyByAdmin")
    @Operation(summary = "查询是否有圈内管理员待处理的申请消息")
    public Result<Integer> checkTopicApplyByAdmin(@Parameter(description = "圈子id", required = true) @RequestParam("topicId") Integer topicId,
                              @Parameter(hidden = true) @LoginUser AppUserEntity user){

        Integer num = topicApplyService.checkTopicApplyByAdmin(topicId,user);
        return new Result<Integer>().ok(num);
    }

}
