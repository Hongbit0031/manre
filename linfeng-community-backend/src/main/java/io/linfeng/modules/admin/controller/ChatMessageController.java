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

import io.linfeng.modules.app.entity.ChatMessageEntity;
import io.linfeng.modules.app.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.R;



/**
 * 私聊模块
 *
 * @author linfeng
 * @email linfengtech002@qq.com
 * @date 2026-02-27 10:55:40
 */
@RestController
@RequestMapping("admin/chatmessage")
@Tag(name = "管理端——私聊表模块")
public class ChatMessageController {
    @Autowired
    private ChatMessageService chatMessageService;


    @GetMapping("/list")
    @RequiresPermissions("admin:chatmessage:list")
    @Operation(summary = "私聊表列表分页")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = chatMessageService.queryPage(params);

        return R.ok().put("page", page);
    }


    @GetMapping("/info/{id}")
    @RequiresPermissions("admin:chatmessage:info")
    @Operation(summary = "私聊表详情")
    public R info(@PathVariable("id") Long id){
		ChatMessageEntity chatMessage = chatMessageService.getById(id);

        return R.ok().put("chatMessage", chatMessage);
    }


    @PostMapping("/delete")
    @RequiresPermissions("admin:chatmessage:delete")
    @Operation(summary = "私聊表删除")
    public R delete(@RequestBody Long[] ids){
		chatMessageService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
