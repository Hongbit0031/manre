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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.linfeng.common.utils.*;
import io.linfeng.common.vo.app.FollowBatchResponse;
import io.linfeng.modules.admin.entity.AppUserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.linfeng.modules.app.dao.FollowDao;
import io.linfeng.modules.app.entity.FollowEntity;
import io.linfeng.modules.app.service.FollowService;


@Service("followService")
public class FollowServiceImpl extends ServiceImpl<FollowDao, FollowEntity> implements FollowService {

    @Autowired
    private FollowDao followDao;
    @Autowired
    private RedisUtils redisUtils;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<FollowEntity> page = this.page(
                new Query<FollowEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     *    isFollow
     * 	  0  关注
     * 	  1  互相关注
     * 	  2  已关注
     *
     * @param uid
     * @param followUid
     * @return
     */
    @Override
    public Integer isFollow(Integer uid, Integer followUid) {
        LambdaQueryWrapper<FollowEntity> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(FollowEntity::getUid, uid);
        lambdaQueryWrapper.eq(FollowEntity::getFollowUid, followUid);
        Long num = baseMapper.selectCount(lambdaQueryWrapper);
        if(num == 0){
            return 0;
        }
        LambdaQueryWrapper<FollowEntity> lambdaQueryWrapper2 = Wrappers.lambdaQuery();
        lambdaQueryWrapper2.eq(FollowEntity::getUid, followUid);
        lambdaQueryWrapper2.eq(FollowEntity::getFollowUid, uid);
        Long num2 = baseMapper.selectCount(lambdaQueryWrapper2);
        if(num2==0){
            return 2;
        }
        return 1;
    }

    @Override
    public boolean isFollowOrNot(Integer uid, Integer followUid) {
        LambdaQueryWrapper<FollowEntity> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(FollowEntity::getUid, uid);
        lambdaQueryWrapper.eq(FollowEntity::getFollowUid, followUid);
        Long num = baseMapper.selectCount(lambdaQueryWrapper);
        if(num == 0){
            return false;
        }
        return true;
    }

    @Override
    public List<FollowBatchResponse> findFollowBatch(List<Integer> list,Integer uid) {
        return followDao.findFollowBatch(list,uid);
    }

    @Override
    public List<Integer> getFollowUids(AppUserEntity user) {
        List<FollowEntity> list = this.lambdaQuery().eq(FollowEntity::getUid, user.getUid()).list();
        return list.stream().map(FollowEntity::getFollowUid).collect(Collectors.toList());
    }

    @Override
    public Integer getFollowCount(Integer uid) {
        Object hashValue = redisUtils.getHashValue(RedisKeys.getUserKey(uid), ConfigConstant.USER_FOLLOW_NUM);
        if(hashValue==null){
            Integer num = this.lambdaQuery().eq(FollowEntity::getUid, uid).count().intValue();
            redisUtils.hashAdd(RedisKeys.getUserKey(uid), ConfigConstant.USER_FOLLOW_NUM,num.toString());
            return num;
        }else{
            return Integer.valueOf(hashValue.toString());
        }
    }

    @Override
    public Integer getFans(Integer uid) {
        Object hashValue = redisUtils.getHashValue(RedisKeys.getUserKey(uid), ConfigConstant.USER_FANS_NUM);
        if(hashValue==null){
            Integer num = this.lambdaQuery().eq(FollowEntity::getFollowUid, uid).count().intValue();
            redisUtils.hashAdd(RedisKeys.getUserKey(uid), ConfigConstant.USER_FANS_NUM,num.toString());
            return num;
        }else{
            return Integer.valueOf(hashValue.toString());
        }
    }

    @Override
    public List<Integer> getFansList(Integer uid) {
        List<FollowEntity> list = this.lambdaQuery()
                .eq(FollowEntity::getFollowUid, uid)
                .orderByDesc(FollowEntity::getId)
                .list();
        if(list.isEmpty()){
            return new ArrayList<>();
        }
        List<Integer> collect = list.stream().map(FollowEntity::getUid).collect(Collectors.toList());
        return collect;
    }

}