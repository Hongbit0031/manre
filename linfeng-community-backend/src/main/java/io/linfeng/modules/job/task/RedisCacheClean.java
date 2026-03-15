package io.linfeng.modules.job.task;

import io.linfeng.common.utils.RedisUtils;
import io.linfeng.modules.admin.service.AppUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 定期清理点赞数、评论数、用户粉丝、用户关注等缓存数据
 * @author linfeng
 * @date 2023/10/18 15:17
 */
@Component("redisCacheClean")
public class RedisCacheClean implements ITask {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private AppUserService appUserService;

    @Override
    public void run(String params) {
        logger.debug("redisCacheClean定时任务正在执行，参数为：{}", params);
        redisUtils.deleteKeysWithPrefix("post:info:");
        redisUtils.deleteKeysWithPrefix("user:info:");
        //会员过期检查
        appUserService.checkVipExpirationBatch();
    }
}
