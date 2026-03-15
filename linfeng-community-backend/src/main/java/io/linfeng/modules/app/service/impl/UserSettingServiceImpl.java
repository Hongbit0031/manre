/**
 * -----------------------------------
 *  Copyright (c) 2022-2026
 *  All rights reserved, Designed By www.linfengtech.cn
 * 林风社交论坛商业版本请务必保留此注释头信息
 * 商业版授权联系技术客服	 QQ:  3582996245
 * 严禁分享、盗用、转卖源码或非法牟利！
 * 版权所有 ，侵权必究！
 * -----------------------------------
 */
package io.linfeng.modules.app.service.impl;

import cn.hutool.core.util.ObjectUtil;
import io.linfeng.common.exception.LinfengException;
import io.linfeng.common.vo.app.AppUserSettingResponse;
import io.linfeng.modules.app.param.UpdateUserSettingForm;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.Query;

import io.linfeng.modules.app.dao.UserSettingDao;
import io.linfeng.modules.app.entity.UserSettingEntity;
import io.linfeng.modules.app.service.UserSettingService;


@Service("userSettingService")
public class UserSettingServiceImpl extends ServiceImpl<UserSettingDao, UserSettingEntity> implements UserSettingService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UserSettingEntity> page = this.page(
                new Query<UserSettingEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     * 用户隐私设置
     * @param uid
     * @return
     */
    @Override
    public AppUserSettingResponse userSetting(Integer uid) {
        AppUserSettingResponse response=new AppUserSettingResponse();
        UserSettingEntity userSetting = this.lambdaQuery().eq(UserSettingEntity::getUid,uid).one();
        if(ObjectUtil.isNull(userSetting)){
            response.setIsFan(false);
            response.setIsWatch(false);
            response.setIsPost(false);
        }else{
            response.setIsFan(userSetting.getIsFollow()==1);
            response.setIsPost(userSetting.getIsPost()==1);
            response.setIsWatch(userSetting.getIsWatch()==1);
        }
        return response;
    }

    @Override
    public void updateUserSetting(Integer uid, UpdateUserSettingForm param) {
        UserSettingEntity userSetting = this.lambdaQuery().eq(UserSettingEntity::getUid,uid).one();
        if(ObjectUtil.isNull(userSetting)){
            UserSettingEntity entity =new UserSettingEntity();
            entity.setUid(uid);
            entity.setIsFollow(calculate(param.getIsFan()));
            entity.setIsPost(calculate(param.getIsPost()));
            entity.setIsWatch(calculate(param.getIsWatch()));
            boolean isSave = this.save(entity);
            if(!isSave){
                throw new LinfengException("更改失败");
            }
        }else{
            userSetting.setIsFollow(calculate(param.getIsFan()));
            userSetting.setIsPost(calculate(param.getIsPost()));
            userSetting.setIsWatch(calculate(param.getIsWatch()));
            boolean update = this.saveOrUpdate(userSetting);
            if(!update){
                throw new LinfengException("更新失败");
            }
        }

    }

    private Integer calculate(Boolean value){
        if(value){
            return 1;
        }
        return 0;
    }

}