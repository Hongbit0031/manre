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

import io.linfeng.common.utils.AppPageUtils;
import io.linfeng.common.utils.DataConvertUtils;
import io.linfeng.common.utils.Result;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.vo.app.DiscussDetailResponse;
import io.linfeng.common.vo.app.AppDiscussListResponse;
import io.linfeng.common.vo.app.AppDiscussSimpleResponse;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.entity.DiscussEntity;
import io.linfeng.modules.admin.service.DiscussService;
import io.linfeng.modules.app.annotation.Login;
import io.linfeng.modules.app.annotation.LoginUser;
import io.linfeng.modules.app.param.DiscussAddForm;
import io.linfeng.modules.app.param.DiscussDeleteForm;
import io.linfeng.modules.app.param.DiscussListForm;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("app/discuss")
@Tag(name = "移动端——话题模块")
public class AppDiscussController {

    @Autowired
    private DiscussService discussService;


    @PostMapping("/list")
    @Operation(summary = "话题列表")
    public Result<AppPageUtils<AppDiscussListResponse>> list(@RequestBody DiscussListForm request){
        AppPageUtils<AppDiscussListResponse> page = discussService.getDiscussList(request);

        return new Result<AppPageUtils<AppDiscussListResponse>>().ok(page);
    }

    @Login
    @PostMapping("/myDis")
    @Operation(summary = "我创建的话题")
    public Result<AppPageUtils<AppDiscussListResponse>> myDiscuss(@RequestBody DiscussListForm request, @Parameter(hidden = true) @LoginUser AppUserEntity user){
        AppPageUtils<AppDiscussListResponse> page = discussService.myDiscuss(request,user);

        return new Result<AppPageUtils<AppDiscussListResponse>>().ok(page);
    }

    @Login
    @PostMapping("/del")
    @Operation(summary = "圈主删除话题")
    public Result deleteDiscuss(@RequestBody DiscussDeleteForm request, @Parameter(hidden = true) @LoginUser AppUserEntity user){
        discussService.deleteDiscuss(request,user);
        return new Result();
    }

    @Login
    @PostMapping("/addDis")
    @Operation(summary = "圈主创建话题")
    public Result addDiscuss(@RequestBody DiscussAddForm request, @Parameter(hidden = true) @LoginUser AppUserEntity user){
        ValidatorUtils.validateEntity(request);
        Boolean success=discussService.addDiscuss(request,user);
        if(success){
            return new Result().okMessage("话题创建完成");
        }
        return new Result().error("话题创建失败");
    }

    @GetMapping("/detail")
    @Operation(summary = "话题详情页")
	@Parameters({
			@Parameter(name = "id", description = "话题id", in = ParameterIn.QUERY, required = true,
					schema = @Schema(implementation = Integer.class))
	})
    public Result<DiscussDetailResponse> detail(@RequestParam Integer id){
        DiscussDetailResponse response=discussService.detail(id);

        return new Result<DiscussDetailResponse>().ok(response);
    }

    @Login
    @GetMapping("/checkJoinTopic")
    @Operation(summary = "检查用户是否加入圈子")
	@Parameters({
			@Parameter(name = "id", description = "话题id", in = ParameterIn.QUERY, required = true,
					schema = @Schema(implementation = Integer.class))
	})
    public Result checkJoinTopic(@RequestParam Integer id, @Parameter(hidden = true) @LoginUser AppUserEntity user){
        discussService.checkJoinTopic(id,user.getUid());

        return new Result<>().ok();
    }

    @GetMapping("/discussList")
    @Operation(summary = "热门话题-近一个月-取7条")
    public Result<List<AppDiscussSimpleResponse>> discussList(){
        List<DiscussEntity> list = discussService.discussList();
        List<AppDiscussSimpleResponse> result = DataConvertUtils.sourceToTarget(list, AppDiscussSimpleResponse.class);
        return new Result<List<AppDiscussSimpleResponse>>().ok(result);
    }

    @GetMapping("/hotDiscussList")
    @Operation(summary = "热门话题-平台综合-取20条")
    public Result<List<AppDiscussSimpleResponse>> hotDiscussList(){
        List<DiscussEntity> list = discussService.hotDiscussList();
        List<AppDiscussSimpleResponse> result = DataConvertUtils.sourceToTarget(list, AppDiscussSimpleResponse.class);
        return new Result<List<AppDiscussSimpleResponse>>().ok(result);
    }

}
