package io.linfeng.modules.app.dao;

import io.linfeng.common.vo.app.AppFriendResponse;
import io.linfeng.modules.app.entity.FriendEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 
 * 
 * @author JL.Yu
 * @email linfengtech001@163.com
 * @date 2022-11-16 14:05:06
 */
@Mapper
public interface FriendDao extends BaseMapper<FriendEntity> {


    @Update("update lf_friend " +
            "set last_message=#{lastMessage},unread=unread+1,update_time=NOW() " +
            "where session_id=#{sessionId}")
    void updateInfo(@Param("sessionId") String sessionId, @Param("lastMessage") String lastMessage);

    @Update("update lf_friend " +
            "set last_message=#{lastMessage},update_time=NOW() " +
            "where session_id=#{sessionId}")
    void updateWithdrawInfo(@Param("sessionId") String sessionId, @Param("lastMessage") String lastMessage);

    @Select("SELECT f.id AS id," +
            "f.friend_id AS friendId," +
            "f.notation AS notation," +
            "f.session_id AS sessionId," +
            "f.last_message AS lastMessage," +
            "f.unread AS unread," +
            "f.is_hidden AS isHidden," +
            "f.update_time AS updateTime," +
            "u.avatar AS avatar " +
            "FROM lf_friend f,lf_user u " +
            "where u.uid=f.friend_id and f.my_id=#{myId}")
    List<AppFriendResponse> getFriendList(Integer uid);
}
