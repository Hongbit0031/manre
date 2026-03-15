package io.linfeng.modules.app.dao;

import io.linfeng.modules.app.entity.UserSettingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户隐私设置表
 * 
 * @author JL.Yu
 * @email linfengtech001@163.com
 * @date 2022-11-24 15:17:15
 */
@Mapper
public interface UserSettingDao extends BaseMapper<UserSettingEntity> {
	
}
