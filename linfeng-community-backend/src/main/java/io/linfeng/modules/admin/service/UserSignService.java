package io.linfeng.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.vo.app.SignResponse;
import io.linfeng.common.vo.app.SignUserResponse;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.entity.SignConfigEntity;
import io.linfeng.modules.admin.entity.UserSignEntity;

import java.util.List;
import java.util.Map;

/**
 * 签到记录表
 *
 * @author linfeng
 * @email linfengtech001@163.com
 * @date 2022-05-07 15:01:03
 */
public interface UserSignService extends IService<UserSignEntity> {

    PageUtils queryPage(Map<String, Object> params);

    SignUserResponse getUserInfo(AppUserEntity user);

    List<SignResponse> getSignList(Integer uid, Integer page, Integer limit);

    int sign(AppUserEntity user);

    List<SignConfigEntity> getConfigList();

    Integer getSignUserCount();
}

