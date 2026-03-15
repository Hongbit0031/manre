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
import io.linfeng.common.exception.LinfengException;
import io.linfeng.common.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.linfeng.modules.admin.dao.CategoryDao;
import io.linfeng.modules.admin.entity.CategoryEntity;
import io.linfeng.modules.admin.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private RedisUtils redisUtils;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveCategory(CategoryEntity category) {
        redisUtils.delete(ConfigConstant.TOPIC_CATEGORY_KEY);
        Long count = this.lambdaQuery().eq(CategoryEntity::getCateName, category.getCateName()).count();
        if(count!=0){
            throw new LinfengException("分类名不能重复");
        }
        this.save(category);
    }

    @Override
    public List<CategoryEntity> getList() {
        String result = redisUtils.get(ConfigConstant.TOPIC_CATEGORY_KEY);
        if(WechatUtil.isEmpty(result)){
            List<CategoryEntity> list = this.lambdaQuery().list();
            redisUtils.set(ConfigConstant.TOPIC_CATEGORY_KEY,JSON.toJSON(list).toString(),60 * 60 * 24);
            return list;
        }
        return JSONObject.parseArray(result, CategoryEntity.class);
    }

}