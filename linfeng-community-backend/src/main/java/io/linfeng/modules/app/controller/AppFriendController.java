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

import io.linfeng.common.utils.Result;
import io.linfeng.common.vo.app.AppFriendResponse;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.app.annotation.Login;
import io.linfeng.modules.app.annotation.LoginUser;
import io.linfeng.modules.app.param.AgreePersonForm;
import io.linfeng.modules.app.param.ApplyFriendForm;
import io.linfeng.modules.app.param.DeleteFriendForm;
import io.linfeng.modules.app.service.FriendService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author linfeng
 * @date 2022/11/16 16:55
 */
@RestController
@RequestMapping("app/friend")
@Tag(name = "移动端——好友模块")
public class AppFriendController {

    @Autowired
    private FriendService friendService;


    @Login
    @GetMapping("/list")
    @Operation(summary = "获取好友列表")
    public Result<List<AppFriendResponse>> getFriendList(@Parameter(hidden = true) @LoginUser AppUserEntity user){
        List<AppFriendResponse> list = friendService.getFriendList(user.getUid());
        return new Result<List<AppFriendResponse>>().ok(list);
    }

    @Login
    @PostMapping("/deleteFriend")
    @Operation(summary = "删除指定好友")
    public Result deleteFriend(@Parameter(hidden = true) @LoginUser AppUserEntity user,
                          @RequestBody DeleteFriendForm param){
        friendService.deleteFriend(param.getId(),user.getUid());
        return new Result();
    }

    /**
     * websocket掉线状态下的申请好友方式
     */
    @Login
    @PostMapping("/applyFriend")
    @Operation(summary = "申请好友")
    public Result applyFriend(@RequestBody ApplyFriendForm param){
        friendService.applyFriend(param.getData());
        return new Result();
    }

    /**
     * websocket掉线状态下的同意申请好友方式
     */
    @Login
    @PostMapping("/agreePersonApply")
    @Operation(summary = "同意申请好友")
    public Result agreePersonApply(@RequestBody AgreePersonForm param){
        friendService.agreePersonApply(param.getId());
        return new Result();
    }

}
