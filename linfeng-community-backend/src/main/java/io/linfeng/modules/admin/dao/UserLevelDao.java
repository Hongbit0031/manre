package io.linfeng.modules.admin.dao;

import io.linfeng.modules.admin.entity.UserLevelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户经验值设置
 * 
 * @author pity
 * @email linfengtech002@163.com
 * @date 2023-08-02 15:05:25
 */
@Mapper
public interface UserLevelDao extends BaseMapper<UserLevelEntity> {
	
}
