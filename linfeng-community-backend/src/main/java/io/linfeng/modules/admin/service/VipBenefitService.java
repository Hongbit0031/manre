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
import io.linfeng.modules.admin.entity.VipBenefitEntity;

import java.util.List;
import java.util.Map;

/**
 * 会员权益
 *
 * @author Pity
 * @email linfengtech002@163.com
 * @date 2023-12-11 22:07:11
 */
public interface VipBenefitService extends IService<VipBenefitEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<VipBenefitEntity> getList();
}

