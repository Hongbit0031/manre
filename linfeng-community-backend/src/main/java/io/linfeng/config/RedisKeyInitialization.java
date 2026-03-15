/**
 * -----------------------------------
 *  Copyright (c) 2022-2026
 *  All rights reserved, Designed By www.linfengtech.cn
 *  林风社交论坛商业版本请务必保留此注释头信息
 *  商业版授权联系技术客服	 QQ:  3582996245
 *  严禁分享、盗用、转卖源码或非法牟利！
 *  版权所有 ，侵权必究！
 * -----------------------------------
 */
package io.linfeng.config;

import io.linfeng.common.utils.RedisKeys;
import io.linfeng.common.utils.RedisUtils;
import io.linfeng.modules.sys.entity.SysConfigEntity;
import io.linfeng.modules.sys.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 项目配置项服务启动初始化至redis缓存
 * @author linfeng
 * @date 2022/7/20 9:51
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RedisKeyInitialization  implements CommandLineRunner {

    private final SysConfigService systemConfigService;
    private final RedisUtils redisUtils;


    @Override
    public void run(String... args){
        this.redisKeyInitialization();
    }

    /**
     * 初始化redis
     */
    private void redisKeyInitialization(){
        try {
            //初始化清理点赞数、评论数、用户粉丝、用户关注等缓存数据
            redisUtils.deleteKeysWithPrefix("post:info:");
            redisUtils.deleteKeysWithPrefix("user:info:");
            //初始化配置信息
            List<SysConfigEntity> systemConfigList = systemConfigService.list();
            for (SysConfigEntity systemConfig : systemConfigList) {
                redisUtils.set(RedisKeys.getSysConfigKey(systemConfig.getParamKey()),systemConfig,-1);
            }
            log.info("---------------redisKey初始化成功---------------");
        }catch (Exception e){
            log.error("redisKey初始化失败: {}",e.getMessage());
        }

    }

}
