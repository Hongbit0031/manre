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
package io.linfeng.modules.admin.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.linfeng.common.exception.LinfengException;
import io.linfeng.common.utils.*;
import io.linfeng.common.vo.admin.AdminMessageListResponse;
import io.linfeng.common.vo.app.AppSystemMessageResponse;
import io.linfeng.common.vo.app.AppUserShortInfoResponse;
import io.linfeng.common.vo.app.MessageListResponse;
import io.linfeng.common.vo.app.MessageNumberResponse;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.service.AppUserService;
import io.linfeng.modules.admin.service.PostService;
import io.linfeng.modules.admin.param.AddMessageParam;
import io.linfeng.modules.app.param.ChatListForm;
import io.linfeng.modules.app.param.MessageReadForm;
import io.linfeng.modules.app.param.UpdateSystemNoticeStatusForm;
import io.linfeng.modules.app.utils.LocalUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.linfeng.modules.admin.dao.MessageDao;
import io.linfeng.modules.admin.entity.MessageEntity;
import io.linfeng.modules.admin.service.MessageService;
import org.springframework.transaction.annotation.Transactional;


@Service("messageService")
public class MessageServiceImpl extends ServiceImpl<MessageDao, MessageEntity> implements MessageService {

    @Autowired
    private AppUserService appUserService;
    @Autowired
    private PostService postService;
    @Autowired
    private LocalUser localUser;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<MessageEntity> queryWrapper=new QueryWrapper<>();
        //条件查询
        String key = (String) params.get("key");
        String type = (String) params.get("type");
        if (!WechatUtil.isEmpty(key)) {
            queryWrapper.lambda().like(MessageEntity::getTitle,key);
        }
        if (!WechatUtil.isEmpty(type)) {
            queryWrapper.lambda().eq(MessageEntity::getType,Integer.valueOf(type));
        }
        queryWrapper.lambda().orderByDesc(MessageEntity::getMId);
        IPage<MessageEntity> page = this.page(
                new Query<MessageEntity>().getPage(params),
                queryWrapper
        );
        List<MessageEntity> list = page.getRecords();

