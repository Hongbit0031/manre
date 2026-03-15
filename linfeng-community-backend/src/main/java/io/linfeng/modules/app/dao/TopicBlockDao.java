package io.linfeng.modules.app.dao;

import io.linfeng.modules.app.entity.TopicBlockEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 圈子用户拉黑
 * 
 * @author pity
 * @email linfengtech002@163.com
 * @date 2023-06-25 13:40:04
 */
@Mapper
public interface TopicBlockDao extends BaseMapper<TopicBlockEntity> {
	
}
