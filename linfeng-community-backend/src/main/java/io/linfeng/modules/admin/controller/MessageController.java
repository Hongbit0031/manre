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
import java.util.Map;

import io.linfeng.common.annotation.SysLog;
import io.linfeng.modules.admin.param.AddMessageParam;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import io.linfeng.modules.admin.entity.MessageEntity;
import io.linfeng.modules.admin.service.MessageService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;



/**
 * 
 *  消息管理
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-26 13:15:30
 */
@RestController
@RequestMapping("admin/message")
@Tag(name = "管理端——消息管理")
public class MessageController {

    @Autowired
    @Lazy
    private MessageService messageService;


    @GetMapping("/list")
    @RequiresPermissions("admin:message:list")
    @Operation(summary = "消息列表分页")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = messageService.queryPage(params);

        return R.ok().put("page", page);
    }



    @GetMapping("/info/{mId}")
    @RequiresPermissions("admin:message:info")
    @Operation(summary = "消息详情")
    public R info(@PathVariable("mId") Integer mId){
		MessageEntity message = messageService.getById(mId);

        return R.ok().put("message", message);
    }


    @PostMapping("/save")
    @RequiresPermissions("admin:message:save")
    @Operation(summary = "消息发送")
    public R save(@RequestBody AddMessageParam param){
        messageService.saveMessage(param);

        return R.ok();
    }


    @SysLog("删除消息")
    @PostMapping("/delete")
    @RequiresPermissions("admin:message:delete")
    @Operation(summary = "消息删除")
    public R delete(@RequestBody Integer[] mIds){
		messageService.removeByIds(Arrays.asList(mIds));

        return R.ok();
    }

}
