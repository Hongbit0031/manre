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

import io.linfeng.common.utils.*;
import io.linfeng.modules.admin.dao.AppUserDao;
import io.linfeng.modules.admin.entity.AppUserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.linfeng.modules.admin.dao.SystemDao;
import io.linfeng.modules.admin.entity.SystemEntity;
import io.linfeng.modules.admin.service.SystemService;

import jakarta.servlet.http.HttpServletRequest;


@Service("systemService")
public class SystemServiceImpl extends ServiceImpl<SystemDao, SystemEntity> implements SystemService {

    @Autowired
    private AppUserDao appUserDao;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SystemEntity> page = this.page(
                new Query<SystemEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Async
    @Override
    public void saveUserLoginIp(AppUserEntity userInfo, String ip) {
        String cityInfo = IPUtil.getCityInfo(ip);//获取大致的ip地址 不需要附加配置
//        String cityInfo = IPUtil.getCityInfo3(ip);//获取精确的ip地址 需要申请腾讯地图的key
//        if(cityInfo.equals("")){
//            cityInfo = IPUtil.getCityInfo(ip);
//        }
        userInfo.setCity(cityInfo);
        userInfo.setLastLoginIp(ip);
        userInfo.setUpdateTime(DateUtil.nowDateTime());
        appUserDao.updateById(userInfo);
    }

}