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

import io.linfeng.common.utils.Constant;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.sys.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.Query;

import io.linfeng.modules.app.dao.NameChangeDao;
import io.linfeng.modules.app.entity.NameChangeEntity;
import io.linfeng.modules.app.service.NameChangeService;


@Service("nameChangeService")
public class NameChangeServiceImpl extends ServiceImpl<NameChangeDao, NameChangeEntity> implements NameChangeService {

    @Autowired
    private SysConfigService sysConfigService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<NameChangeEntity> page = this.page(
                new Query<NameChangeEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     * 查看用户是否可以修改名字
     * @param user
     * @return
     */
    @Override
    public boolean canChangeName(AppUserEntity user) {
        Integer count = getCountByMonth(user.getUid());
        if (user.getVip().equals(Constant.VIP_USER)) {
            String vipRename = sysConfigService.getValue(Constant.VIP_RENAME);
            if (count >= Integer.valueOf(vipRename)) {
                return false;
            }
        } else {
            String commonRename = sysConfigService.getValue(Constant.COMMON_RENAME);
            if (count >= Integer.valueOf(commonRename)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取本月用户名称修改次数
     * @param uid
     * @return
     */
    private Integer getCountByMonth(Integer uid){
        Date month = cn.hutool.core.date.DateUtil.beginOfMonth(new Date());
        return this.lambdaQuery()
                .eq(NameChangeEntity::getUid,uid)
                .ge(NameChangeEntity::getCreateTime, month)
                .count()
                .intValue();
    }

}