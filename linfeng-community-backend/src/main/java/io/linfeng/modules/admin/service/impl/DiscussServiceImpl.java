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

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.linfeng.common.exception.LinfengException;
import io.linfeng.common.vo.admin.AdminDiscussListResponse;
import io.linfeng.common.vo.app.AppDiscussListResponse;
import io.linfeng.common.vo.app.AppUserShortInfoResponse;
import io.linfeng.common.vo.app.DiscussDetailResponse;
import io.linfeng.common.utils.*;
import io.linfeng.modules.admin.entity.*;
import io.linfeng.modules.admin.service.*;
import io.linfeng.modules.app.entity.UserTopicEntity;
import io.linfeng.modules.app.param.DiscussAddForm;
import io.linfeng.modules.app.param.DiscussDeleteForm;
import io.linfeng.modules.app.param.DiscussListForm;
import io.linfeng.modules.app.service.UserTopicService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.linfeng.modules.admin.dao.DiscussDao;


@Service("discussService")
public class DiscussServiceImpl extends ServiceImpl<DiscussDao, DiscussEntity> implements DiscussService {


    @Autowired
    private AppUserService appUserService;

    @Autowired
    private PostService postService;

    @Autowired
    private SensitiveService sensitiveService;

    @Autowired
    private UserTopicService userTopicService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private RedisUtils redisUtils;




    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<DiscussEntity> queryWrapper=new QueryWrapper<>();
        String key = (String)params.get("key");
        String uid = (String)params.get("uid");
        if(!WechatUtil.isEmpty(uid)){
            if(NumberUtil.isInteger(uid)){
                queryWrapper.lambda().eq(DiscussEntity::getUid, Integer.parseInt(uid));
            }
        }
        if(!WechatUtil.isEmpty(key)){
            if(NumberUtil.isInteger(key)){
                queryWrapper.lambda().eq(DiscussEntity::getTopicId, Integer.parseInt(key));
            }
        }
        queryWrapper.lambda().orderByDesc(DiscussEntity::getId);
        IPage<DiscussEntity> page = this.page(
                new Query<DiscussEntity>().getPage(params),
                queryWrapper
        );
        List<DiscussEntity> list = page.getRecords();

