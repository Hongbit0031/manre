package io.linfeng.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.modules.admin.entity.UserMenuEntity;

import java.util.List;
import java.util.Map;

/**
 * 用户菜单
 *
 * @author linfeng
 * @email 3582996245@qq.com
 * @date 2022-07-22 09:33:30
 */
public interface UserMenuService extends IService<UserMenuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<UserMenuEntity> menuList();

    void downBatch(List<Integer> list);

    void upBatch(List<Integer> list);
}

