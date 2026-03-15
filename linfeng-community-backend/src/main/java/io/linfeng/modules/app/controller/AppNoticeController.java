package io.linfeng.modules.app.controller;

import io.linfeng.common.utils.Result;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.app.annotation.Login;
import io.linfeng.modules.app.annotation.LoginUser;
import io.linfeng.modules.app.param.ReadNoticeForm;
import io.linfeng.modules.app.param.RejectNoticeForm;
import io.linfeng.modules.app.service.NoticeService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author linfeng
 */
@RestController
@RequestMapping("app/notice")
@Tag(name = "移动端——IM通知模块")
public class AppNoticeController {

    @Autowired
    private NoticeService noticeService;


    @Login
    @GetMapping("/list")
    @Operation(summary = "获取IM通知列表")
    public Result<Map<String, Object>> getNoticeList(@Parameter(hidden = true) @LoginUser AppUserEntity user){
        Map<String, Object> map = noticeService.getNoticeList(user.getUid());
        return new Result<Map<String, Object>>().ok(map);
    }

    @Login
    @PostMapping("/readById")
    @Operation(summary = "根据id已读消息")
    public Result readById(@RequestBody ReadNoticeForm param){
        noticeService.readById(param);
        return new Result();
    }

    @Login
    @PostMapping("/reject")
    @Operation(summary = "拒绝好友申请")
    public Result reject(@RequestBody RejectNoticeForm param){
        noticeService.reject(param);
        return new Result();
    }

    @Login
    @PostMapping("/delete")
    @Operation(summary = "删除好友申请")
    public Result delete(@RequestBody ReadNoticeForm param){
        noticeService.removeById(param.getId());
        return new Result();
    }

}
