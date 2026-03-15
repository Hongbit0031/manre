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
package io.linfeng.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.vo.app.AppCashInfoResponse;
import io.linfeng.modules.admin.entity.CashOutEntity;
import io.linfeng.modules.app.param.AddCashOutForm;

import java.util.Map;

/**
 * 提现
 *
 * @author JL.Yu
 * @email linfengtech001@163.com
 * @date 2023-02-01 11:43:29
 */
public interface CashOutService extends IService<CashOutEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void submit(AddCashOutForm param,Integer uid);

    boolean checkIsNormal(Integer uid);

    AppCashInfoResponse getAccountBasicInfo(Integer uid);

    void updateCash(CashOutEntity cashOut);
}

