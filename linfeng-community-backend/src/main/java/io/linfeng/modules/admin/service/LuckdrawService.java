package io.linfeng.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.utils.AppPageUtils;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.entity.LuckdrawEntity;
import io.linfeng.modules.admin.entity.LuckdrawRecordEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 *
 * @author linfeng
 * @email 3582996245@qq.com
 * @date 2022-08-14 14:28:48
 */
public interface LuckdrawService extends IService<LuckdrawEntity> {

    PageUtils queryPage(Map<String, Object> params);

    Map<String,Object> getPrize(AppUserEntity user);

    Map<String,Object> startLuckDraw(AppUserEntity user);

    AppPageUtils<LuckdrawRecordEntity> record(AppUserEntity user,Integer page);
}

