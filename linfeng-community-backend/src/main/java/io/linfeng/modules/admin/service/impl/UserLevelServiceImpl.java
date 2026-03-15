package io.linfeng.modules.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.linfeng.common.exception.LinfengException;
import io.linfeng.common.utils.*;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.linfeng.modules.admin.dao.UserLevelDao;
import io.linfeng.modules.admin.entity.UserLevelEntity;
import io.linfeng.modules.admin.service.UserLevelService;


@Service("userLevelService")
public class UserLevelServiceImpl extends ServiceImpl<UserLevelDao, UserLevelEntity> implements UserLevelService {


    @Autowired
    private AppUserService appUserService;
    @Autowired
    private RedisUtils redisUtils;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<UserLevelEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByAsc(UserLevelEntity::getLevelId);
        IPage<UserLevelEntity> page = this.page(
                new Query<UserLevelEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public Integer updateUserLevel(AppUserEntity user) {
        List<UserLevelEntity> list = this.getList();
        if (list.size() == 0) {
            return 0;
        }
        Integer integral = user.getIntegral();
        Integer userLevel = list.get(list.size() - 1).getLevelId();
        for (int i = 0; i < list.size(); i++) {
            if (integral < list.get(i).getMaxNum()) {
                userLevel = list.get(i).getLevelId();
                break;
            }
        }
        if (!user.getLevel().equals(userLevel)) {
            boolean update = appUserService.lambdaUpdate()
                    .set(AppUserEntity::getLevel, userLevel)
                    .eq(AppUserEntity::getUid, user.getUid())
                    .update();
            if (!update) {
                throw new LinfengException("用户等级信息更新失败");
            }
            redisUtils.delete(RedisKeys.getUserCacheKey(user.getUid()));
        }
        return userLevel;
    }

    @Override
    public Integer checkUserLevel(Integer uid) {
        AppUserEntity user = appUserService.getById(uid);
        List<UserLevelEntity> list = this.getList();
        if (list.size() == 0) {
            return 0;
        }
        Integer integral = user.getIntegral();
        Integer userLevel = list.get(list.size() - 1).getLevelId();
        for (int i = 0; i < list.size(); i++) {
            if (integral < list.get(i).getMaxNum()) {
                userLevel = list.get(i).getLevelId();
                break;
            }
        }
        if (!user.getLevel().equals(userLevel)) {
            boolean update = appUserService.lambdaUpdate()
                    .set(AppUserEntity::getLevel, userLevel)
                    .eq(AppUserEntity::getUid, user.getUid())
                    .update();
            if (!update) {
                throw new LinfengException("用户等级信息更新失败");
            }
            redisUtils.delete(RedisKeys.getUserCacheKey(user.getUid()));
        }
        return userLevel;
    }


    @Override
    public List<UserLevelEntity> getList() {
        String result = redisUtils.get(ConfigConstant.USER_LEVEL_KEY);
        if(WechatUtil.isEmpty(result)){
            List<UserLevelEntity> list = this.lambdaQuery()
                    .orderByAsc(UserLevelEntity::getLevelId)
                    .list();
            redisUtils.set(ConfigConstant.USER_LEVEL_KEY,JSON.toJSON(list).toString(),60 * 60 * 24);
            return list;
        }
        return JSONObject.parseArray(result, UserLevelEntity.class);
    }

}