package io.linfeng.modules.app.dao;

import cn.hutool.core.date.DateTime;
import io.linfeng.common.vo.app.FollowBatchResponse;
import io.linfeng.common.vo.app.HotUserResponse;
import io.linfeng.modules.app.entity.FollowEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 
 * 
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-24 14:38:31
 */
@Mapper
public interface FollowDao extends BaseMapper<FollowEntity> {

	void cancelFollow(@Param("uid")Integer uid, @Param("followId")Integer followId);

    List<FollowBatchResponse> findFollowBatch(@Param("list")List<Integer> list,@Param("uid")Integer uid);


    @Select("SELECT follow_uid AS uid,COUNT(follow_uid) AS num FROM lf_follow " +
            " WHERE create_time >= #{time} " +
            " GROUP BY follow_uid " +
            " ORDER BY num DESC LIMIT 8")
    List<HotUserResponse> getHotUserList(DateTime dateTime);
}
