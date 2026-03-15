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
import io.linfeng.common.vo.app.AppVersionResponse;
import io.linfeng.modules.admin.entity.AppVersionEntity;

import java.util.Map;

/**
 * APP版本管理
 *
 * @author JL.Yu
 * @email linfengtech001@163.com
 * @date 2025-03-14 12:55:42
 */
public interface AppVersionService extends IService<AppVersionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    AppVersionResponse checkUpdate(String version);
}

