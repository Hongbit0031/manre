package io.linfeng.modules.admin.dao;

import io.linfeng.common.vo.admin.ChartDataResponse;
import io.linfeng.common.vo.app.TopicPostResponse;
import io.linfeng.modules.admin.entity.PostEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * 
 * 
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-23 20:49:55
 */
@Mapper
public interface PostDao extends BaseMapper<PostEntity> {

    List<TopicPostResponse> findTopicPostCountBatch(List<Integer> topicIdList);

    @Select("SELECT count(id) as num," +
            "DATE_FORMAT(create_time, '%m-%d') as time " +
            " FROM lf_post where status=0 and create_time >= #{time}" +
            " GROUP BY DATE_FORMAT(create_time,'%Y-%m-%d') " +
            " ORDER BY create_time ASC")
    List<ChartDataResponse> chartList(Date nowMonth);

    @Select("SELECT post_id FROM lf_comment WHERE status = 1 " +
            "GROUP BY post_id " +
            "ORDER BY MAX(create_time) DESC " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<Integer> getLastPostByComment(@Param("offset") Integer offset, @Param("limit") Integer limit);

}
