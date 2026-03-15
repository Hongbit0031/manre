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
package io.linfeng.modules.app.utils.redismq;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.linfeng.common.enums.CommentStatus;
import io.linfeng.common.utils.*;
import io.linfeng.modules.admin.entity.CommentEntity;
import io.linfeng.modules.admin.entity.PostEntity;
import io.linfeng.modules.admin.service.CommentService;
import io.linfeng.modules.admin.service.MessageService;
import io.linfeng.modules.admin.service.PostService;
import io.linfeng.modules.admin.service.SensitiveService;
import io.linfeng.modules.sys.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 消息订阅者（集群版）
 * 使用 Redis 分布式锁防止重复消费
 * @author linfeng
 * @date 2026/2/27 09:08
 */
@Slf4j
@Component
public class MessageSubscriber implements MessageListener {

    private static final String MQ_LOCK_PREFIX = "linfeng:mq:lock:";
    private static final long LOCK_EXPIRE_MS = 30 * 1000L;

    @Autowired
    private SensitiveService sensitiveService;
    @Autowired
    private PostService postService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private SysConfigService configService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public void onMessage(Message message, byte[] pattern) {
        String receivedMessage = message.toString();
        String[] split = receivedMessage.split(":");
        if (split.length < 2) {
            return;
        }

        String type = split[0];
        String targetId = split[1];

        // PV 操作不需要加锁（幂等操作，数据库层面原子执行）
        if (Constant.POST_READ_NUM.equals(type)) {
            if (NumberUtil.isInteger(targetId)) {
                handPostPv(Integer.valueOf(targetId));
            }
            return;
        }

        // 其他操作需要加锁（审核类操作，重复执行会有副作用）
        String lockKey = MQ_LOCK_PREFIX + type + ":" + targetId;

        // 尝试获取分布式锁（原子操作）
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", LOCK_EXPIRE_MS, TimeUnit.MILLISECONDS);
        if (!Boolean.TRUE.equals(acquired)) {
            // 获取锁失败，说明消息正在被其他实例处理，跳过
            log.debug("消息正在处理中，跳过: {}", receivedMessage);
            return;
        }

        try {
            log.info("Received message:{}", receivedMessage);
            switch (type) {
                case Constant.POST_CONTENT_CHECK:
                    if (NumberUtil.isInteger(targetId)) {
                        handlePostCheck(Integer.valueOf(targetId));
                    }
                    break;
                case Constant.COMMENT_CONTENT_CHECK:
                    if (NumberUtil.isInteger(targetId)) {
                        handCommentImgCheck(Integer.valueOf(targetId));
                    }
                    break;
            }
        } finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }

    /**
     * 审核帖子图片视频内容
     * @param postId
     */
    private void handlePostCheck(Integer postId){
        PostEntity post = postService.getById(postId);
        if(post!=null && post.getStatus().equals(Constant.POST_REVIEWED)){
            List<String> list = JsonUtils.JsonToList(post.getMedia());
            if(post.getType().equals(Constant.POST_TYPE_VIDEO)){
                boolean result = sensitiveService.checkVideo(list.get(0));
                if(!result){
                    //审核未通过，下架帖子
                    post.setStatus(Constant.POST_BANNER);
                    postService.updateById(post);
                    String content = StrUtil.format(Constant.AI_DOWN, post.getTitle());
                    messageService.sendMessageNotAsync(0, post.getUid(), post.getId(), Constant.SYSMSG, content, Constant.TITLE_VIOLATION);
                }else{
                    post.setStatus(Constant.POST_NORMAL);
                    postService.updateById(post);
                }
            }else if(post.getType().equals(Constant.POST_TYPE_TEXT)){
                boolean tmp=true;
                for (int i = 0; i < list.size(); i++) {
                    boolean result = sensitiveService.checkImg(list.get(i));
                    if(!result){
                        //审核未通过，下架帖子
                        post.setStatus(Constant.POST_BANNER);
                        postService.updateById(post);
                        tmp=false;
                        String content = StrUtil.format(Constant.AI_DOWN, post.getTitle());
                        messageService.sendMessageNotAsync(0, post.getUid(), post.getId(), Constant.SYSMSG, content, Constant.TITLE_VIOLATION);
                        break;
                    }
                }
                if(tmp){
                    post.setStatus(Constant.POST_NORMAL);
                    postService.updateById(post);
                }
            }else if(post.getType().equals(Constant.POST_TYPE_ARTICLE)){
                List<String> imageUrls = HtmlImageExtractor.extractImageUrls(post.getContent());
                boolean tmp2=true;
                for (int i = 0; i < imageUrls.size(); i++) {
                    boolean result = sensitiveService.checkImg(imageUrls.get(i));
                    if(!result){
                        //审核未通过，下架帖子
                        post.setStatus(Constant.POST_BANNER);
                        postService.updateById(post);
                        tmp2=false;
                        String content = StrUtil.format(Constant.AI_DOWN, post.getTitle());
                        messageService.sendMessageNotAsync(0, post.getUid(), post.getId(), Constant.SYSMSG, content, Constant.TITLE_VIOLATION);
                        break;
                    }
                }
                if(tmp2){
                    post.setStatus(Constant.POST_NORMAL);
                    postService.updateById(post);
                }
            }
        }
    }

    /**
     * 审核评论的图片
     * @param commentId
     */
    private void handCommentImgCheck(Integer commentId){
        CommentEntity comment = commentService.getById(commentId);
        if(comment!=null && !WechatUtil.isEmpty(comment.getImg())){
            boolean result = sensitiveService.checkImg(comment.getImg());
            if(!result){
                comment.setStatus(CommentStatus.OFF.getValue());
                commentService.updateById(comment);
                String content = StrUtil.format(Constant.AI_COMMENT_DOWN, comment.getContent());
                messageService.sendMessageNotAsync(0, comment.getUid().intValue(), comment.getPostId().intValue(), Constant.SYSMSG, content, Constant.TITLE_VIOLATION);
                //清理评论列表缓存
                redisUtils.delete(ConfigConstant.COMMENT_KEY+comment.getPostId());
                //如果评论发布策略为直接过审，那么需要修改评论数量缓存
                if(configService.getValue(Constant.COMMENT_CHECK).equals("0")){
                    List<CommentEntity> child = commentService.lambdaQuery().eq(CommentEntity::getPid, comment.getId()).list();
                    redisUtils.hashChange(RedisKeys.getPostKey(comment.getPostId().intValue()),ConfigConstant.POST_COMMENT_NUM,-(child.size()+1));
                }
            }
        }
    }

    /**
     * 帖子浏览量添加
     * @param postId
     */
    private void handPostPv(Integer postId){
        UpdateWrapper<PostEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id",postId);
        wrapper.setSql("`read_count` = `read_count` + 1");
        postService.update(wrapper);
    }

}
