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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.linfeng.common.utils.*;
import io.linfeng.modules.app.param.LinkListForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.linfeng.modules.admin.dao.LinkDao;
import io.linfeng.modules.admin.entity.LinkEntity;
import io.linfeng.modules.admin.service.LinkService;


@Service("linkService")
public class LinkServiceImpl extends ServiceImpl<LinkDao, LinkEntity> implements LinkService {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<LinkEntity> page = this.page(
                new Query<LinkEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<LinkEntity> getPageList(LinkListForm request) {
        if(request.getCateId()==0){
            String result = redisUtils.get(ConfigConstant.BANNER_LIST_KEY_SQUARE);
            if(WechatUtil.isEmpty(result)){
                List<LinkEntity> list = this.lambdaQuery()
                        .eq(LinkEntity::getCateId, request.getCateId())
                        .orderByDesc(LinkEntity::getId)
                        .list();
                redisUtils.set(ConfigConstant.BANNER_LIST_KEY_SQUARE,JSON.toJSON(list).toString(),60 * 60 * 24);
                return list;
            }
            return JSONObject.parseArray(result, LinkEntity.class);
        }else{
            String result = redisUtils.get(ConfigConstant.BANNER_LIST_KEY_MINE);
            if(WechatUtil.isEmpty(result)){
                List<LinkEntity> list = this.lambdaQuery()
                        .eq(LinkEntity::getCateId, request.getCateId())
                        .orderByDesc(LinkEntity::getId)
                        .list();
                redisUtils.set(ConfigConstant.BANNER_LIST_KEY_MINE,JSON.toJSON(list).toString(),60 * 60 * 24);
                return list;
            }
            return JSONObject.parseArray(result, LinkEntity.class);
        }

    }

}