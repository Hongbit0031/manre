package io.linfeng.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.modules.admin.entity.NavigationEntity;

import java.util.List;
import java.util.Map;

/**
 * 导航栏模块
 *
 * @author JL.Yu
 * @email linfengtech001@163.com
 * @date 2023-02-04 22:14:59
 */
public interface NavigationService extends IService<NavigationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<NavigationEntity> getNav();

    void downBatch(List<Integer> list);

    void upBatch(List<Integer> list);
}

