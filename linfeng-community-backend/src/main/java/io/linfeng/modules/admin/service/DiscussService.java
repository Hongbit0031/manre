package io.linfeng.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.vo.app.DiscussDetailResponse;
import io.linfeng.common.utils.AppPageUtils;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.vo.app.AppDiscussListResponse;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.entity.DiscussEntity;
import io.linfeng.modules.app.param.DiscussAddForm;
import io.linfeng.modules.app.param.DiscussDeleteForm;
import io.linfeng.modules.app.param.DiscussListForm;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-24 13:44:37
 */
public interface DiscussService extends IService<DiscussEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<DiscussEntity> getListByTopicId(Integer topicId);

    AppPageUtils<AppDiscussListResponse> getDiscussList(DiscussListForm request);

    AppPageUtils<AppDiscussListResponse> myDiscuss(DiscussListForm request, AppUserEntity user);

    DiscussDetailResponse detail(Integer id);

    void deleteDiscuss(DiscussDeleteForm request, AppUserEntity user);

    Boolean addDiscuss(DiscussAddForm request, AppUserEntity user);

    List<DiscussEntity> discussList();

    List<DiscussEntity> hotDiscussList();

    String getDiscussNameById(Integer id);

    void checkJoinTopic(Integer id,Integer uid);

    void saveDiscussByAdmin(DiscussEntity discuss);
}

