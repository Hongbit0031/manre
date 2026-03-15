package io.linfeng.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.vo.app.AppChildrenCommentResponse;
import io.linfeng.common.vo.app.AppCommentResponse;
import io.linfeng.common.vo.app.CommentCountResponse;
import io.linfeng.common.utils.AppPageUtils;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.entity.CommentEntity;
import io.linfeng.modules.app.param.DelCommentForm;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-24 21:29:22
 */
public interface CommentService extends IService<CommentEntity> {

    PageUtils queryPage(Map<String, Object> params);

    Integer getCountByPostId(Integer id);

    List<CommentCountResponse> getAllCountByPostId(List<Integer> id);

    AppPageUtils<AppCommentResponse> queryCommentPage(Integer postId, Integer page);

    List<CommentEntity> getByPid(Long pid);

    void del(DelCommentForm request, AppUserEntity user);

    void deleteByAdmin(List<Long> list);

    AppPageUtils<AppCommentResponse> getCommentListInCache(Integer postId);

    void updateCommentById(CommentEntity comment);

    List<AppChildrenCommentResponse> remainComment(Long id);
}

