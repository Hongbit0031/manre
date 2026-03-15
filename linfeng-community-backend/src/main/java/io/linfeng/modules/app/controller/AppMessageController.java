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

import io.linfeng.common.utils.Result;
import io.linfeng.common.vo.app.MessageNumberResponse;
import io.linfeng.common.vo.app.MessageListResponse;
import io.linfeng.common.utils.AppPageUtils;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.service.MessageService;
import io.linfeng.modules.app.annotation.Login;
import io.linfeng.modules.app.annotation.LoginUser;
import io.linfeng.modules.app.param.MessageReadForm;
import io.linfeng.modules.app.param.UpdateChatStatusForm;
import io.linfeng.modules.app.param.UpdateSystemNoticeStatusForm;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 
 *  APP 系统消息模块
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-26 13:15:30
 */
@RestController
@RequestMapping("app/message")
@Tag(name = "移动端——系统消息模块")
public class AppMessageController {


    @Autowired
    private MessageService messageService;

    /**
     * 消息列表分页
     */
    @Login
    @GetMapping("/list")
    @Operation(summary = "消息列表分页")
	@Parameters({
			@Parameter(name = "type", description = "前端类型：1点赞2关注3评论", in = ParameterIn.QUERY, required = true,
					schema = @Schema(implementation = Integer.class)),
			@Parameter(name = "page", description = "分页页码", in = ParameterIn.QUERY, required = true,
					schema = @Schema(implementation = Integer.class))
	})
    public Result<AppPageUtils<MessageListResponse>> list(@RequestParam("type") Integer type,
                                     @RequestParam("page") Integer page,
                                     @Parameter(hidden = true) @LoginUser AppUserEntity user){
        AppPageUtils<MessageListResponse> pages=messageService.queryMessageList(type,page,user);

        return new Result<AppPageUtils<MessageListResponse>>().ok(pages);
    }

    /**
     * 消息数量
     */
    @PostMapping("/num")
    @Operation(summary = "消息数量")
    public Result<MessageNumberResponse> num(){
        MessageNumberResponse response=messageService.getMessageNumber();

        return new Result<MessageNumberResponse>().ok(response);
    }


    @Login
    @GetMapping("/status")
    @Operation(summary = "设置消息已读")
	@Parameters({
			@Parameter(name = "type", description = "类型", in = ParameterIn.QUERY, required = true,
					schema = @Schema(implementation = Integer.class))
	})
    public Result status(@RequestParam("type") Integer type,
                    @Parameter(hidden = true) @LoginUser AppUserEntity user){
        messageService.status(type, user.getUid());
        return new Result();
    }



    @Login
    @PostMapping("/readMessage")
    @Operation(summary = "设置用户点击的消息为已读")
    public Result readMessage(@RequestBody MessageReadForm request,
                         @Parameter(hidden = true) @LoginUser AppUserEntity user){
        messageService.readMessage(request, user.getUid());
        return new Result();
    }

    @Login
    @GetMapping("/readAllWatchInfo")
    @Operation(summary = "设置所有用户关注消息为已读")
    public Result readAllWatchInfo(@Parameter(hidden = true) @LoginUser AppUserEntity user){
        messageService.readAllWatchInfo(user.getUid());
        return new Result();
    }

    @Login
    @PostMapping("/articleMsgState")
    @Operation(summary = "设置系统消息为已读")
    public Result articleMsgState(@RequestBody UpdateSystemNoticeStatusForm request,
                             @Parameter(hidden = true) @LoginUser AppUserEntity user){
        Boolean status = messageService.articleMsgState(request, user.getUid());
        if(status){
            return new Result();
        }
        return new Result().error();
    }

    @Login
    @PostMapping("/updateSystemStatus")
    @Operation(summary = "设置所有系统消息为已读")
    public Result updateSystemStatus(@RequestBody UpdateChatStatusForm request,@Parameter(hidden = true)  @LoginUser AppUserEntity user){
        messageService.updateSystemStatus(user.getUid());
        return new Result();
    }

    @Login
    @PostMapping("/delSystemMsg")
    @Operation(summary = "用户删除系统消息")
    public Result delSystemMsg(@Parameter(hidden = true)  @LoginUser AppUserEntity user){
        messageService.delSystemMsg(user.getUid());
        return new Result();
    }
}
