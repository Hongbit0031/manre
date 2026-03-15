/**
 * -----------------------------------
 * Copyright (c) 2022-2026
 * All rights reserved, Designed By www.linfengtech.cn
 * 林风社交论坛商业版本请务必保留此注释头信息
 * 商业版授权联系技术客服	 QQ:  3582996245
 * 严禁分享、盗用、转卖源码或非法牟利！
 * 版权所有 ，侵权必究！
 * -----------------------------------
 */
package io.linfeng.modules.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.linfeng.common.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.linfeng.modules.admin.dao.VipBenefitDao;
import io.linfeng.modules.admin.entity.VipBenefitEntity;
import io.linfeng.modules.admin.service.VipBenefitService;


@Service("vipBenefitService")
public class VipBenefitServiceImpl extends ServiceImpl<VipBenefitDao, VipBenefitEntity> implements VipBenefitService {

    @Autowired
    private RedisUtils redisUtils;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<VipBenefitEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(VipBenefitEntity::getSort);
        IPage<VipBenefitEntity> page = this.page(
                new Query<VipBenefitEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<VipBenefitEntity> getList() {
        String result = redisUtils.get(ConfigConstant.VIP_BENEFIT_KEY);
        if(WechatUtil.isEmpty(result)){
            List<VipBenefitEntity> list = this.lambdaQuery()
                    .eq(VipBenefitEntity::getStatus,Constant.NORMAL)
                    .orderByDesc(VipBenefitEntity::getSort).list();
            redisUtils.set(ConfigConstant.VIP_BENEFIT_KEY,JSON.toJSON(list).toString(),60 * 60 * 24);
            return list;
        }
        return JSONObject.parseArray(result, VipBenefitEntity.class);
    }

}