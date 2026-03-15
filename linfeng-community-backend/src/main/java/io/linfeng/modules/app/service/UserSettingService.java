/**
 * -----------------------------------
 *  Copyright (c) 2022-2026
 *  All rights reserved, Designed By www.linfengtech.cn
 * 林风社交论坛商业版本请务必保留此注释头信息
 * 商业版授权联系技术客服	 QQ:  3582996245
 * 严禁分享、盗用、转卖源码或非法牟利！
 * 版权所有 ，侵权必究！
 * -----------------------------------
 */
package io.linfeng.modules.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.vo.app.AppUserSettingResponse;
import io.linfeng.modules.app.entity.UserSettingEntity;
import io.linfeng.modules.app.param.UpdateUserSettingForm;

import java.util.Map;

/**
 * 用户隐私设置
 *
 * @author JL.Yu
 * @email linfengtech001@163.com
 * @date 2022-11-24 15:17:15
 */
public interface UserSettingService extends IService<UserSettingEntity> {

    PageUtils queryPage(Map<String, Object> params);

    AppUserSettingResponse userSetting(Integer uid);

    void updateUserSetting(Integer uid, UpdateUserSettingForm param);
}

