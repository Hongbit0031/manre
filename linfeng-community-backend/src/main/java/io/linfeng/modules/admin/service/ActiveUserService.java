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
package io.linfeng.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.vo.admin.VisitorInfoResponse;
import io.linfeng.modules.admin.entity.ActiveUserEntity;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.app.param.VisitorForm;
import org.springframework.scheduling.annotation.Async;

import java.util.Map;

/**
 * 用户访问统计
 *
 * @author JL.Yu
 * @email linfengtech001@163.com
 * @date 2024-05-29 12:25:03
 */
public interface ActiveUserService extends IService<ActiveUserEntity> {

    PageUtils queryPage(Map<String, Object> params);

    @Async
    void addVisit(String ip,VisitorForm param,AppUserEntity user);

    VisitorInfoResponse visitData();

    void cleanRecordByDay(Integer day);

    String getAddressByIp(Map<String,Object> params);
}

