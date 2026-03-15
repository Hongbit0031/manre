package io.linfeng.modules.sys.service;

import io.linfeng.modules.sys.entity.SysUserEntity;
import io.linfeng.modules.sys.entity.SysUserTokenEntity;

import java.util.Set;


public interface ShiroService {

    /**
     * 获取用户权限列表
     */
    Set<String> getUserPermissions(long userId);

    /**
     * token查询用户
     */
    SysUserTokenEntity queryByToken(String token);

    /**
     * 根据用户ID，查询用户
     * @param userId 管理用户ID
     */
    SysUserEntity queryUser(Long userId);
}