        List<AdminDiscussListResponse> responseList=new ArrayList<>();
        list.forEach(item->{
            AdminDiscussListResponse response=new AdminDiscussListResponse();
            BeanUtils.copyProperties(item,response);
            AppUserEntity user = appUserService.getById(item.getUid());
            TopicEntity topic = topicService.getById(item.getTopicId());
            response.setAvatar(user.getAvatar());
            response.setUsername(user.getUsername());
            response.setCoverImage(topic.getCoverImage());
            response.setTopicName(topic.getTopicName());
            responseList.add(response);
        });
        PageUtils pageUtils=new PageUtils(page);
        pageUtils.setList(responseList);
        return pageUtils;
    }

    @Override
    public List<DiscussEntity> getListByTopicId(Integer topicId) {
        return this.lambdaQuery()
                .eq(DiscussEntity::getTopicId, topicId)
                .list();

    }

    @Override
    public AppPageUtils<AppDiscussListResponse> getDiscussList(DiscussListForm request) {
        QueryWrapper<DiscussEntity> wrapper = new QueryWrapper<>();
        if (request.getTopicId() != null) {
            wrapper.lambda().eq(DiscussEntity::getTopicId, request.getTopicId());
        }
        Page<DiscussEntity> page = new Page<>(request.getPage(), 20);
        Page<DiscussEntity> pages = baseMapper.selectPage(page, wrapper);
        List<DiscussEntity> records = pages.getRecords();
        if(records.isEmpty()){
            return new AppPageUtils<>(Collections.emptyList(), 0, 20, request.getPage());
        }
        List<AppDiscussListResponse> responseList = new ArrayList<>();
        List<Integer> uidList = records.stream().map(DiscussEntity::getUid).collect(Collectors.toList());
        List<AppUserEntity> batchUser = appUserService.getBatchUser(uidList);
        Map<Integer, AppUserEntity> userMap = batchUser.stream().collect(Collectors.toMap(AppUserEntity::getUid, Function.identity()));
        for (DiscussEntity item : records) {
            AppDiscussListResponse response = new AppDiscussListResponse();
            BeanUtils.copyProperties(item, response);
            if (ObjectUtil.isNotNull(userMap.get(item.getUid()))) {
                AppUserShortInfoResponse shortInfoResponse = new AppUserShortInfoResponse();
                BeanUtils.copyProperties(userMap.get(item.getUid()), shortInfoResponse);
                response.setUserInfo(shortInfoResponse);
            }
            response.setPostNumber(postService.getPostNumberByDiscussId(item.getId()));
            responseList.add(response);
        }
        return new AppPageUtils<>(pages, responseList);
    }

    @Override
    public AppPageUtils<AppDiscussListResponse> myDiscuss(DiscussListForm request, AppUserEntity user) {
        Page<DiscussEntity> page = this.lambdaQuery()
                .eq(DiscussEntity::getUid, user.getUid())
                .orderByDesc(DiscussEntity::getId)
                .page(new Page<>(request.getPage(), 20));

        List<DiscussEntity> records = page.getRecords();
        if (records.isEmpty()) {
            return new AppPageUtils<>(Collections.emptyList(), 0, 20, request.getPage());
        }

        AppUserShortInfoResponse shortInfoResponse = new AppUserShortInfoResponse();
        BeanUtils.copyProperties(user, shortInfoResponse);

        List<AppDiscussListResponse> responseList = records.stream().map(item -> {
            AppDiscussListResponse response = new AppDiscussListResponse();
            BeanUtils.copyProperties(item, response);
            response.setUserInfo(shortInfoResponse);
            response.setPostNumber(postService.getPostNumberByDiscussId(item.getId()));
            return response;
        }).collect(Collectors.toList());

        return new AppPageUtils<>(page, responseList);
    }

    @Override
    public DiscussDetailResponse detail(Integer id) {
        DiscussEntity discuss = this.getById(id);
        if(discuss==null){
            throw new LinfengException("该话题不存在");
        }
        discuss.setReadCount(discuss.getReadCount()+1);
        this.updateById(discuss);
        DiscussDetailResponse response=new DiscussDetailResponse();
        BeanUtils.copyProperties(discuss,response);
        AppUserEntity appUser = appUserService.getById(discuss.getUid());
        if(!WechatUtil.isEmpty(appUser.getMobile())){
            appUser.setMobile(WechatUtil.maskMobile(appUser.getMobile()));
        }
        response.setUserInfo(appUser);
        response.setPostCount(postService.getPostNumberByDiscussId(id));
        return response;
    }

    @Override
    public void deleteDiscuss(DiscussDeleteForm request, AppUserEntity user) {
        DiscussEntity discuss = this.getById(request.getId());
        if(!discuss.getUid().equals(user.getUid())){
            throw new LinfengException("您不是话题创建者");
        }
        List<PostEntity> list = postService.lambdaQuery().eq(PostEntity::getDiscussId, discuss.getId()).list();
        if(!list.isEmpty()){
            throw new LinfengException("该话题存在帖子未删除");
        }
        this.removeById(request.getId());
    }

    @Override
    public Boolean addDiscuss(DiscussAddForm request, AppUserEntity user) {
        sensitiveService.checkContent(request.getTitle()+request.getIntroduce());
        DiscussEntity discuss=new DiscussEntity();
        BeanUtils.copyProperties(request,discuss);
        discuss.setCreateTime(DateUtil.nowDateTime());
        discuss.setUid(user.getUid());
        return this.save(discuss);
    }

    /**
     * 查询平台浏览量最高的话题
     * @return
     */
    @Override
    public List<DiscussEntity> hotDiscussList() {
        String result = redisUtils.get(ConfigConstant.DISCUSS_HOT_KEY);
        if(WechatUtil.isEmpty(result)){
            List<DiscussEntity> list = this.lambdaQuery()
                    .orderByDesc(DiscussEntity::getReadCount)
                    .last("limit 20")
                    .list();
            redisUtils.set(ConfigConstant.DISCUSS_HOT_KEY,JSON.toJSON(list).toString(),60 * 60 * 2);
            return list;
        }
        return JSONObject.parseArray(result, DiscussEntity.class);
    }


    /**
     * 查询近一个月浏览量最高的话题
     * @return
     */
    @Override
    public List<DiscussEntity> discussList() {
        DateTime dateTime = cn.hutool.core.date.DateUtil.lastMonth();
        return this.lambdaQuery()
                .gt(DiscussEntity::getCreateTime, dateTime)
                .orderByDesc(DiscussEntity::getReadCount)
                .last("limit 7")
                .list();
    }

    @Override
    public String getDiscussNameById(Integer id) {
        DiscussEntity discuss = this.getById(id);
        if(ObjectUtil.isNull(discuss)){
            return "";
        }
        return discuss.getTitle();
    }

    @Override
    public void checkJoinTopic(Integer id,Integer uid) {
        DiscussEntity discuss = this.lambdaQuery().eq(DiscussEntity::getId, id).one();
        UserTopicEntity one = userTopicService.lambdaQuery()
                .eq(UserTopicEntity::getTopicId, discuss.getTopicId())
                .eq(UserTopicEntity::getUid, uid)
                .one();
        if(null == one){
            throw new LinfengException("请先加入圈子");
        }

    }

    @Override
    public void saveDiscussByAdmin(DiscussEntity discuss) {
        AppUserEntity user = appUserService.getById(discuss.getUid());
        if(user == null){
            throw new LinfengException("用户不存在");
        }
        TopicEntity topic = topicService.getById(discuss.getTopicId());
        if(topic == null){
            throw new LinfengException("圈子不存在");
        }
        discuss.setCreateTime(new Date());
        this.save(discuss);
    }

}