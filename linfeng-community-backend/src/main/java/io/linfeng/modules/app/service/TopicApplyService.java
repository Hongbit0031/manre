package io.linfeng.modules.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.utils.AppPageUtils;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.vo.app.ApplyInfoResponse;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.app.entity.TopicApplyEntity;
import io.linfeng.modules.app.param.ApplyForm;
import io.linfeng.modules.app.param.ApplyListForm;

import java.util.Map;

/**
 * 进圈申请
 *
 * @author pity
 * @email linfengtech002@163.com
 * @date 2023-06-26 14:21:34
 */
public interface TopicApplyService extends IService<TopicApplyEntity> {

    PageUtils queryPage(Map<String, Object> params);

    Boolean getApplyInfoByUserId(Integer uid, Integer id);

    AppPageUtils<ApplyInfoResponse> applyInfoList(ApplyListForm request, AppUserEntity user);

    void agreeApply(ApplyForm param, AppUserEntity user);

    void rejectApply(ApplyForm param, AppUserEntity user);

    Integer checkTopicApplyByAdmin(Integer topicId, AppUserEntity user);
}

