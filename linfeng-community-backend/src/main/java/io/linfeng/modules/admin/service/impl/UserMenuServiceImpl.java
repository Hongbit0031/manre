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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.linfeng.modules.admin.dao.UserMenuDao;
import io.linfeng.modules.admin.entity.UserMenuEntity;
import io.linfeng.modules.admin.service.UserMenuService;


@Service("userMenuService")
public class UserMenuServiceImpl extends ServiceImpl<UserMenuDao, UserMenuEntity> implements UserMenuService {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<UserMenuEntity> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(UserMenuEntity::getSort);
        IPage<UserMenuEntity> page = this.page(
                new Query<UserMenuEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    @Override
    public List<UserMenuEntity> menuList() {
        String result = redisUtils.get(ConfigConstant.USER_MENU_CONFIG_KEY);
        if(WechatUtil.isEmpty(result)){
            List<UserMenuEntity> list = this.lambdaQuery()
                    .eq(UserMenuEntity::getStatus, 0)
                    .orderByDesc(UserMenuEntity::getSort)
                    .list();
            redisUtils.set(ConfigConstant.USER_MENU_CONFIG_KEY,JSON.toJSON(list).toString(),60 * 60 * 24);
            return list;
        }
        return JSONObject.parseArray(result, UserMenuEntity.class);
    }

    @Override
    public void downBatch(List<Integer> list) {
        list.forEach(id->{
            UserMenuEntity userMenu = this.getById(id);
            if(userMenu.getStatus()==0){
                userMenu.setStatus(1);
                this.updateById(userMenu);
            }
        });
    }

    @Override
    public void upBatch(List<Integer> list) {
        list.forEach(id->{
            UserMenuEntity userMenu = this.getById(id);
            if(userMenu.getStatus()==1){
                userMenu.setStatus(0);
                this.updateById(userMenu);
            }
        });
    }

}