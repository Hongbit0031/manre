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
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.linfeng.common.enums.CommentStatus;
import io.linfeng.common.exception.LinfengException;
import io.linfeng.common.utils.*;
import io.linfeng.common.vo.admin.AdminCommentListResponse;
import io.linfeng.common.vo.app.AppChildrenCommentResponse;
import io.linfeng.common.vo.app.AppCommentResponse;
import io.linfeng.common.vo.app.AppUserShortInfoResponse;
import io.linfeng.common.vo.app.CommentCountResponse;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.service.AppUserService;
import io.linfeng.modules.app.param.DelCommentForm;
import io.linfeng.modules.app.service.CommentThumbsService;
import io.linfeng.modules.app.utils.LocalUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.linfeng.modules.admin.dao.CommentDao;
import io.linfeng.modules.admin.entity.CommentEntity;
import io.linfeng.modules.admin.service.CommentService;
import org.springframework.transaction.annotation.Transactional;


@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentDao, CommentEntity> implements CommentService {

    @Autowired
    private AppUserService appUserService;
    @Autowired
    private CommentThumbsService commentThumbsService;
    @Autowired
    private LocalUser localUser;
    @Autowired
    private CommentDao commentDao;
    @Autowired
    private RedisUtils redisUtils;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<CommentEntity> queryWrapper=new QueryWrapper<>();
        String key = (String)params.get("key");
        String status = (String)params.get("status");
        String uid = (String)params.get("uid");
        String pid = (String)params.get("pid");
        if(!WechatUtil.isEmpty(key)){
            if(NumberUtil.isInteger(key)){
                queryWrapper.lambda().eq(CommentEntity::getPostId, key);
            }else{
                queryWrapper.lambda().like(CommentEntity::getContent, key);
            }
        }
        if(!WechatUtil.isEmpty(status)){
            queryWrapper.lambda().eq(CommentEntity::getStatus, Integer.parseInt(status));
        }
        if(!WechatUtil.isEmpty(uid)){
            if(NumberUtil.isInteger(uid)){
                queryWrapper.lambda().eq(CommentEntity::getUid, Integer.parseInt(uid));
            }
        }
        if(!WechatUtil.isEmpty(pid)){
            if(NumberUtil.isInteger(pid)){
                queryWrapper.lambda().eq(CommentEntity::getPid, Integer.parseInt(pid));
            }
        }

        queryWrapper.lambda().orderByDesc(CommentEntity::getId);
        IPage<CommentEntity> page = this.page(
                new Query<CommentEntity>().getPage(params),
                queryWrapper
        );
        List<CommentEntity> list = page.getRecords();
        List<AdminCommentListResponse> responseList=new ArrayList<>();
        list.forEach(item->{
            AdminCommentListResponse response=new AdminCommentListResponse();
            BeanUtils.copyProperties(item,response);
            if(item.getToUid()!=null && item.getToUid()>0){
                AppUserEntity toUser = appUserService.getById(item.getToUid());
                response.setToUserAvatar(toUser.getAvatar());
                response.setToUsername(toUser.getUsername());
            }
            AppUserEntity user = appUserService.getById(item.getUid());
            if(item.getPid()!=null&&item.getPid()>0){
                CommentEntity comment = this.getById(item.getPid());
                if(comment!=null){
                    response.setParentComment(comment.getContent());
                    if(comment.getImg()!=null){
                        response.setParentImg(comment.getImg());
                    }
                }else{
                    response.setParentComment("/");
                }
            }else{
                response.setParentComment("/");
            }
            response.setAvatar(user.getAvatar());
            response.setUsername(user.getUsername());
            responseList.add(response);
        });
        PageUtils pageUtils=new PageUtils(page);
        pageUtils.setList(responseList);
        return pageUtils;
    }

    @Override
    public Integer getCountByPostId(Integer id) {
        Object hashValue = redisUtils.getHashValue(RedisKeys.getPostKey(id), ConfigConstant.POST_COMMENT_NUM);
        if(hashValue==null){
            Integer num = baseMapper.selectCount(
                    new LambdaQueryWrapper<CommentEntity>()
                            .eq(CommentEntity::getStatus, CommentStatus.NORMAL.getValue())
                            .eq(CommentEntity::getPostId, id)).intValue();
            redisUtils.hashAdd(RedisKeys.getPostKey(id), ConfigConstant.POST_COMMENT_NUM,num.toString());
            return num;
        }else{
            return Integer.valueOf(hashValue.toString());
        }
    }

    @Override
    public List<CommentCountResponse> getAllCountByPostId(List<Integer> list) {
        List<Integer> resultList=new ArrayList<>();
        List<CommentCountResponse> cacheCollectList =new ArrayList<>();
        list.forEach(postId->{
            Object hashValue = redisUtils.getHashValue(RedisKeys.getPostKey(postId), ConfigConstant.POST_COMMENT_NUM);
            if(hashValue==null){
                resultList.add(postId);
            }else{
                CommentCountResponse response=new CommentCountResponse();
                response.setNumber(Integer.valueOf(hashValue.toString()));
                response.setPostId(postId);
                cacheCollectList.add(response);
            }
        });
        //如果redis缓存中没有 去数据库批量查询
        if(!resultList.isEmpty()){
            List<CommentCountResponse> batchCollectCount = commentDao.getAllCountByPostId(resultList);
            //数据库查询后也存入redis缓存
            batchCollectCount.forEach(item->{
                redisUtils.hashAdd(RedisKeys.getPostKey(item.getPostId()), ConfigConstant.POST_COMMENT_NUM,item.getNumber().toString());
            });
            cacheCollectList.addAll(batchCollectCount);
        }
        return cacheCollectList;
    }

    @Override
    public AppPageUtils<AppCommentResponse> queryCommentPage(Integer postId, Integer page) {
        Page<CommentEntity> commentPage = new Page<>(page,10);
        QueryWrapper<CommentEntity> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(CommentEntity::getPostId,postId);
        queryWrapper.lambda().eq(CommentEntity::getStatus,CommentStatus.NORMAL.getValue());
        queryWrapper.lambda().orderByDesc(CommentEntity::getId);
        Page<CommentEntity> pages = baseMapper.selectPage(commentPage,queryWrapper);
        List<CommentEntity> data = pages.getRecords();
        List<AppCommentResponse> responseList=new ArrayList<>();
        AppUserEntity user = localUser.getUser();
        data.forEach(l -> {
            if (l.getPid() == 0) {  //过滤子评论
                AppCommentResponse response = new AppCommentResponse();
                BeanUtils.copyProperties(l, response);
                response.setImg(l.getImg() == null ? "" : l.getImg());
                AppUserEntity appUserEntity = appUserService.getById(response.getUid());
                AppUserShortInfoResponse userInfoVo=new AppUserShortInfoResponse();
                BeanUtils.copyProperties(appUserEntity,userInfoVo);
                response.setUserInfo(userInfoVo);
                response.setThumbs(commentThumbsService.getThumbsCount(l.getId()));
                response.setShowBtn(true);
                if(user==null){
                    response.setIsThumbs(false);
                }else{
                    Boolean isThumb = commentThumbsService.isThumbs(user.getUid(), l.getId());
                    if(isThumb && page==1){
                        //点赞的话不能缓存
                        redisUtils.set(Constant.COMMENT_CACHE+postId,"0");
                    }
                    response.setIsThumbs(isThumb);
                }

                //处理子评论
                List<CommentEntity> childrenCommentList = this.getByPid(l.getId());
                response.setChildrenCount(childrenCommentList.size());
                if (childrenCommentList.isEmpty()) {
                    response.setChildren(new ArrayList<>());
                } else {
                    List<AppChildrenCommentResponse> childrenList = new ArrayList<>();
                    //最多只读取2条子评论 剩下的用户请求了再单独读取
                    Integer commentNum = Math.min(2, childrenCommentList.size());
                    for (int i = 0; i < commentNum ; i++) {
                        CommentEntity childrenComment = childrenCommentList.get(i);
                        AppChildrenCommentResponse childrenResponse = new AppChildrenCommentResponse();
                        BeanUtils.copyProperties(childrenComment, childrenResponse);
                        AppUserEntity commentUser = appUserService.getById(childrenComment.getUid());
                        AppUserShortInfoResponse userVo=new AppUserShortInfoResponse();
                        BeanUtils.copyProperties(commentUser,userVo);
                        childrenResponse.setUserInfo(userVo);
                        AppUserEntity toUserInfo = appUserService.getById(childrenComment.getToUid());
                        AppUserShortInfoResponse toUserInfoVo=new AppUserShortInfoResponse();
                        BeanUtils.copyProperties(toUserInfo,toUserInfoVo);
                        childrenResponse.setToUser(toUserInfoVo);
                        childrenResponse.setThumbs(commentThumbsService.getThumbsCount(childrenComment.getId()));
                        if(user==null){
                            childrenResponse.setIsThumbs(false);
                        }else{
                            childrenResponse.setIsThumbs(commentThumbsService.isThumbs(user.getUid(), childrenComment.getId()));
                        }
                        childrenList.add(childrenResponse);
                    }
                    response.setChildren(childrenList);
                }
                responseList.add(response);
            }
        });
        return new AppPageUtils<>(pages, responseList);
    }

    @Override
    public List<CommentEntity> getByPid(Long pid) {
        return baseMapper.selectList(
                new LambdaQueryWrapper<CommentEntity>()
                        .eq(CommentEntity::getStatus,CommentStatus.NORMAL.getValue())
                        .eq(CommentEntity::getPid, pid));
    }

    /**
     * 用户端-长按评论-删除自己评论
     * @param request
     * @param user
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void del(DelCommentForm request, AppUserEntity user) {
        CommentEntity comment = this.getById(request.getId().longValue());
        if(comment == null){
            throw new LinfengException("请先长按评论再删除");
        }
        if (comment.getUid().intValue() != user.getUid()) {
            throw new LinfengException("不能删除别人的评论");
        }
        //评论数量缓存修改
        List<CommentEntity> list = this.lambdaQuery().eq(CommentEntity::getPid, comment.getId()).list();
        redisUtils.hashChange(RedisKeys.getPostKey(comment.getPostId().intValue()), ConfigConstant.POST_COMMENT_NUM,-(1+list.size()));
        //子评论更改展示状态为屏蔽
        this.lambdaUpdate()
                .set(CommentEntity::getStatus, CommentStatus.OFF.getValue())
                .eq(CommentEntity::getPid, comment.getId())
                .update();
        this.removeById(request.getId());

        //清理评论列表缓存
        redisUtils.delete(ConfigConstant.COMMENT_KEY+comment.getPostId());
    }

    /**
     * 管理端批量删除评论
     * @param list
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByAdmin(List<Long> list) {
        list.forEach(id->{
            CommentEntity comment = this.getById(id);
            if(comment.getStatus().equals(CommentStatus.NORMAL.getValue())){
                //如果删除的是原本正常显示的评论，那需要清理评论数量缓存
                List<CommentEntity> lists = this.lambdaQuery().eq(CommentEntity::getPid, id).list();
                redisUtils.hashChange(RedisKeys.getPostKey(comment.getPostId().intValue()), ConfigConstant.POST_COMMENT_NUM,-(lists.size()+1));
            }
            this.removeById(id);
            //子评论更改展示状态为屏蔽
            this.lambdaUpdate()
                    .set(CommentEntity::getStatus, CommentStatus.OFF.getValue())
                    .eq(CommentEntity::getPid, id)
                    .update();
            redisUtils.delete(ConfigConstant.COMMENT_KEY+comment.getPostId());

        });
    }


    /**
     * 从缓存获取评论列表第一页数据
     *
     * @return
     */
    @Override
    public AppPageUtils<AppCommentResponse> getCommentListInCache(Integer postId){
        AppUserEntity user = localUser.getUser();
        if(user == null){
            //未登录请求可以走缓存
            String result = redisUtils.get(ConfigConstant.COMMENT_KEY+postId);
            if(!WechatUtil.isEmpty(result)){
                return JSON.parseObject(result, new TypeReference<AppPageUtils<AppCommentResponse>>(){});
            }
        }
        AppPageUtils<AppCommentResponse> appPage = this.queryCommentPage(postId, 1);
        //查看是否可以进行缓存
        String tag = redisUtils.get(Constant.COMMENT_CACHE+postId);
        if(tag!=null && tag.equals("0")){
            redisUtils.delete(Constant.COMMENT_CACHE+postId);
        }else{
            redisUtils.set(ConfigConstant.COMMENT_KEY+postId, JSON.toJSONString(appPage, SerializerFeature.WriteDateUseDateFormat), 60*10);
        }
        return appPage;
    }

    @Override
    public void updateCommentById(CommentEntity comment) {
        CommentEntity commentEntity = this.getById(comment.getId());
        if(!comment.getStatus().equals(commentEntity.getStatus())){
            List<CommentEntity> lists = this.lambdaQuery().eq(CommentEntity::getPid, comment.getId()).list();
            //如果是修改待审核评论，修改为上架才做缓存更改
            if(commentEntity.getStatus()==CommentStatus.AUDIT.getValue()){
                if(comment.getStatus()==CommentStatus.NORMAL.getValue()){
                    redisUtils.hashChange(RedisKeys.getPostKey(comment.getPostId().intValue()), ConfigConstant.POST_COMMENT_NUM,lists.size()+1);
                }
            }else{
                //如果是修改审核过的评论，正常处理
                if(comment.getStatus()==CommentStatus.OFF.getValue()){
                    redisUtils.hashChange(RedisKeys.getPostKey(comment.getPostId().intValue()), ConfigConstant.POST_COMMENT_NUM,-(lists.size()+1));
                }else{
                    redisUtils.hashChange(RedisKeys.getPostKey(comment.getPostId().intValue()), ConfigConstant.POST_COMMENT_NUM,lists.size()+1);
                }
            }


        }
        redisUtils.delete(ConfigConstant.COMMENT_KEY+comment.getPostId());
        this.updateById(comment);
    }

    @Override
    public List<AppChildrenCommentResponse> remainComment(Long id) {
        List<CommentEntity> child = this.getByPid(id);
        if (child.size() >= 2) {
            child.subList(0, 2).clear();
        }
        List<AppChildrenCommentResponse> responseList=new ArrayList<>();
        AppUserEntity user = localUser.getUser();
        child.forEach(l -> {
            AppChildrenCommentResponse childrenResponse = new AppChildrenCommentResponse();
            BeanUtils.copyProperties(l, childrenResponse);
            AppUserEntity commentUser = appUserService.getById(l.getUid());
            AppUserShortInfoResponse userVo=new AppUserShortInfoResponse();
            BeanUtils.copyProperties(commentUser,userVo);
            childrenResponse.setUserInfo(userVo);
            AppUserEntity toUserInfo = appUserService.getById(l.getToUid());
            AppUserShortInfoResponse toUserInfoVo=new AppUserShortInfoResponse();
            BeanUtils.copyProperties(toUserInfo,toUserInfoVo);
            childrenResponse.setToUser(toUserInfoVo);
            childrenResponse.setThumbs(commentThumbsService.getThumbsCount(l.getId()));
            if(user==null){
                childrenResponse.setIsThumbs(false);
            }else{
                childrenResponse.setIsThumbs(commentThumbsService.isThumbs(user.getUid(), l.getId()));
            }
            responseList.add(childrenResponse);
        });
        return responseList;
    }
}