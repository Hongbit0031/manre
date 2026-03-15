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

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.linfeng.common.utils.RedisUtils;
import io.linfeng.common.utils.Result;
import io.linfeng.common.utils.WechatUtil;
import io.linfeng.modules.app.annotation.Login;
import io.linfeng.modules.app.entity.ChatMessageEntity;
import io.linfeng.modules.app.param.ClearChatMessageUnreadForm;
import io.linfeng.modules.app.param.GetChatListForm;
import io.linfeng.modules.app.service.ChatMessageService;
import io.linfeng.modules.app.service.FriendService;
import io.linfeng.modules.app.websocket.constant.MessageConstant;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author linfeng
 * @date 2022/11/16 16:55
 */
@RestController
@RequestMapping("app/chat")
@Tag(name = "移动端——私聊模块")
public class AppChatMessageController {


    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private FriendService friendService;

    @Autowired
    private RedisUtils redisUtils;


    /**
     * IM会话列表+缓存
     * @param param
     * @return
     */
    @Login
    @PostMapping("/list")
    @Operation(summary = "获取私聊分页列表")
    public Result<Object> getChatMessageList(@RequestBody GetChatListForm param){
        String cache=MessageConstant.IM_CHAT+param.getSessionId();
        if(param.getPageNum()==1){
            String result = redisUtils.get(cache);
            if(!WechatUtil.isEmpty(result)){
                return new Result<>().ok(JSONObject.parseObject(result));
            }
        }
        IPage<ChatMessageEntity> chatMessageList = chatMessageService.getChatMessageList(param);
        Map<String,Object> map=new HashMap<>();
        map.put("sessionId",param.getSessionId());
        map.put("pageInfo",chatMessageList);
        if(param.getPageNum()==1){
            redisUtils.set(cache,map,60 * 60 * 24 * 7);
        }
        return new Result<>().ok(map);
    }

    @PostMapping("/clearUnread")
    @Operation(summary = "清理私聊未读数据")
    public Result clearUnread(@RequestBody ClearChatMessageUnreadForm param) {
        friendService.clearUnread(param);
        return new Result();
    }

}
