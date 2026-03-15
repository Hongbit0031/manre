package io.linfeng.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.vo.app.ClassTopicImageResponse;
import io.linfeng.common.vo.app.TopicDetailResponse;
import io.linfeng.common.vo.app.TopicListResponse;
import io.linfeng.common.utils.AppPageUtils;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.entity.TopicEntity;
import io.linfeng.modules.app.param.*;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-21 17:01:12
 */
public interface TopicService extends IService<TopicEntity> {

    PageUtils queryPage(Map<String, Object> params);

    AppPageUtils queryByPage(Map<String, Object> params);

    TopicDetailResponse detail(Integer id);

    Integer joinTopic(Integer id, AppUserEntity user);

    void userTopicDel(Integer id, AppUserEntity user);

    void topicDel(Integer id, AppUserEntity user);

    void topicDeleteByAdmin(List<Integer> list);

    AppPageUtils<TopicListResponse> userJoinTopic(UserJoinTopicForm request);

    AppPageUtils<TopicListResponse> queryByPageList(Integer page,Integer classId);

    List<TopicEntity> hotTopic();

    AppPageUtils<ClassTopicImageResponse> classTopicAreImg(Integer classId, Integer page);

    AppPageUtils<TopicListResponse> myCreateTopic(Integer page,AppUserEntity user);

    Integer topicAdd(TopicAddForm topic,AppUserEntity user);

    AppPageUtils<TopicListResponse> search(Integer page, String keyword);

    Boolean topicEdit(TopicUpdateForm topic, AppUserEntity user);

    boolean detection(Integer uid);

    String getQrCode(Integer topicId, String origin, String url,AppUserEntity user) throws Exception ;

    Boolean giveTopic(SetAdminForm request, AppUserEntity user);

    void updateByAdmin(TopicEntity topic);

    List<TopicEntity> getJoinTopicList(Integer id);

    void doBlock(BlockForm param, AppUserEntity user);

    void removeBlock(BlockForm param, AppUserEntity user);

    void joinTopicApply(UserJoinTopicApplyForm request, AppUserEntity user);

    Map<Integer,String> getAllByList(List<Integer> topicIdList);

    void saveTopicByAdmin(TopicEntity topic);

    boolean privateCirclesOpen();
}

