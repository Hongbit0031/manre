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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.Query;

import io.linfeng.modules.app.dao.UserTopicDao;
import io.linfeng.modules.app.entity.UserTopicEntity;
import io.linfeng.modules.app.service.UserTopicService;


@Service("userTopicService")
public class UserTopicServiceImpl extends ServiceImpl<UserTopicDao, UserTopicEntity> implements UserTopicService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserTopicEntity> page = this.page(
                new Query<UserTopicEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public Integer findUserTopicService(Integer topicId) {
        LambdaQueryWrapper<UserTopicEntity> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(UserTopicEntity::getTopicId,topicId);
        return baseMapper.selectCount(lambdaQueryWrapper).intValue();
    }

    @Override
    public Boolean isJoin(Integer uid, Integer id) {
        LambdaQueryWrapper<UserTopicEntity> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(UserTopicEntity::getUid, uid);
        lambdaQueryWrapper.eq(UserTopicEntity::getTopicId, id);
        Integer num = baseMapper.selectCount(lambdaQueryWrapper).intValue();

        return num > 0;
    }

    @Override
    public List<Integer> getUidByTopicId(Integer id) {
        QueryWrapper<UserTopicEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserTopicEntity::getTopicId, id);
        List<UserTopicEntity> list = baseMapper.selectList(queryWrapper);
        return list.stream().map(UserTopicEntity::getUid).collect(Collectors.toList());
    }

    @Override
    public List<Integer> getSomeUidListByTopicId(Integer id) {
        List<UserTopicEntity> list = this.lambdaQuery()
                .eq(UserTopicEntity::getTopicId, id)
                .orderByDesc(UserTopicEntity::getCreateTime)
                .last("limit 10")
                .list();
        return list.stream().map(UserTopicEntity::getUid).collect(Collectors.toList());
    }

    @Override
    public List<UserTopicEntity> getTopicIdByUid(Integer uid) {
        return baseMapper.selectList(
                new LambdaQueryWrapper<UserTopicEntity>()
                        .eq(UserTopicEntity::getUid, uid));
    }

}