package io.linfeng.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.modules.admin.entity.RechargeEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author linfeng
 * @email linfengtech001@163.com
 * @date 2022-04-21 17:05:58
 */
public interface RechargeService extends IService<RechargeEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<RechargeEntity> getAllRecharge();
}

