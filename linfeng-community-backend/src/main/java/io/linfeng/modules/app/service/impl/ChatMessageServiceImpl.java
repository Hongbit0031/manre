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
package io.linfeng.modules.app.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.linfeng.common.utils.RedisUtils;
import io.linfeng.common.utils.WechatUtil;
import io.linfeng.common.vo.admin.AdminChatMessageResponse;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.service.AppUserService;
import io.linfeng.modules.app.dao.FriendDao;
import io.linfeng.modules.app.param.GetChatListForm;
import io.linfeng.modules.app.websocket.constant.MessageConstant;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.Query;

import io.linfeng.modules.app.dao.ChatMessageDao;
import io.linfeng.modules.app.entity.ChatMessageEntity;
import io.linfeng.modules.app.service.ChatMessageService;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;


@Service("chatMessageService")
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageDao, ChatMessageEntity> implements ChatMessageService {


    @Resource
    private FriendDao friendDao;

    @Resource
    private ChatMessageDao chatMessageDao;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private AppUserService appUserService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<ChatMessageEntity> wrapper = new LambdaQueryWrapper<>();
        String content = (String) params.get("content");
        String sessionId = (String) params.get("sessionId");
        String senderId = (String) params.get("senderId");
        String receiverId = (String) params.get("receiverId");
        if (!WechatUtil.isEmpty(content)) {
            wrapper.like(ChatMessageEntity::getContent,content);
        }
        if (!WechatUtil.isEmpty(sessionId)) {
            wrapper.eq(ChatMessageEntity::getSessionId,sessionId);
        }
        if (!WechatUtil.isEmpty(senderId) && NumberUtil.isInteger(senderId)) {
            wrapper.eq(ChatMessageEntity::getSenderId,senderId);
        }
        if (!WechatUtil.isEmpty(receiverId) && NumberUtil.isInteger(receiverId)) {
            wrapper.eq(ChatMessageEntity::getReceiverId,receiverId);
        }
        wrapper.orderByDesc(ChatMessageEntity::getId);
        IPage<ChatMessageEntity> page = this.page(
                new Query<ChatMessageEntity>().getPage(params),
                wrapper
        );
        List<ChatMessageEntity> list = page.getRecords();
        List<AdminChatMessageResponse> responseList=new ArrayList<>();
        list.forEach(item->{
            AdminChatMessageResponse response=new AdminChatMessageResponse();
            BeanUtils.copyProperties(item,response);
            AppUserEntity sendUser = appUserService.getById(item.getSenderId());
            response.setSenderAvatar(sendUser.getAvatar());
            response.setSenderUsername(sendUser.getUsername());
            AppUserEntity receiverUser = appUserService.getById(item.getReceiverId());
            response.setReceiverAvatar(receiverUser.getAvatar());
            response.setReceiverUsername(receiverUser.getUsername());
            if(item.getMessageType().equals("image")){
                //提取出图片url
                JSONObject jsonObject = new JSONObject(item.getContent());
                String imageUrl = jsonObject.getStr("compressUrl");
                response.setContent(imageUrl);
            }else if(item.getMessageType().equals("file")){
                //提取出文件url
                JSONObject jsonObject = new JSONObject(item.getContent());
                String fileUrl = jsonObject.getStr("url");
                response.setContent(fileUrl);
            }
            responseList.add(response);
        });
        PageUtils pageUtils=new PageUtils(page);
        pageUtils.setList(responseList);
        return pageUtils;
    }

    @Override
    @Transactional
    public void saveMessage(ChatMessageEntity chatMessage) {
        this.save(chatMessage);
        redisUtils.delete(MessageConstant.IM_CHAT+chatMessage.getSessionId());
        String lastMessage ;
        switch (chatMessage.getMessageType()) {
            case "image":
                lastMessage = "【图片】";
                break;
            case "file":
                lastMessage = "【文件】";
                break;
            case "video":
                lastMessage = "【视频】";
                break;
            default:
                lastMessage = chatMessage.getContent();
        }
        friendDao.updateInfo(chatMessage.getSessionId(), lastMessage);

    }

    @Override
    public void withdrawMessage(String messageId) {
        ChatMessageEntity message = this.getById(messageId);
        chatMessageDao.withdrawMessage(messageId);
        redisUtils.delete(MessageConstant.IM_CHAT+message.getSessionId());
        //修改好友表最新消息展示 如果有消息撤回就显示
        String lastMessage="撤回了一条消息";
        friendDao.updateWithdrawInfo(message.getSessionId(), lastMessage);
    }

    @Override
    public IPage<ChatMessageEntity> getChatMessageList(GetChatListForm param) {
        IPage<ChatMessageEntity> page = new Page<>(param.getPageNum(),param.getPageSize());
        LambdaQueryWrapper<ChatMessageEntity> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessageEntity::getSessionId,param.getSessionId());
        queryWrapper.orderByDesc(ChatMessageEntity::getId);
        if(param.getLastMessageId()!=null){
            queryWrapper.le(ChatMessageEntity::getId, param.getLastMessageId());
        }
        return this.baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public void deleteChatMessage(Integer friendId, Integer myId) {
        LambdaQueryWrapper<ChatMessageEntity> wrapper1=new LambdaQueryWrapper<>();
        wrapper1.eq(ChatMessageEntity::getSenderId,friendId);
        wrapper1.eq(ChatMessageEntity::getReceiverId,myId);
        this.remove(wrapper1);
        LambdaQueryWrapper<ChatMessageEntity> wrapper2=new LambdaQueryWrapper<>();
        wrapper2.eq(ChatMessageEntity::getSenderId,myId);
        wrapper2.eq(ChatMessageEntity::getReceiverId,friendId);
        this.remove(wrapper2);
    }


}