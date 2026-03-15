package io.linfeng.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.vo.app.PostDetailResponse;
import io.linfeng.common.vo.app.PostListResponse;
import io.linfeng.common.vo.app.PostVipInfoResponse;
import io.linfeng.common.vo.app.ShortVideoListResponse;
import io.linfeng.common.vo.app.TopicPostResponse;
import io.linfeng.common.utils.AppPageUtils;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.entity.PostEntity;
import io.linfeng.modules.admin.param.AddPostByAdminParam;
import io.linfeng.modules.admin.param.DeletePostParam;
import io.linfeng.modules.admin.param.DownPostParam;
import io.linfeng.modules.admin.param.VideoPostForm;
import io.linfeng.modules.app.param.*;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-23 20:49:55
 */
public interface PostService extends IService<PostEntity> {

    PageUtils queryPage(Map<String, Object> params);

    Integer findTopicPostCount(Integer topicId);

    List<TopicPostResponse> findTopicPostCountBatch(List<Integer> topicIdList);

    PostDetailResponse detail(Integer id);

    AppPageUtils<PostListResponse> getPostListByDiscussId(PostListForm request);

    AppPageUtils<PostListResponse> getListByTopicId(PostListForm request);

    AppPageUtils<PostListResponse> getListByUid(PostListForm request);

    Integer addComment(AddCommentForm request, AppUserEntity user);

    void addCollection(AddCollectionForm request, AppUserEntity user);

    AppPageUtils<PostListResponse> joinTopicPost(Integer page, AppUserEntity user);

    AppPageUtils<PostListResponse> lastPost(Integer page);

    AppPageUtils<PostListResponse> lastPostByFilter(Integer page, Integer limit, Integer filter, Integer type);

    AppPageUtils followUserPost(Integer page, AppUserEntity user);

    List<String> findThreeMedia(Integer id);

    Integer getPostNumberByUid(Integer uid);

    Integer addPost(AddPostForm request, AppUserEntity user);

    AppPageUtils<PostListResponse> myCollectPost(Integer page,AppUserEntity user);

    AppPageUtils<PostListResponse> myPost(Integer page, AppUserEntity user);

    AppPageUtils<PostListResponse> search(Integer page, String keyword);

    void del(Integer id, Integer uid);

    Integer getPostNumberByDiscussId(Integer id);

    Boolean setAdmin(SetAdminForm request, AppUserEntity user);

    Boolean cancelAdmin(SetAdminForm request, AppUserEntity user);

    void deleteByAdmin(List<Integer> integers);

    PostVipInfoResponse getVipPostInfo(VipPostInfoForm request);

    Integer voteAdd(AddVoteForm request, AppUserEntity user);

    void userVote(UserVoteForm request, AppUserEntity user);

    void downByAdmin(DownPostParam param);

    void upByAdmin(List<Integer> list);

    void getRobotPostContent();

    AppUserEntity virtualUser();

    Boolean setPostTop(SetPostTopForm request, AppUserEntity user);

    Boolean topPostDel(SetPostTopForm request, AppUserEntity user);

    String getSharePic(Integer postId, String origin, String url,AppUserEntity user) throws Exception;

    List<PostEntity> getHotPost();

    AppPageUtils<PostListResponse> getPostListByType(Integer page, Integer type);

    Integer addArticle(AddArticleForm request, Integer uid);

    void postAddByAdmin(AddPostByAdminParam request);

    AppPageUtils<ShortVideoListResponse> queryShortVideoPageList(VideoListForm request);

    void addReadCount(Integer postId);

    void deletePostIdByAdmin(DeletePostParam param);

    void doVideoPostJob(VideoPostForm param);
}

