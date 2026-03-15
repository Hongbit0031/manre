package io.linfeng.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.entity.SystemEntity;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 
 *
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-19 16:29:48
 */
public interface SystemService extends IService<SystemEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveUserLoginIp(AppUserEntity userInfo, String ip);
}

