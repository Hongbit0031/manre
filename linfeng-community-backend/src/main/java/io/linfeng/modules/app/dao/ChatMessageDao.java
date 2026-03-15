package io.linfeng.modules.app.dao;

import io.linfeng.modules.app.entity.ChatMessageEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * 
 * 
 * @author JL.Yu
 * @email linfengtech001@163.com
 * @date 2022-11-16 14:05:06
 */
@Mapper
public interface ChatMessageDao extends BaseMapper<ChatMessageEntity> {


    @Update("update lf_chat_message " +
            "set is_withdrawn=1 " +
            "where id=#{id}")
    void withdrawMessage(String id);


}
