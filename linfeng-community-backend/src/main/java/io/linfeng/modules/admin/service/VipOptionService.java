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
import io.linfeng.common.vo.app.OrderPay;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.entity.UserRechargeEntity;
import io.linfeng.modules.admin.entity.VipOptionEntity;
import io.linfeng.modules.app.param.VipPayForm;
import io.linfeng.modules.app.param.VipRechargeForm;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 会员充值选项
 *
 * @author JL.Yu
 * @email linfengtech001@163.com
 * @date 2022-09-28 14:26:17
 */
public interface VipOptionService extends IService<VipOptionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    String rechargeVip(AppUserEntity user,VipRechargeForm param);

    OrderPay vipPay(AppUserEntity user, VipPayForm param, HttpServletRequest request) throws Exception;

    void rollBackVip(UserRechargeEntity userRecharge);

    void rechargeVipByYue(Integer uid, VipRechargeForm param);
}

