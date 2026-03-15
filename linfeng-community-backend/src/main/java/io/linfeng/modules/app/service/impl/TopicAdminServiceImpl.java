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
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.Query;

import io.linfeng.modules.app.dao.TopicAdminDao;
import io.linfeng.modules.app.entity.TopicAdminEntity;
import io.linfeng.modules.app.service.TopicAdminService;


@Service("topicAdminService")
public class TopicAdminServiceImpl extends ServiceImpl<TopicAdminDao, TopicAdminEntity> implements TopicAdminService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<TopicAdminEntity> page = this.page(
                new Query<TopicAdminEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public Boolean isAdmin(Integer uid, Integer id) {
        LambdaQueryWrapper<TopicAdminEntity> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(TopicAdminEntity::getUid, uid);
        lambdaQueryWrapper.eq(TopicAdminEntity::getTopicId, id);
        Long num = baseMapper.selectCount(lambdaQueryWrapper);
        return num > 0;
    }

}