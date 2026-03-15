package io.linfeng.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.modules.admin.entity.VoteOptionEntity;

import java.util.Map;

/**
 * 
 *
 * @author linfeng
 * @email linfengtech001@163.com
 * @date 2022-06-28 13:56:51
 */
public interface VoteOptionService extends IService<VoteOptionEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

