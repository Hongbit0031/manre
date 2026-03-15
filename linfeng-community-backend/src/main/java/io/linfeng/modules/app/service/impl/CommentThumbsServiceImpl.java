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
package io.linfeng.modules.app.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.linfeng.common.enums.CommentStatus;
import io.linfeng.common.exception.LinfengException;
import io.linfeng.common.utils.*;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.entity.CommentEntity;
import io.linfeng.modules.admin.service.CommentService;
import io.linfeng.modules.admin.service.MessageService;
import io.linfeng.modules.app.param.AddThumbsForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.linfeng.modules.app.dao.CommentThumbsDao;
import io.linfeng.modules.app.entity.CommentThumbsEntity;
import io.linfeng.modules.app.service.CommentThumbsService;


@Service("commentThumbsService")
public class CommentThumbsServiceImpl extends ServiceImpl<CommentThumbsDao, CommentThumbsEntity> implements CommentThumbsService {


    @Autowired
    @Lazy
    private MessageService messageService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private RedisUtils redisUtils;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CommentThumbsEntity> page = this.page(
                new Query<CommentThumbsEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public Integer getThumbsCount(Long id) {
        Integer count = baseMapper.selectCount(
                new LambdaQueryWrapper<CommentThumbsEntity>()
                        .eq(CommentThumbsEntity::getCId, id)).intValue();
        return count;
    }

    @Override
    public Boolean isThumbs(Integer uid, Long id) {
        CommentThumbsEntity one = baseMapper.selectOne(new LambdaQueryWrapper<CommentThumbsEntity>()
                .eq(CommentThumbsEntity::getCId, id)
                .eq(CommentThumbsEntity::getUid, uid));

        return one!=null;
    }

    @Override
    public void addThumbs(AddThumbsForm request, AppUserEntity user) {
        CommentThumbsEntity one = baseMapper.selectOne(new LambdaQueryWrapper<CommentThumbsEntity>()
                .eq(CommentThumbsEntity::getCId, request.getId())
                .eq(CommentThumbsEntity::getUid, user.getUid()));
        if(ObjectUtil.isNotNull(one)){
            throw new LinfengException("请勿重复点赞");
        }
        CommentEntity comment = commentService.getById(request.getId());
        if(ObjectUtil.isNull(comment)||comment.getStatus()!=CommentStatus.NORMAL.getValue()){
            throw new LinfengException("您点赞的评论已删除");
        }
        CommentThumbsEntity thumbs=new CommentThumbsEntity();
        thumbs.setCId(request.getId());
        thumbs.setCreateTime(DateUtil.nowDateTime());
        thumbs.setUid(user.getUid());
        this.save(thumbs);
        //清理评论列表缓存
        redisUtils.delete(ConfigConstant.COMMENT_KEY+comment.getPostId());
        String content = StrUtil.format(Constant.CONTENT_STAR,user.getUsername(),comment.getContent());
        messageService.sendMessage(user.getUid(),comment.getUid().intValue(),comment.getPostId().intValue(),Constant.STAR,content,Constant.TITLE_STAR);

    }

    @Override
    public void cancelThumbs(AddThumbsForm request, AppUserEntity user) {
        CommentEntity comment = commentService.getById(request.getId());
        baseMapper.delete(new LambdaQueryWrapper<CommentThumbsEntity>()
                .eq(CommentThumbsEntity::getCId, request.getId())
                .eq(CommentThumbsEntity::getUid, user.getUid()));
        //清理评论列表缓存
        redisUtils.delete(ConfigConstant.COMMENT_KEY+comment.getPostId());
    }

}