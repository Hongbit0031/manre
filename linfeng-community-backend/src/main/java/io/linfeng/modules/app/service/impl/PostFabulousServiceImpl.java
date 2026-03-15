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
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.Query;

import io.linfeng.modules.app.dao.PostFabulousDao;
import io.linfeng.modules.app.entity.PostFabulousEntity;
import io.linfeng.modules.app.service.PostFabulousService;


@Service("postFabulousService")
public class PostFabulousServiceImpl extends ServiceImpl<PostFabulousDao, PostFabulousEntity> implements PostFabulousService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PostFabulousEntity> page = this.page(
                new Query<PostFabulousEntity>().getPage(params),
                new QueryWrapper<PostFabulousEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public Boolean isThumb(Integer uid, Integer id) {
        PostFabulousEntity entity = baseMapper.selectOne(
                new LambdaQueryWrapper<PostFabulousEntity>()
                        .eq(PostFabulousEntity::getPostId, id)
                        .eq(PostFabulousEntity::getUid, uid));
        if(entity!=null){
            return true;
        }
        return false;
    }

}