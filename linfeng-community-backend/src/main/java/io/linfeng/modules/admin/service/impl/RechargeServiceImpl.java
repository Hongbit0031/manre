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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.Query;

import io.linfeng.modules.admin.dao.RechargeDao;
import io.linfeng.modules.admin.entity.RechargeEntity;
import io.linfeng.modules.admin.service.RechargeService;


@Service("rechargeService")
public class RechargeServiceImpl extends ServiceImpl<RechargeDao, RechargeEntity> implements RechargeService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        IPage<RechargeEntity> page = this.page(
                new Query<RechargeEntity>().getPage(params),
                new LambdaQueryWrapper<RechargeEntity>().orderByDesc(RechargeEntity::getSort)
        );

        return new PageUtils(page);
    }

    @Override
    public List<RechargeEntity> getAllRecharge() {
        return this.lambdaQuery()
                .eq(RechargeEntity::getStatus, 0)
                .orderByDesc(RechargeEntity::getSort)
                .list();
    }

}