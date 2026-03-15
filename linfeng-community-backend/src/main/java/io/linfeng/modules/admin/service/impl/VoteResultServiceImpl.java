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

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.Query;

import io.linfeng.modules.admin.dao.VoteResultDao;
import io.linfeng.modules.admin.entity.VoteResultEntity;
import io.linfeng.modules.admin.service.VoteResultService;


@Service("voteResultService")
public class VoteResultServiceImpl extends ServiceImpl<VoteResultDao, VoteResultEntity> implements VoteResultService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<VoteResultEntity> page = this.page(
                new Query<VoteResultEntity>().getPage(params),
                new QueryWrapper<VoteResultEntity>()
        );

        return new PageUtils(page);
    }

}