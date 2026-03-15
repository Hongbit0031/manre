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
package io.linfeng.modules.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.app.entity.NameChangeEntity;

import java.util.Map;

/**
 * 用户名称修改
 *
 * @author JL.Yu
 * @email linfengtech001@163.com
 * @date 2022-10-07 12:40:50
 */
public interface NameChangeService extends IService<NameChangeEntity> {

    PageUtils queryPage(Map<String, Object> params);

    boolean canChangeName(AppUserEntity user);

}

