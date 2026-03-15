package io.linfeng.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.vo.app.LuckdrawRecordResponse;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.entity.LuckdrawRecordEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author linfeng
 * @email 3582996245@qq.com
 * @date 2022-08-14 14:28:48
 */
public interface LuckdrawRecordService extends IService<LuckdrawRecordEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<LuckdrawRecordResponse> getLuckDrawRecordList();

    Integer getSurplus(AppUserEntity user);
}

