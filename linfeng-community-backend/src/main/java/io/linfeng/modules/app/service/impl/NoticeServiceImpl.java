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
import io.linfeng.modules.app.param.ReadNoticeForm;
import io.linfeng.modules.app.param.RejectNoticeForm;
import io.linfeng.modules.app.websocket.constant.MessageConstant;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.Query;

import io.linfeng.modules.app.dao.NoticeDao;
import io.linfeng.modules.app.entity.NoticeEntity;
import io.linfeng.modules.app.service.NoticeService;
import org.springframework.transaction.annotation.Transactional;


@Service("noticeService")
public class NoticeServiceImpl extends ServiceImpl<NoticeDao, NoticeEntity> implements NoticeService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<NoticeEntity> page = this.page(
                new Query<NoticeEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public Map<String,Object> getNoticeList(Integer uid) {
        List<NoticeEntity> list = this.lambdaQuery()
                .eq(NoticeEntity::getReceiverId, uid)
                .orderByDesc(NoticeEntity::getCreateTime)
                .list();
        Map<String,Object> map=new HashMap<>(2);
        map.put("noticeList",list);
        Integer count = this.lambdaQuery()
                .eq(NoticeEntity::getReceiverId, uid)
                .eq(NoticeEntity::getIsRead, false)
                .orderByDesc(NoticeEntity::getCreateTime)
                .count()
                .intValue();
        map.put("count",count);
        return map;
    }

    @Override
    public void readById(ReadNoticeForm param) {
        this.lambdaUpdate()
                .set(NoticeEntity::getIsRead,MessageConstant.HAS_READ)
                .eq(NoticeEntity::getId,param.getId())
                .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(RejectNoticeForm param) {
        NoticeEntity notice = this.getById(param.getId());
        boolean update = this.lambdaUpdate()
                .set(NoticeEntity::getIsRead, MessageConstant.HAS_READ)
                .eq(NoticeEntity::getId, param.getId())
                .update();
        if(update){
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("fatherNoticeId", notice.getId());
            jsonObject.set("senderName", param.getSenderName());
            jsonObject.set("senderAvatar", param.getSenderAvatar());
            
            NoticeEntity reject = NoticeEntity.builder()
                    .senderId(notice.getReceiverId())
                    .receiverId(notice.getSenderId())
                    .type(MessageConstant.REJECT)
                    .information(jsonObject.toString())
                    .isRead(false)
                    .build();
            this.save(reject);
        }
    }

}