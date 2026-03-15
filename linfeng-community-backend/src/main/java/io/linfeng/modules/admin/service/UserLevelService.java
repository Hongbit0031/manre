package io.linfeng.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.entity.UserLevelEntity;

import java.util.List;
import java.util.Map;

/**
 * 用户经验值设置
 *
 * @author pity
 * @email linfengtech002@163.com
 * @date 2023-08-02 15:05:25
 */
public interface UserLevelService extends IService<UserLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);

    Integer updateUserLevel(AppUserEntity user);

    Integer checkUserLevel(Integer uid);

    List<UserLevelEntity> getList();
}

