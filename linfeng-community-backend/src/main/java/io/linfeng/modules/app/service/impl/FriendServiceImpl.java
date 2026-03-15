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

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.linfeng.common.exception.LinfengException;
import io.linfeng.common.utils.DateUtil;
import io.linfeng.common.utils.SnowFlakeUtil;
import io.linfeng.common.vo.app.AppFriendResponse;
import io.linfeng.modules.app.entity.NoticeEntity;
import io.linfeng.modules.app.param.ClearChatMessageUnreadForm;
import io.linfeng.modules.app.service.ChatMessageService;
import io.linfeng.modules.app.service.NoticeService;
import io.linfeng.modules.app.websocket.constant.MessageConstant;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.Query;

import io.linfeng.modules.app.dao.FriendDao;
import io.linfeng.modules.app.entity.FriendEntity;
import io.linfeng.modules.app.service.FriendService;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;


@Service("friendService")
public class FriendServiceImpl extends ServiceImpl<FriendDao, FriendEntity> implements FriendService {

    @Resource
    private FriendDao friendDao;

    @Resource
    private ChatMessageService chatMessageService;

    @Resource
    private NoticeService noticeService;

    @Resource
    private FriendService friendService;



    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<FriendEntity> page = this.page(
                new Query<FriendEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<AppFriendResponse> getFriendList(Integer uid) {
        return friendDao.getFriendList(uid);
    }

    @Override
    public void clearUnread(ClearChatMessageUnreadForm param) {
        this.lambdaUpdate().set(FriendEntity::getUnread,0)
                .eq(FriendEntity::getFriendId,param.getFriendId())
                .eq(FriendEntity::getMyId,param.getMyId())
                .update();
    }

    @Override
    public Boolean checkIsFriend(Integer myId, Integer friendId) {
        Long count = this.lambdaQuery()
                .eq(FriendEntity::getFriendId, friendId)
                .eq(FriendEntity::getMyId, myId)
                .count();
        return count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFriend(Integer friendId, Integer myId) {
        LambdaQueryWrapper<FriendEntity> wrapper1=new LambdaQueryWrapper<>();
        wrapper1.eq(FriendEntity::getFriendId,friendId);
        wrapper1.eq(FriendEntity::getMyId,myId);
        boolean remove1 = this.remove(wrapper1);

        LambdaQueryWrapper<FriendEntity> wrapper2=new LambdaQueryWrapper<>();
        wrapper2.eq(FriendEntity::getFriendId,myId);
        wrapper2.eq(FriendEntity::getMyId,friendId);
        boolean remove2 = this.remove(wrapper2);
        if(!remove1 || !remove2){
            throw new LinfengException("操作失败");
        }
        //清理两人之间的聊天记录
        chatMessageService.deleteChatMessage(friendId,myId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyFriend(Object dataInfo) {
        JSONObject data = JSONUtil.parseObj(dataInfo);
        NoticeEntity notice = NoticeEntity.builder()
                .senderId(data.getLong("senderId"))
                .receiverId(data.getLong("receiverId"))
                .type(MessageConstant.PERSON_APPLY)
                .information(data.toString())
                .isRead(false)
                .createTime(DateUtil.nowDateTime())
                .updateTime(DateUtil.nowDateTime())
                .build();
        noticeService.save(notice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void agreePersonApply(Integer id) {
        NoticeEntity noticeInfo = noticeService.getById(id);
        JSONObject jsonObject = JSONUtil.parseObj(noticeInfo.getInformation());
        Boolean isFriend = friendService.checkIsFriend(noticeInfo.getReceiverId().intValue(), noticeInfo.getSenderId().intValue());
        if (isFriend) {
            noticeInfo.setIsRead(true);
            noticeService.saveOrUpdate(noticeInfo);
            return;
        }
        Long sessionId = SnowFlakeUtil.getSnowFlakeId();
        FriendEntity friend1 = FriendEntity.builder()
                .myId(noticeInfo.getSenderId())
                .friendId(noticeInfo.getReceiverId())
                .notation(jsonObject.getStr("notation"))
                .sessionId(sessionId)
                .lastMessage(MessageConstant.DEFAULT_LAST_MESSAGE)
                .unread(MessageConstant.NOT_READ)
                .isHidden(false)
                .createTime(DateUtil.nowDateTime())
                .updateTime(DateUtil.nowDateTime())
                .build();
        FriendEntity friend2 = FriendEntity.builder()
                .myId(noticeInfo.getReceiverId())
                .friendId(noticeInfo.getSenderId())
                .notation(jsonObject.getStr("senderName"))
                .sessionId(sessionId)
                .lastMessage(MessageConstant.DEFAULT_LAST_MESSAGE)
                .unread(MessageConstant.NOT_READ)
                .isHidden(false)
                .createTime(DateUtil.nowDateTime())
                .updateTime(DateUtil.nowDateTime())
                .build();
        friendService.save(friend1);
        friendService.save(friend2);
        noticeInfo.setIsRead(true);
        noticeService.saveOrUpdate(noticeInfo);
    }

}