        List<AdminMessageListResponse> responseList=new ArrayList<>();
        list.forEach(item->{
            AdminMessageListResponse response=new AdminMessageListResponse();
            BeanUtils.copyProperties(item,response);
            if(item.getToUid()>0){
                AppUserEntity user = appUserService.getById(item.getToUid());
                response.setAvatar(user.getAvatar());
                response.setUsername(user.getUsername());
            }
            if(item.getFromUid()>0){
                AppUserEntity user = appUserService.getById(item.getFromUid());
                response.setFromAvatar(user.getAvatar());
                response.setFromUsername(user.getUsername());
            }
            responseList.add(response);
        });
        PageUtils pageUtils=new PageUtils(page);
        pageUtils.setList(responseList);
        return pageUtils;
    }

    @Override
    public MessageNumberResponse getMessageNumber() {
        AppUserEntity user = localUser.getUser();
        if(ObjectUtil.isNull(user)){
            MessageNumberResponse response=new MessageNumberResponse();
            response.setThumbCount(0);
            response.setComment(0);
            response.setAllCount(0);
            response.setFollow(0);
            response.setChatMsgList(new ArrayList<>());
            response.setArticleMsgList(new ArrayList<>());
            return response;
        }
        MessageNumberResponse response=new MessageNumberResponse();
        Integer allCount = this.lambdaQuery()
                .eq(MessageEntity::getStatus, Constant.NOT_READ)
                .eq(MessageEntity::getToUid, user.getUid())
                .count()
                .intValue();
        Integer follow = this.lambdaQuery()
                .eq(MessageEntity::getStatus, Constant.NOT_READ)
                .eq(MessageEntity::getToUid, user.getUid())
                .eq(MessageEntity::getType, Constant.WATCH)
                .count()
                .intValue();
        Integer comment = this.lambdaQuery()
                .eq(MessageEntity::getStatus, Constant.NOT_READ)
                .eq(MessageEntity::getToUid, user.getUid())
                .eq(MessageEntity::getType, Constant.COMMENT)
                .count()
                .intValue();
        Integer thumbCollect = this.lambdaQuery()
                .eq(MessageEntity::getStatus, Constant.NOT_READ)
                .eq(MessageEntity::getToUid, user.getUid())
                .eq(MessageEntity::getType, Constant.STAR)
                .count()
                .intValue();
        //系统通知消息查询
        Integer systemUnreadCount = this.lambdaQuery().eq(MessageEntity::getType, Constant.SYSMSG)
                .eq(MessageEntity::getToUid, user.getUid())
                .eq(MessageEntity::getStatus, Constant.NOT_READ)
                .orderByDesc(MessageEntity::getMId)
                .count()
                .intValue();
        response.setAllCount(allCount);
        response.setFollow(follow);
        response.setComment(comment);
        response.setThumbCount(thumbCollect);
        response.setSystemUnreadCount(systemUnreadCount);
        response.setArticleMsgList(new ArrayList<>());
        response.setChatMsgList(new ArrayList<>());
        return response;
    }

    /**
     * 查询消息列表
     * 前端类型：1点赞收藏 2关注 3评论
     */
    @Override
    public AppPageUtils<MessageListResponse> queryMessageList(Integer type, Integer currPage, AppUserEntity user) {
        LambdaQueryWrapper<MessageEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MessageEntity::getToUid,user.getUid());
        if(type==1){
            queryWrapper.eq(MessageEntity::getType,Constant.STAR);
        }else if(type==2){
            queryWrapper.eq(MessageEntity::getType,Constant.WATCH);
        }else if(type==3){
            queryWrapper.eq(MessageEntity::getType,Constant.COMMENT);
        }
        queryWrapper.orderByDesc(MessageEntity::getMId);
        Page<MessageEntity> page = new Page<>(currPage, 10);
        Page<MessageEntity> pageInfo = this.page(page, queryWrapper);
        List<MessageEntity> messageList = pageInfo.getRecords();
        
        // 批量获取用户信息
        List<Integer> uidList = messageList.stream()
                .map(MessageEntity::getFromUid)
                .filter(uid -> uid != null && uid > 0)
                .distinct()
                .collect(Collectors.toList());
        Map<Integer, AppUserEntity> userMap = new HashMap<>();
        if (!uidList.isEmpty()) {
            List<AppUserEntity> userList = appUserService.getBatchUser(uidList);
            userMap = userList.stream().collect(Collectors.toMap(AppUserEntity::getUid, Function.identity()));
        }
        
        List<MessageListResponse> responseList = new ArrayList<>();
        for (MessageEntity message : messageList) {
            MessageListResponse response = new MessageListResponse();
            BeanUtils.copyProperties(message, response);
            // 设置用户信息
            AppUserEntity userEntity = userMap.get(message.getFromUid());
            if (userEntity != null) {
                AppUserShortInfoResponse shortInfoResponse = new AppUserShortInfoResponse();
                BeanUtils.copyProperties(userEntity, shortInfoResponse);
                response.setUserInfo(shortInfoResponse);
            }
            // 非关注类型的消息需要设置帖子内容
            if (!message.getType().equals(Constant.WATCH)) {
                response.setPostContent(postService.getById(message.getPostId()).getContent());
            }
            responseList.add(response);
        }
        return new AppPageUtils<>(pageInfo, responseList);
    }

    /**
     * 消息异步发送
     * @param fromUid 发送者uid
     * @param toUid  接收者uid
     * @param postId 帖子id
     * @param type 1为点赞，2为评论  3为收藏 4为关注  5为系统通知
     * @param content  发送内容
     * @param title 发送标题
     */
    public void sendMessage(Integer fromUid,Integer toUid,Integer postId,Integer type,String content,String title){
        if(fromUid.equals(toUid)){
            return;
        }
        MessageEntity message=new MessageEntity();
        message.setContent(content);
        message.setPostId(postId);
        message.setFromUid(fromUid);
        message.setTitle(title);
        message.setCreateTime(DateUtil.nowDateTime());
        message.setToUid(toUid);
        message.setType(type);
        this.save(message);
    }

    /**
     * 消息同步发送
     * @param fromUid 发送者uid
     * @param toUid  接收者uid
     * @param postId 帖子id
     * @param type 1为点赞，2为评论  3为收藏 4为关注  5为系统通知
     * @param content  发送内容
     * @param title 发送标题
     */
    @Transactional(rollbackFor = Exception.class)
    public void sendMessageNotAsync(Integer fromUid,Integer toUid,Integer postId,Integer type,String content,String title){
        if(fromUid.equals(toUid)){
            return;
        }
        MessageEntity message=new MessageEntity();
        message.setContent(content);
        message.setPostId(postId);
        message.setFromUid(fromUid);
        message.setTitle(title);
        message.setCreateTime(DateUtil.nowDateTime());
        message.setToUid(toUid);
        message.setType(type);
        this.save(message);
    }

    @Override
    public Boolean status(Integer type, Integer uid) {

        UpdateWrapper<MessageEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("status",Constant.HAS_READ);
        updateWrapper.eq("to_uid",uid);
        if(type==1){
            updateWrapper.eq("type",Constant.STAR);
        }else if(type==2){
            updateWrapper.eq("type",Constant.WATCH);
        }else if(type==3){
            updateWrapper.eq("type",Constant.COMMENT);
        }else if(type==5){
            updateWrapper.eq("type",Constant.SYSMSG);
        }
        return update(updateWrapper);
    }



    @Override
    public Boolean articleMsgState(UpdateSystemNoticeStatusForm request, Integer uid) {
        return this.lambdaUpdate()
                .set(MessageEntity::getStatus,Constant.HAS_READ)
                .eq(MessageEntity::getFromUid,0)
                .eq(MessageEntity::getMId,request.getId())
                .eq(MessageEntity::getToUid,uid)
                .eq(MessageEntity::getType,Constant.SYSMSG)
                .update();
    }

    /**
     * 修改用户点击的消息为已读状态
     * @param request
     * @param uid
     */
    @Override
    public void readMessage(MessageReadForm request, Integer uid) {
        this.lambdaUpdate()
                .set(MessageEntity::getStatus,Constant.HAS_READ)
                .eq(MessageEntity::getToUid,uid)
                .eq(MessageEntity::getPostId,request.getPostId())
                .eq(MessageEntity::getMId,request.getMid())
                .update();
    }

    /**
     * 修改关注的人的消息为已读状态
     * @param uid
     */
    @Override
    public void readAllWatchInfo(Integer uid) {
        List<MessageEntity> list = this.lambdaQuery()
                .eq(MessageEntity::getType, Constant.WATCH)
                .eq(MessageEntity::getToUid, uid)
                .eq(MessageEntity::getStatus, Constant.NOT_READ).list();
        list.forEach(l-> l.setStatus(Constant.HAS_READ));
        updateBatchById(list);

    }

    /**
     * 清除几个月前的消息数据
     * @param month
     */
    @Override
    public void deleteMessageByMonth(Integer month) {
        String s = DateUtils.addDateMonths(new Date(),-month);
        LambdaQueryWrapper<MessageEntity> wrapper=new LambdaQueryWrapper<>();
        wrapper.le(MessageEntity::getCreateTime,s);
        this.remove(wrapper);
    }

    /**
     * 删除不重要消息
     * 如果你有业务需求需要保留别的类型消息，就在这里更改
     * @param day day天前的消息
     */
    @Override
    public void deleteSomeMessageByDay(Integer day) {
        String s = DateUtils.addDateDays(new Date(), -day);
        LambdaQueryWrapper<MessageEntity> wrapper=new LambdaQueryWrapper<>();
        wrapper.le(MessageEntity::getCreateTime,s);
        this.remove(wrapper);
    }

    @Override
    public AppPageUtils<MessageEntity> getSystemList(Integer uid,Integer currPage) {

        LambdaQueryWrapper<MessageEntity> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(MessageEntity::getToUid,uid);
        queryWrapper.eq(MessageEntity::getType, Constant.SYSMSG);
        queryWrapper.orderByDesc(MessageEntity::getMId);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<MessageEntity> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(currPage, 10);//每次获取的数量
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<MessageEntity> pages=this.page(page,queryWrapper);
        return new AppPageUtils<>(pages);
    }

    @Override
    public void updateSystemStatus(Integer uid) {
        this.lambdaUpdate()
                .set(MessageEntity::getStatus, Constant.HAS_READ)
                .eq(MessageEntity::getToUid, uid)
                .eq(MessageEntity::getType, Constant.SYSMSG)
                .update();
    }

    @Override
    public void delSystemMsg(Integer uid) {
        LambdaQueryWrapper<MessageEntity> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(MessageEntity::getToUid,uid);
        queryWrapper.eq(MessageEntity::getType,Constant.SYSMSG);
        queryWrapper.eq(MessageEntity::getFromUid,0);
        this.remove(queryWrapper);
    }

    @Override
    public void saveMessage(AddMessageParam param) {
        AppUserEntity user;
        MessageEntity message=new MessageEntity();
        if(NumberUtil.isInteger(param.getToUid())){
            user = appUserService.getById(Integer.valueOf(param.getToUid()));
            if(user==null){
                throw new LinfengException("接收用户不存在");
            }
            message.setToUid(Integer.valueOf(param.getToUid()));
        }else{
            List<AppUserEntity> list = appUserService.lambdaQuery()
                    .eq(AppUserEntity::getUsername, param.getToUid())
                    .list();
            if(list.size()==0){
                throw new LinfengException("接收用户不存在");
            }
            if(list.size()>1){
                throw new LinfengException("接收用户名不唯一，请设置用户ID");
            }
            message.setToUid(list.get(0).getUid());
        }
        message.setStatus(Constant.NOT_READ);
        message.setType(Constant.SYSMSG);
        message.setPostId(0);
        message.setFromUid(0);
        message.setCreateTime(new Date());
        message.setContent(param.getContent());
        message.setTitle(param.getTitle());
        this.save(message);
    }

    @Override
    public List<AppSystemMessageResponse> systemInfoList(ChatListForm request, AppUserEntity user) {
        List<MessageEntity> toList = getSystemList(user.getUid(), request.getPage()).getData();
        return toList.stream()
                .sorted(Comparator.comparing(MessageEntity::getMId).reversed())
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AppPageUtils<AppSystemMessageResponse> systemInfoListByPc(ChatListForm request, AppUserEntity user) {
        AppPageUtils<MessageEntity> appPage = getSystemList(user.getUid(), request.getPage());
        List<AppSystemMessageResponse> collect = appPage.getData().stream()
                .sorted(Comparator.comparing(MessageEntity::getMId).reversed())
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return new AppPageUtils<>(collect, appPage.getLast_page(), appPage.getPer_page(), appPage.getCurrent_page());
    }

    /**
     * 将MessageEntity转换为AppSystemMessageResponse
     */
    private AppSystemMessageResponse convertToResponse(MessageEntity entity) {
        AppSystemMessageResponse response = new AppSystemMessageResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }
}