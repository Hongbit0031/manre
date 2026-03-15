/**
 * -----------------------------------
 *  Copyright (c) 2022-2026
 *  All rights reserved, Designed By www.linfengtech.cn
 *  林风社交论坛商业版本请务必保留此注释头信息
 *  商业版授权联系技术客服	 QQ:  3582996245
 *  严禁分享、盗用、转卖源码或非法牟利！
 *  版权所有 ，侵权必究！
 * -----------------------------------
 */
package io.linfeng.modules.admin.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.linfeng.common.enums.*;
import io.linfeng.common.exception.LinfengException;
import io.linfeng.common.utils.*;
import io.linfeng.common.vo.admin.AdminPostListResponse;
import io.linfeng.common.vo.app.*;
import io.linfeng.config.RestTemplateUtil;
import io.linfeng.modules.admin.entity.*;
import io.linfeng.modules.admin.param.AddPostByAdminParam;
import io.linfeng.modules.admin.param.DeletePostParam;
import io.linfeng.modules.admin.param.DownPostParam;
import io.linfeng.modules.admin.param.VideoPostForm;
import io.linfeng.modules.admin.service.*;
import io.linfeng.modules.app.entity.*;
import io.linfeng.modules.app.param.*;
import io.linfeng.modules.app.service.*;
import io.linfeng.modules.app.utils.LocalUser;
import io.linfeng.modules.app.utils.QRCodeUtils;
import io.linfeng.modules.app.utils.redismq.MessagePublisher;
import io.linfeng.modules.oss.factory.OSSFactory;
import io.linfeng.modules.oss.service.impl.MinioService;
import io.linfeng.modules.sys.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.linfeng.modules.admin.dao.PostDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service("postService")
public class PostServiceImpl extends ServiceImpl<PostDao, PostEntity> implements PostService {

    @Autowired
    private AppUserService appUserService;
    @Autowired
    private TopicService topicService;
    @Autowired
    private FollowService followService;
    @Autowired
    private PostCollectionService postCollectionService;
    @Autowired
    private PostService postService;
    @Autowired
    private UserTopicService userTopicService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private DiscussService discussService;
    @Autowired
    @Lazy
    private MessageService messageService;
    @Autowired
    private SensitiveService sensitiveService;
    @Autowired
    private TopicAdminService topicAdminService;
    @Autowired
    private LocalUser localUser;
    @Autowired
    private BillService billService;
    @Autowired
    private PostDao postDao;
    @Autowired
    private VoteSubjectService voteSubjectService;
    @Autowired
    private VoteOptionService voteOptionService;
    @Autowired
    private VoteResultService voteResultService;
    @Autowired
    private SysConfigService configService;
    @Autowired
    private RestTemplateUtil restTemplateUtil;
    @Autowired
    private TopicTopService topicTopService;
    @Autowired
    private CommentThumbsService commentThumbsService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private SearchService searchService;
    @Autowired
    private UserLevelService userLevelService;
    @Autowired
    private MessagePublisher messagePublisher;
    @Autowired
    private MinioService minioService;

    @Value("${linfeng.path.image}")
    private String imagePath;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PostEntity> queryWrapper=new QueryWrapper<>();
        //条件查询
        String key = (String)params.get("key");
        String pid = (String)params.get("pid");
        String title = (String)params.get("title");
        String status = (String)params.get("status");
        String type = (String)params.get("type");
        String tid = (String)params.get("tid");
        String uid = (String)params.get("uid");
        String isPrivate = (String)params.get("private");


        if(!WechatUtil.isEmpty(key)){
            queryWrapper.like("content", key);
        }
        if(!WechatUtil.isEmpty(title)){
            queryWrapper.like("title", title);
        }
        if(!WechatUtil.isEmpty(pid)&&NumberUtil.isInteger(pid)){
            queryWrapper.lambda().eq(PostEntity::getId,pid);
        }
        if(!WechatUtil.isEmpty(tid)&&NumberUtil.isInteger(tid)){
            queryWrapper.lambda().eq(PostEntity::getTopicId,tid);
        }
        if(!WechatUtil.isEmpty(uid)&&NumberUtil.isInteger(uid)){
            queryWrapper.lambda().eq(PostEntity::getUid,uid);
        }
        if(!WechatUtil.isEmpty(isPrivate)&&NumberUtil.isInteger(isPrivate)){
            queryWrapper.lambda().eq(PostEntity::getIsPrivate,isPrivate);
        }
        if(!WechatUtil.isEmpty(status)){
            queryWrapper.lambda().eq(PostEntity::getStatus, Integer.parseInt(status));
        }
        if(!WechatUtil.isEmpty(type)){
            queryWrapper.lambda().eq(PostEntity::getType, Integer.parseInt(type));
        }
        queryWrapper.lambda().orderByDesc(PostEntity::getId);
        IPage<PostEntity> page = this.page(
                new Query<PostEntity>().getPage(params),
                queryWrapper
        );
        List<PostEntity> data = page.getRecords();

        List<AdminPostListResponse> responseList=new ArrayList<>();
        data.forEach(l->{
            AdminPostListResponse response=new AdminPostListResponse();
            BeanUtils.copyProperties(l,response);
            if(ObjectUtil.isNotNull(response.getDiscussId())&&response.getDiscussId()>0){
                DiscussEntity discussEntity = discussService.getById(response.getDiscussId());
                if(ObjectUtil.isNotNull(discussEntity)){
                    response.setDiscussTitle(discussEntity.getTitle());
                }
            }
            TopicEntity topic = topicService.getById(response.getTopicId());
            response.setTopicName(topic.getTopicName());
            response.setCoverImage(topic.getCoverImage());
            response.setCollectionCount(postCollectionService.collectCount(response.getId()));
            response.setCommentCount(commentService.getCountByPostId(response.getId()));
            AppUserShortInfoResponse shortInfoResponse=new AppUserShortInfoResponse();
            BeanUtils.copyProperties(appUserService.getById(response.getUid()),shortInfoResponse);
            response.setUserInfo(shortInfoResponse);
            response.setAvatar(shortInfoResponse.getAvatar());
            response.setMedia(JsonUtils.JsonToList(l.getMedia()));
            //投票
            if(l.getVoteId()!=null&&l.getVoteId()>0){
                VoteSubjectEntity voteSubject = voteSubjectService.lambdaQuery().eq(VoteSubjectEntity::getId, l.getVoteId()).one();
                List<VoteOptionEntity> voteOptionList = voteOptionService.lambdaQuery().eq(VoteOptionEntity::getVoteId, l.getVoteId()).list();
                VoteInfoResponse voteInfoResponse=new VoteInfoResponse();
                BeanUtils.copyProperties(voteSubject,voteInfoResponse);
                voteInfoResponse.setOptions(voteOptionList);
                voteInfoResponse.setExpireTime(DateUtil.getSecondTimestamp(voteSubject.getExpireTime()));
                voteInfoResponse.setTime(voteSubject.getExpireTime());
                response.setVoteInfo(voteInfoResponse);
            }
            responseList.add(response);
        });
        PageUtils pageUtils=new PageUtils(page);
        pageUtils.setList(responseList);
        return pageUtils;
    }

    /**
     * 查询圈内的帖子数量
     * @param topicId 圈子ID
     * @return 总数
     */
    @Override
    public Integer findTopicPostCount(Integer topicId) {
        String value = redisUtils.get(RedisKeys.getTopicKey(topicId));
        if(value==null){
            LambdaQueryWrapper<PostEntity> lambdaQueryWrapper = Wrappers.lambdaQuery();
            lambdaQueryWrapper.eq(PostEntity::getTopicId,topicId);
            lambdaQueryWrapper.eq(PostEntity::getStatus,0);
            Integer count = baseMapper.selectCount(lambdaQueryWrapper).intValue();
            redisUtils.set(RedisKeys.getTopicKey(topicId),count,60*30);
            return count;
        }else{
            return Integer.valueOf(value);
        }

    }

    @Override
    public List<TopicPostResponse> findTopicPostCountBatch(List<Integer> topicIdList) {
        return postDao.findTopicPostCountBatch(topicIdList);
    }

    @Override
    public PostDetailResponse detail(Integer id) {
        PostEntity post = this.getById(id);
        AppUserEntity user = localUser.getUser();
        //检查帖子状态信息
        checkPostStatus(post,user);
        TopicEntity topicInfo = topicService.getById(post.getTopicId());
        //检查帖子归属圈子信息以及帖子查看权限
        checkPostAuth(post,user,topicInfo);

        PostDetailResponse response=new PostDetailResponse();
        BeanUtils.copyProperties(post,response);
        AppUserEntity userInfo = appUserService.getById(post.getUid());
        AppUserShortInfoResponse userShortInfoResponse=new AppUserShortInfoResponse();
        BeanUtils.copyProperties(userInfo,userShortInfoResponse);
        response.setUserInfo(userShortInfoResponse);
        if(post.getDiscussId()>0){
            response.setDiscussName(discussService.getDiscussNameById(post.getDiscussId()));
        }
        TopicDetailResponse topicDetailResponse=new TopicDetailResponse();
        BeanUtils.copyProperties(topicInfo,topicDetailResponse);
        Integer topicId=topicInfo.getId();
        Integer postCount = postService.findTopicPostCount(topicId);
        topicDetailResponse.setPostCount(postCount);
        topicDetailResponse.setUserCount(topicInfo.getUserNum());
        response.setTopicInfo(topicDetailResponse);

        if(ObjectUtil.isNull(user)){
            response.setIsFollow(false);
            response.setIsCollection(false);
            response.setIsAuthor(false);
        }else{
            response.setIsFollow(followService.isFollowOrNot(user.getUid(), post.getUid()));
            response.setIsCollection(postCollectionService.isCollection(user.getUid(),post.getId()));
            response.setIsAuthor(user.getUid().equals(post.getUid()));
        }
        response.setCollectionCount(postCollectionService.collectCount(post.getId()));
        response.setCommentCount(commentService.getCountByPostId(post.getId()));
        if(post.getType().equals(Constant.POST_TYPE_VIDEO)){
            //视频源加密
            String encryptUrl = UrlEncryptor.encryptUrl(post.getMedia());
            List<String> list =new ArrayList<>();
            list.add(encryptUrl);
            response.setMedia(list);
        }else{
            response.setMedia(JsonUtils.JsonToList(post.getMedia()));//文件处理
        }

        String showType=configService.getValue(Constant.POST_DETAIL_SHOW_TYPE);
        String rewardBtn=configService.getValue(Constant.REWARD_BTN);
        response.setShowType(showType);
        response.setRewardBtn(rewardBtn);
        //投票
        if(post.getVoteId()!=null&&post.getVoteId()>0){
            VoteSubjectEntity voteSubject = voteSubjectService.lambdaQuery().eq(VoteSubjectEntity::getId, post.getVoteId()).one();
            List<VoteOptionEntity> voteOptionList = voteOptionService.lambdaQuery().eq(VoteOptionEntity::getVoteId, post.getVoteId()).list();
            VoteInfoResponse voteInfoResponse=new VoteInfoResponse();
            BeanUtils.copyProperties(voteSubject,voteInfoResponse);
            voteInfoResponse.setOptions(voteOptionList);
            voteInfoResponse.setExpireTime(DateUtil.getSecondTimestamp(voteSubject.getExpireTime()));
            voteInfoResponse.setTime(voteSubject.getExpireTime());
            response.setVoteInfo(voteInfoResponse);
            if (ObjectUtil.isNull(user)) {
                response.setIsVoteResult(false);
            }else{
                VoteResultEntity voteResult = voteResultService.lambdaQuery().eq(VoteResultEntity::getVoteId, post.getVoteId()).eq(VoteResultEntity::getUid, user.getUid()).one();
                if(ObjectUtil.isNull(voteResult)){
                    response.setIsVoteResult(false);
                }else{
                    response.setMyVoteResult(voteResult.getResult());
                    response.setIsVoteResult(true);
                }
            }
        }
        this.updatePv(id);
        return response;
    }

    /**
     * 检查帖子状态信息
     * @param post
     * @param user
     */
    private void checkPostStatus(PostEntity post,AppUserEntity user){
        if(ObjectUtil.isNull(post)){
            throw new LinfengException("该帖子不存在或已删除");
        }
        if(post.getStatus().equals(Constant.POST_REVIEWED)){
            if(ObjectUtil.isNull(user) || !user.getUid().equals(post.getUid())){
                throw new LinfengException("该帖子待审核");
            }
        }
        if(post.getStatus().equals(Constant.POST_BANNER)){
            if(ObjectUtil.isNull(user) || !user.getUid().equals(post.getUid())){
                throw new LinfengException("该帖子已下架");
            }
        }
    }

    /**
     * 检查帖子归属圈子信息以及帖子查看权限
     * @param post
     * @param user
     * @param topicInfo
     */
    private void checkPostAuth(PostEntity post,AppUserEntity user,TopicEntity topicInfo){
        //如果不存在该圈子则删除帖子
        if(ObjectUtil.isNull(topicInfo)){
            this.removeById(post.getId());
            throw new LinfengException("该帖子不存在");
        }
        if(topicInfo.getStatus().equals(Constant.BAN)){
            this.removeById(post.getId());
            throw new LinfengException("帖子所在圈子已禁用");
        }

        if (post.getCut() == 1) {
            if (ObjectUtil.isNull(user)) {
                throw new LinfengException("付费帖请先登录");
            }
            //查看是否是帖子作者本人
            if (!post.getUid().equals(user.getUid())) {
                boolean isPay = billService.vipPostIsPay(post.getId(), user.getUid());
                if (!isPay) {
                    throw new LinfengException("付费帖请先支付",999);
                }
            }
        }
        if(post.getIsPrivate().equals(PrivacyStatus.PRIVACY.getValue())){
            if(ObjectUtil.isNotNull(user)){
                Boolean isJoin = userTopicService.isJoin(user.getUid(), post.getTopicId());
                if(!isJoin){
                    throw new LinfengException("私密贴暂无查看权限");
                }
            }else{
                throw new LinfengException("私密贴暂无查看权限");
            }
        }
    }

    /**
     * 根据话题ID查询帖子
     * @param request
     * @return
     */
    @Override
    public AppPageUtils<PostListResponse> getPostListByDiscussId(PostListForm request) {
        AppPageUtils<PostListResponse> appPage = null;
        Page<PostEntity> page = new Page<>(request.getPage(), 10);
        QueryWrapper<PostEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PostEntity::getStatus,Constant.POST_NORMAL);
        if (ObjectUtil.isNotNull(request.getDisId())) {
            Integer uid = 0;
            AppUserEntity user = localUser.getUser();
            if(ObjectUtil.isNotNull(user)){
                uid = user.getUid();
                //处理私密圈帖子
                DiscussEntity discuss = discussService.getById(request.getDisId());
                if(discuss == null){
                    throw new LinfengException("话题不存在");
                }
                TopicEntity topic = topicService.getById(discuss.getTopicId());
                if(topic.getIsPrivacy().equals(PrivacyStatus.PRIVACY.getValue())){
                    if(!userTopicService.isJoin(user.getUid(),discuss.getTopicId())){
                        queryWrapper.lambda().eq(PostEntity::getIsPrivate,PrivacyStatus.PUBLIC.getValue());
                    }
                }
            }else{
                queryWrapper.lambda().eq(PostEntity::getIsPrivate,PrivacyStatus.PUBLIC.getValue());
            }

            queryWrapper.lambda().eq(PostEntity::getDiscussId, request.getDisId());
            if (ObjectUtil.isNotNull(request.getSort())&&request.getSort().equals("hot")){
                queryWrapper.lambda().orderByDesc(PostEntity::getReadCount);
            }else{
                queryWrapper.lambda().orderByDesc(PostEntity::getId);
            }
            appPage = this.mapPostList(page, queryWrapper, uid);
        }
        return appPage;
    }

    /**
     * 查询圈子内的帖子
     * @param request
     * @return
     */
    @Override
    public AppPageUtils<PostListResponse> getListByTopicId(PostListForm request) {
        AppPageUtils<PostListResponse> appPage;
        Page<PostEntity> page = new Page<>(request.getPage(), 10);
        QueryWrapper<PostEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PostEntity::getStatus,Constant.POST_NORMAL);

        if (ObjectUtil.isNotNull(request.getTopicId())) {
            queryWrapper.lambda().eq(PostEntity::getTopicId, request.getTopicId());
        }
        //查询私密圈帖子逻辑
        AppUserEntity user = localUser.getUser();
        if(ObjectUtil.isNotNull(user)){
            TopicEntity topic = topicService.getById(request.getTopicId());
            if(topic.getIsPrivacy().equals(PrivacyStatus.PRIVACY.getValue())){
                if(!userTopicService.isJoin(user.getUid(),request.getTopicId())){
                    queryWrapper.lambda().eq(PostEntity::getIsPrivate,PrivacyStatus.PUBLIC.getValue());
                }
            }
        }else{
            queryWrapper.lambda().eq(PostEntity::getIsPrivate,PrivacyStatus.PUBLIC.getValue());
        }
        if (ObjectUtil.isNotNull(request.getOrder())) {
            if (request.getOrder().equals(Constant.ORDER_DESC_READCOUNT)) {
                queryWrapper.lambda().orderByDesc(PostEntity::getReadCount);
            } else if (request.getOrder().equals(Constant.ORDER_DESC_ID)) {
                queryWrapper.lambda().orderByDesc(PostEntity::getId);
            } else if (request.getOrder().equals(Constant.ORDER_TYPE_ONE_TWO)) {
                queryWrapper.lambda()
                        .and(wrapper->wrapper.eq(PostEntity::getType,Constant.POST_TYPE_TEXT).or()
                                .eq(PostEntity::getType,Constant.POST_TYPE_VIDEO));
                queryWrapper.lambda().orderByDesc(PostEntity::getId);
            } else if (request.getOrder().equals(Constant.ORDER_TYPE_FOUR)) {
                queryWrapper.lambda().eq(PostEntity::getType,Constant.POST_TYPE_VOTE);
                queryWrapper.lambda().orderByDesc(PostEntity::getId);
            } else if (request.getOrder().equals(Constant.ORDER_TYPE_THREE)) {
                queryWrapper.lambda().eq(PostEntity::getType,Constant.POST_TYPE_ARTICLE);
                queryWrapper.lambda().orderByDesc(PostEntity::getId);
            }
        } else {
            queryWrapper.orderByDesc("post_top","id");
        }
        if(ObjectUtil.isNull(user)){
            appPage = this.mapPostList(page, queryWrapper, 0);
        }else{
            appPage = this.mapPostList(page, queryWrapper, user.getUid());
        }


        return appPage;
    }

    @Override
    public AppPageUtils<PostListResponse> getListByUid(PostListForm request) {
        AppPageUtils<PostListResponse> appPage=null;
        Page<PostEntity> page = new Page<>(request.getPage(), 10);
        QueryWrapper<PostEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PostEntity::getStatus,Constant.POST_NORMAL);
        queryWrapper.lambda().orderByDesc(PostEntity::getId);

        if (ObjectUtil.isNotNull(request.getUid())) {
            queryWrapper.lambda().eq(PostEntity::getUid, request.getUid());
            if(ObjectUtil.isNull(request.getMyUid()) || !request.getUid().equals(request.getMyUid())){
                queryWrapper.lambda().eq(PostEntity::getIsPrivate,PrivacyStatus.PUBLIC.getValue());
            }
            if(ObjectUtil.isNotNull(request.getMyUid())&&request.getMyUid()!=0){
                appPage = this.mapPostList(page, queryWrapper, request.getMyUid());
            }else{
                appPage = this.mapPostList(page, queryWrapper, request.getUid());
            }
        }
        return appPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addComment(AddCommentForm request, AppUserEntity user) {
        checkUserStatus(user);
        //检测文本和图片合法性
        sensitiveService.checkContent(request.getContent());

        String commentCheck = configService.getValue(Constant.COMMENT_CHECK);
        CommentEntity commentEntity=new CommentEntity();
        BeanUtils.copyProperties(request,commentEntity);
        commentEntity.setCreateTime(DateUtil.nowDateTime());
        commentEntity.setUid(user.getUid().longValue());
        commentEntity.setImg(request.getCommentImg() == null ? "" : request.getCommentImg());
        if(commentCheck.equals("1")){
            commentEntity.setStatus(CommentStatus.AUDIT.getValue());
        }else{
            commentEntity.setStatus(CommentStatus.NORMAL.getValue());
            //评论数量缓存修改
            redisUtils.hashChange(RedisKeys.getPostKey(request.getPostId().intValue()),ConfigConstant.POST_COMMENT_NUM,1);
        }
        boolean save = commentService.save(commentEntity);
        if(!save){
            throw new LinfengException("评论发布失败");
        }
        //清理评论列表缓存
        redisUtils.delete(ConfigConstant.COMMENT_KEY+request.getPostId());

        PostEntity post = this.getById(request.getPostId());
        //如果是评论动态则通知动态的作者 如果是回复评论则通知评论用户
        if (ObjectUtil.isNull(request.getToUid())) {
            String content = StrUtil.format(Constant.CONTENT_COMMENT, user.getUsername(), post.getTitle(), request.getContent());
            messageService.sendMessageNotAsync(user.getUid(), post.getUid(), request.getPostId().intValue(), Constant.COMMENT, content, Constant.TITLE_COMMENT);
        }else{
            String content = StrUtil.format(Constant.CONTENT_COMMENT_REPLY, user.getUsername(), post.getTitle(), request.getContent());
            messageService.sendMessageNotAsync(user.getUid(), request.getToUid(), request.getPostId().intValue(), Constant.COMMENT, content, Constant.TITLE_COMMENT);
        }
        //如果有图片，再进行图片合法性异步校验，避免用户等待时间过长
        if(!WechatUtil.isEmpty(request.getCommentImg())){
            messagePublisher.publishMessage(Constant.COMMENT_CONTENT_CHECK+":"+commentEntity.getId());
        }

        return Integer.valueOf(commentCheck);
    }

    @Override
    public void addCollection(AddCollectionForm request, AppUserEntity user) {
        Boolean collection = postCollectionService.isCollection(user.getUid(), request.getId());
        if(collection){
            throw new LinfengException("请勿重复点赞收藏");
        }
        PostCollectionEntity pc=new PostCollectionEntity();
        pc.setPostId(request.getId());
        pc.setUid(user.getUid());
        boolean save = postCollectionService.save(pc);
        if(!save){
            throw new LinfengException("点赞失败");
        }
        redisUtils.hashChange(RedisKeys.getPostKey(request.getId()),ConfigConstant.POST_STAR_NUM,1);
        PostEntity post = this.getById(request.getId());
        String content = StrUtil.format(Constant.CONTENT_POST_STAR,user.getUsername(),post.getTitle());
        messageService.sendMessage(user.getUid(),post.getUid(),request.getId(),Constant.STAR,content,Constant.TITLE_STAR);

    }

    @Override
    public AppPageUtils<PostListResponse> joinTopicPost(Integer currPage, AppUserEntity user) {
        List<UserTopicEntity> topicList=userTopicService.getTopicIdByUid(user.getUid());
        List<Integer> topicIdList = topicList.stream().map(UserTopicEntity::getTopicId).collect(Collectors.toList());
        if(topicIdList.isEmpty()){
            return new AppPageUtils<>(Collections.emptyList(), 0, 20, 1);
        }
        Page<PostEntity> page = new Page<>(currPage,10);
        QueryWrapper<PostEntity> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(PostEntity::getStatus,Constant.POST_NORMAL);
        queryWrapper.lambda().in(PostEntity::getTopicId,topicIdList);
        queryWrapper.orderByDesc("post_top","id");
        return this.mapPostList(page, queryWrapper, user.getUid());
    }

    @Override
    public AppPageUtils<PostListResponse> lastPost(Integer currPage) {
        Page<PostEntity> page = new Page<>(currPage,10);
        QueryWrapper<PostEntity> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(PostEntity::getStatus,Constant.POST_NORMAL);
        queryWrapper.lambda().eq(PostEntity::getIsPrivate,PrivacyStatus.PUBLIC.getValue());
        queryWrapper.orderByDesc("post_top","id");
        AppUserEntity user = localUser.getUser();
        if(ObjectUtil.isNull(user)){
            return this.mapPostList(page, queryWrapper, 0);
        }
        return this.mapPostList(page, queryWrapper, user.getUid());
    }

    @Override
    public AppPageUtils<PostListResponse> lastPostByFilter(Integer currPage, Integer limit, Integer filter, Integer type) {
        this.checkParam(limit,filter,type);
        AppUserEntity user = localUser.getUser();
        QueryWrapper<PostEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PostEntity::getStatus, Constant.POST_NORMAL);
        queryWrapper.lambda().eq(PostEntity::getIsPrivate, PrivacyStatus.PUBLIC.getValue());
        if (type > 0) {
            queryWrapper.lambda().eq(PostEntity::getType, type);
        }
        if (filter == 0) {
            // 按发帖时间排序
            Page<PostEntity> page = new Page<>(currPage, limit);
            queryWrapper.orderByDesc("post_top", "id");
            return this.mapPostList(page, queryWrapper, ObjectUtil.isNull(user) ? 0 : user.getUid());
        } else {
            // 按评论数排序
            Page<PostEntity> page = new Page<>(1, limit);
            Integer offset = (currPage - 1) * limit;
            List<Integer> postIdList = postDao.getLastPostByComment(offset, limit);
            if(!postIdList.isEmpty()){
                queryWrapper.lambda().in(PostEntity::getId, postIdList);
                // 保持查询结果顺序与postIdList一致
                queryWrapper.last("ORDER BY FIELD(id," + String.join(",", postIdList.stream().map(String::valueOf).collect(Collectors.toList())) + ")");
            }else{
                //没有评论按最新发帖排序
                queryWrapper.orderByDesc("post_top", "id");
            }
            return this.mapPostList(page, queryWrapper, ObjectUtil.isNull(user) ? 0 : user.getUid());
        }


    }

    @Override
    public AppPageUtils followUserPost(Integer currPage, AppUserEntity user) {
        List<Integer> followUidList=followService.getFollowUids(user);
        if(followUidList.isEmpty()){
            return null;
        }
        QueryWrapper<PostEntity> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(PostEntity::getStatus,Constant.POST_NORMAL);
        queryWrapper.lambda().eq(PostEntity::getIsPrivate,PrivacyStatus.PUBLIC.getValue());
        queryWrapper.lambda().in(PostEntity::getUid,followUidList);
        queryWrapper.orderByDesc("post_top","id");
        Page<PostEntity> page = new Page<>(currPage,10);
        return this.mapPostList(page, queryWrapper, user.getUid());
    }

    /**
     * 选取圈子中热度最高的三条动态的首图作为展示
     * 加缓存
     * @param id
     * @return
     */
    @Override
    public List<String> findThreeMedia(Integer id) {
        String result = redisUtils.get(ConfigConstant.TOPIC_IMAGE_KEY+id);
        if(!WechatUtil.isEmpty(result)){
            return JSONObject.parseArray(result, String.class);
        }
        QueryWrapper<PostEntity> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(PostEntity::getTopicId,id);
        queryWrapper.lambda().eq(PostEntity::getType,Constant.POST_TYPE_TEXT);
        queryWrapper.lambda().eq(PostEntity::getStatus,Constant.POST_NORMAL);
        queryWrapper.lambda().orderByDesc(PostEntity::getReadCount);
        queryWrapper.last("limit 10");
        List<PostEntity> postEntityList = baseMapper.selectList(queryWrapper);
        List<String> imageList=new ArrayList<>();
        for (int i = 0; i < postEntityList.size() ; i++) {
            if(!postEntityList.get(i).getMedia().equals("")){
                List<String> jsonToList = JsonUtils.JsonToList(postEntityList.get(i).getMedia());
                if(jsonToList.size()>0){
                    if(imageList.size()>2){
                        break;
                    }else{
                        imageList.add(jsonToList.get(0));
                    }
                }
            }
        }
        redisUtils.set(ConfigConstant.TOPIC_IMAGE_KEY+id,JSON.toJSON(imageList).toString(),60 * 60 * 6);
        return imageList;
    }

    @Override
    public Integer getPostNumberByUid(Integer uid) {
        return this.lambdaQuery()
                .eq(PostEntity::getUid, uid)
                .and(wrapper->wrapper.eq(PostEntity::getStatus,Constant.POST_NORMAL).or().eq(PostEntity::getStatus,Constant.POST_REVIEWED))
                .count()
                .intValue();
    }

    @Override
    public Integer addPost(AddPostForm request, AppUserEntity appUser) {
        // 1. 用户验证和参数校验
        AppUserEntity user = appUserService.getById(appUser.getUid());
        checkUserStatus(user);
        validatePostRequest(request, user);
        
        // 2. 内容敏感词检测（同步处理）
        sensitiveService.checkContent(request.getTitle() + request.getContent());

        // 3. 创建帖子实体并设置基本信息
        PostEntity post = createAndConfigurePost(request, user);
        
        // 4. 设置帖子审核状态
        configurePostAuditStatus(post, request);

        // 5. 保存帖子并处理后续操作
        redisUtils.delete(RedisKeys.getTopicKey(request.getTopicId()));
        if (this.save(post)) {
            handlePostSaveSuccess(post, request, user);
            return post.getId();
        }
        return 0;
    }

    /**
     * 验证发帖请求参数
     */
    private void validatePostRequest(AddPostForm request, AppUserEntity user) {
        // 视频类型帖子必须上传视频
        if (request.getType().equals(Constant.POST_TYPE_VIDEO) && request.getMedia().isEmpty()) {
            throw new LinfengException("请上传视频后再发布");
        }
        // 付费帖子额外校验
        checkVipPost(request, user);
    }

    /**
     * 创建帖子实体并设置基本信息
     */
    private PostEntity createAndConfigurePost(AddPostForm request, AppUserEntity user) {
        PostEntity post = new PostEntity();
        BeanUtils.copyProperties(request, post);
        
        // 设置基本信息
        post.setMedia(JSON.toJSONString(request.getMedia()));
        post.setUid(user.getUid());
        post.setCreateTime(DateUtil.nowDateTime());
        
        // 设置隐私状态（根据所属圈子的隐私设置）
        TopicEntity topic = topicService.getById(request.getTopicId());
        post.setIsPrivate(topic.getIsPrivacy());
        
        return post;
    }

    /**
     * 配置帖子审核状态
     */
    private void configurePostAuditStatus(PostEntity post, AddPostForm request) {
        String vipPostConfig = configService.getValue(Constant.POST_VIP);
        String normalPostConfig = configService.getValue(Constant.NORMAL_POST);
        boolean hasMedia = !request.getMedia().isEmpty();
        
        if (request.getCut() == 1) {
            // 处理付费贴审核状态
            if (vipPostConfig.equals("0") || hasMedia) {
                post.setStatus(Constant.POST_REVIEWED);
            }
        } else {
            // 处理普通图文贴审核状态
            if (normalPostConfig.equals("0") || hasMedia) {
                post.setStatus(Constant.POST_REVIEWED);
            }
        }
    }

    /**
     * 处理帖子保存成功后的操作
     */
    private void handlePostSaveSuccess(PostEntity post, AddPostForm request, AppUserEntity user) {
        // 发帖奖励积分
        rewardIntegralByPosting(user);
        
        // 异步内容审核（针对有媒体文件的帖子）
        if (!request.getMedia().isEmpty()) {
            String vipPostConfig = configService.getValue(Constant.POST_VIP);
            String normalPostConfig = configService.getValue(Constant.NORMAL_POST);
            
            // 如果是待人工审核的策略，那图片视频不走百度审核
            if ((request.getCut() == 1 && vipPostConfig.equals("1")) ||
                (request.getCut() == 0 && normalPostConfig.equals("1"))) {
                messagePublisher.publishMessage(Constant.POST_CONTENT_CHECK + ":" + post.getId());
            }
        }
    }

    /**
     * 付费贴权限校验
     * @param request
     * @param user
     */
    private void checkVipPost(AddPostForm request, AppUserEntity user){
        if(request.getCut()==1){
            if(request.getPay().compareTo(BigDecimal.ZERO)<0
                    || request.getPay().compareTo(new BigDecimal(100))>0){
                throw new LinfengException("付费金额不合法");
            }
            if(WechatUtil.isEmpty(request.getBrief())){
                throw new LinfengException("付费简介不能为空");
            }
            String isOpen = configService.getValue(Constant.PAY_POST_BTN);
            if(isOpen.equals("1")){
                throw new LinfengException("付费功能未开启");
            }
            String isVipOpen = configService.getValue(Constant.PAY_POST_VIP);
            if(isVipOpen.equals("1") && user.getVip().equals(Constant.COMMON_USER)){
                throw new LinfengException("您不是会员无付费贴权限");
            }
        }
    }

    @Override
    public AppPageUtils<PostListResponse> myCollectPost(Integer page,AppUserEntity user) {
        List<Integer> postIdList=postCollectionService.getPostListByUid(user.getUid());
        if(postIdList.size()==0){
            return new AppPageUtils<>(Collections.emptyList(), 0, 10, 1);
        }
        QueryWrapper<PostEntity> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(PostEntity::getStatus,Constant.POST_NORMAL);
        queryWrapper.lambda().in(PostEntity::getId,postIdList);
        queryWrapper.lambda().orderByDesc(PostEntity::getId);
        Page<PostEntity> pages = new Page<>(page,10);
        return this.mapPostList(pages, queryWrapper, user.getUid());
    }

    @Override
    public AppPageUtils<PostListResponse> myPost(Integer page, AppUserEntity user) {
        QueryWrapper<PostEntity> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().in(PostEntity::getUid,user.getUid());
        queryWrapper.lambda().orderByDesc(PostEntity::getId);
        Page<PostEntity> pages = new Page<>(page,10);
        return this.mapPostList(pages, queryWrapper, user.getUid());
    }

    @Override
    public AppPageUtils<PostListResponse> search(Integer page, String keyword) {
        if(page==1){
            if(localUser!=null){
                Integer uid = localUser.getUser().getUid();
                searchService.setSearchContent(keyword,uid);
            }
        }
        QueryWrapper<PostEntity> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(PostEntity::getStatus,Constant.POST_NORMAL);
        queryWrapper.lambda().eq(PostEntity::getIsPrivate,PrivacyStatus.PUBLIC.getValue());
        queryWrapper.and(qr->qr.like("content", keyword).or().like("title", keyword));
        queryWrapper.orderByDesc("post_top","id");
        Page<PostEntity> pages = new Page<>(page,20);
        return this.mapPostList(pages, queryWrapper, 0);
    }

    /**
     * 用户端删帖
     * @param id
     * @param uid
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void del(Integer id, Integer uid) {
        PostEntity post = this.getById(id);
        redisUtils.delete(RedisKeys.getTopicKey(post.getTopicId()));
        if(ObjectUtil.isNull(post)){
            throw new LinfengException("该帖子不存在");
        }
        if(!post.getUid().equals(uid)){
            //圈子管理员可以删除帖子
            Boolean admin = topicAdminService.isAdmin(uid, post.getTopicId());
            if(!admin){
                throw new LinfengException("不能删除别人的帖子");
            }
            TopicEntity topic = topicService.getById(post.getTopicId());
            String content = StrUtil.format(Constant.TOPIC_ADMIN_POST_DOWN,post.getTitle(),topic.getTopicName(),uid);
            messageService.sendMessageNotAsync(0,post.getUid(),-1,Constant.SYSMSG,content,Constant.TITLE_VIOLATION);
        }
        this.removeById(id);
        deleteAllPostInfoById(id);
    }

    @Override
    public Integer getPostNumberByDiscussId(Integer discussId) {
        String value = redisUtils.get(RedisKeys.getDiscussKey(discussId));
        if(value==null){
            Integer count = this.lambdaQuery()
                    .eq(PostEntity::getDiscussId, discussId)
                    .count()
                    .intValue();
            redisUtils.set(RedisKeys.getDiscussKey(discussId),count,60*60);
            return count;
        }else{
            return Integer.valueOf(value);
        }
    }

    @Override
    public Boolean setAdmin(SetAdminForm request, AppUserEntity user) {
        TopicAdminEntity topicAdmin =new TopicAdminEntity();
        BeanUtils.copyProperties(request,topicAdmin);
        topicAdmin.setCreateTime(DateUtil.nowDateTime());
        return topicAdminService.save(topicAdmin);
    }

    @Override
    public Boolean cancelAdmin(SetAdminForm request, AppUserEntity user) {
        TopicEntity topic = topicService.getById(request.getTopicId());
        if(topic.getUid().equals(request.getUid())){
            throw new LinfengException("不能解除圈主权限");
        }
        LambdaQueryWrapper<TopicAdminEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TopicAdminEntity::getTopicId,request.getTopicId()).eq(TopicAdminEntity::getUid,request.getUid());
        return topicAdminService.remove(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByAdmin(List<Integer> ids) {
        //通知用户消息违规被删除了
        ids.forEach(postId->{
            PostEntity post = this.getById(postId);
            redisUtils.delete(RedisKeys.getTopicKey(post.getTopicId()));
            String content = StrUtil.format(Constant.ADMIN_POST_DOWN,post.getTitle());
            messageService.sendMessageNotAsync(0,post.getUid(),-1,Constant.SYSMSG,content,Constant.TITLE_VIOLATION);
            deleteAllPostInfoById(postId);
        });
        boolean remove = this.removeByIds(ids);
        if(!remove){
            throw new LinfengException("批量删除失败");
        }
    }

    @Override
    public PostVipInfoResponse getVipPostInfo(VipPostInfoForm request) {
        PostEntity post = this.getById(request.getPostId());
        if(post == null){
            throw new LinfengException("帖子不存在");
        }
        PostVipInfoResponse response=new PostVipInfoResponse();
        response.setTitle(post.getTitle());
        response.setBrief(post.getBrief());
        //如果是自己的帖子可以直接查看
        if(post.getUid().equals(request.getUid())){
            response.setIsBuy(true);
        }else{
            boolean isPay = billService.vipPostIsPay(request.getPostId(), request.getUid());
            response.setIsBuy(isPay);
        }
        response.setPay(post.getPay());
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer voteAdd(AddVoteForm request, AppUserEntity user) {

        VoteSubjectEntity voteSubject=new VoteSubjectEntity();
        voteSubject.setCreateTime(DateUtil.nowDateTime());
        voteSubject.setTitle(request.getVoteTitle());
        voteSubject.setType(request.getVoteType());
        if(request.getExpireTime()==1){
            voteSubject.setExpireTime(DateUtil.addDay(DateUtil.nowDateTimeStr(),1));
        }else if(request.getExpireTime()==2){
            voteSubject.setExpireTime(DateUtil.addDay(DateUtil.nowDateTimeStr(),7));
        }else if(request.getExpireTime()==3){
            voteSubject.setExpireTime(DateUtil.addDay(DateUtil.nowDateTimeStr(),30));
        }else{
            voteSubject.setExpireTime(DateUtil.addDay(DateUtil.nowDateTimeStr(),90));
        }
        if(voteSubjectService.save(voteSubject)){
            request.getVoteOptions().forEach(e->{
                VoteOptionEntity voteOption=new VoteOptionEntity();
                voteOption.setVoteId(voteSubject.getId());
                voteOption.setContent(e.getValue());
                voteOptionService.save(voteOption);
            });
            PostEntity post=new PostEntity();
            BeanUtils.copyProperties(request,post);
            post.setUid(user.getUid());
            post.setTitle(request.getVoteTitle());
            post.setType(Constant.POST_TYPE_VOTE);
            post.setCreateTime(DateUtil.nowDateTime());
            post.setVoteId(voteSubject.getId());
            //设置帖子私密状态
            TopicEntity topic = topicService.getById(request.getTopicId());
            post.setIsPrivate(topic.getIsPrivacy());
            if(this.save(post)){
                return post.getId();
            }
        }
        return 0;
    }

    /**
     * 用户投票
     * @param request
     * @param user
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userVote(UserVoteForm request, AppUserEntity user) {
        VoteResultEntity voteResult=new VoteResultEntity();
        voteResult.setCreateTime(DateUtil.nowDateTime());
        voteResult.setUid(user.getUid());
        voteResult.setVoteId(request.getId());
        voteResult.setResult(request.getVote().toString());
        boolean save = voteResultService.save(voteResult);
        if(!save){
            throw new LinfengException("投票失败");
        }
        request.getVote().forEach(id->{
            UpdateWrapper wrapper = new UpdateWrapper();
            wrapper.eq("id",id);
            wrapper.setSql("`ticket_num` = `ticket_num` + 1");
            boolean update = voteOptionService.update(wrapper);
            if(!update){
                throw new LinfengException("投票失败");
            }
        });
    }


    @Override
    public void downByAdmin(DownPostParam param) {
        if(param.getIsSend()==1 && WechatUtil.isEmpty(param.getDownReason())){
            throw new LinfengException("请填写下架原因");
        }
        PostEntity post = this.getById(param.getId());
        post.setStatus(Constant.POST_BANNER);
        boolean update = this.updateById(post);
        if(!update){
            throw new LinfengException("下架失败");
        }
        redisUtils.delete(RedisKeys.getTopicKey(post.getTopicId()));
        if(param.getIsSend()==1) {
            String content = StrUtil.format(Constant.ADMIN_DOWN, post.getTitle(), param.getDownReason());
            messageService.sendMessage(0, post.getUid(), param.getId(), Constant.SYSMSG, content, Constant.TITLE_VIOLATION);
        }
    }

    @Override
    public void upByAdmin(List<Integer> ids) {
        //通知用户帖子通过审核
        List<PostEntity> list=new ArrayList<>();
        ids.forEach(postId->{
            PostEntity post = this.getById(postId);
            redisUtils.delete(RedisKeys.getTopicKey(post.getTopicId()));
            post.setStatus(Constant.POST_NORMAL);
            list.add(post);
            String content = StrUtil.format(Constant.ADMIN_POST_UP,post.getTitle());
            messageService.sendMessage(0,post.getUid(),postId,Constant.SYSMSG,content,Constant.TITLE_PASS);
        });

        boolean b = this.updateBatchById(list);
        if(!b){
            throw new LinfengException("审核失败");
        }
    }

    /**
     * 创建人机并自动发帖
     */
    @Override
    public void getRobotPostContent() {
        AppUserEntity appUsers = this.virtualUser();
        if(appUsers == null){
            return;
        }
        //请求开放接口数据随机发帖
        PostEntity post=new PostEntity();
        List<String> media=new ArrayList<>();
        post.setUid(appUsers.getUid());
        post.setCreateTime(DateUtil.nowDateTime());
        //请求开放接口获取短句和标题
        JSONObject data = restTemplateUtil.getData(Constant.ROBOT_CONTENT);
        if (ObjectUtil.isNull(data)) {
            return;
        }
        JSONObject result = data.getJSONObject("data");
        String content = result.getString("content");
        String origin = result.getString("origin");
        //请求开放接口获取图片 随机数量
        //可以自己对接开放的图片接口
        /*int count = RandomUtil.randomInt(-2, 5);
        for (int i = 0; i < count; i++) {
            String link = restTemplateUtil.getLink(Constant.ROBOT_PIC);
            if(!link.equals("")){
                JSONObject jsonObject = JSON.parseObject(link);
                String imgUrl = jsonObject.getString("imgurl");
                media.add(imgUrl);
            }
        }*/
        post.setMedia(JSON.toJSONString(media));
        post.setTitle(origin);
        post.setContent(content);
        post.setTopicId(Constant.OFFICIAL_TOPIC_ID);
        post.setType(Constant.POST_TYPE_TEXT);
        post.setReadCount(RandomUtil.randomInt(1,20));
        this.save(post);

    }

    /**
     * 创建虚拟人机
     * @return 虚拟用户实体
     */
    @Override
    public AppUserEntity virtualUser(){
        List<String> list=new ArrayList<>();
        list.add(Constant.DEAULT_TAG);
        String mobile="16"+RandomUtil.randomNumbers(9);
        Long num = appUserService.lambdaQuery().eq(AppUserEntity::getMobile, mobile).count();
        if(num>0){
            return null;
        }
        AppUserEntity appUser = new AppUserEntity();
        appUser.setMobile(mobile);
        appUser.setAvatar(configService.getValue(Constant.DEFAULT_HEAD));
        appUser.setGender(GenderStatus.UNKNOWN.getValue());
        appUser.setUsername(WechatUtil.generateRandomName());
        appUser.setTagStr(JSON.toJSONString(list));
        appUser.setCreateTime(DateUtil.nowDateTime());
        appUser.setUpdateTime(DateUtil.nowDateTime());
        appUser.setType(UserTypeStatus.VIRTUALLY.getValue());
        appUserService.save(appUser);
        AppUserEntity appUsers = appUserService.lambdaQuery().eq(AppUserEntity::getMobile, mobile).one();
        //用户默认加入官方圈子
        topicService.joinTopic(Constant.OFFICIAL_TOPIC_ID,appUsers);
        return appUsers;
    }

    /**
     * 设置帖子圈内置顶
     * @param request
     * @param user
     * @return
     */
    @Override
    public Boolean setPostTop(SetPostTopForm request, AppUserEntity user) {
        Boolean isAdmin = topicAdminService.isAdmin(user.getUid(), request.getTopicId());
        if(!isAdmin){
            throw new LinfengException("非管理员无权限");
        }
        TopicTopEntity topicTop = topicTopService.lambdaQuery()
                .eq(TopicTopEntity::getTopicId, request.getTopicId())
                .eq(TopicTopEntity::getPostId, request.getPostId())
                .one();
        if(ObjectUtil.isNotNull(topicTop)){
            throw new LinfengException("请勿重复置顶");
        }
        TopicTopEntity topicTopEntity=new TopicTopEntity();
        topicTopEntity.setPostId(request.getPostId());
        topicTopEntity.setTopicId(request.getTopicId());
        topicTopEntity.setCreateTime(DateUtil.nowDateTime());
        return topicTopService.save(topicTopEntity);
    }

    /**
     * 取消帖子圈内置顶
     * @param request
     * @param user
     * @return
     */
    @Override
    public Boolean topPostDel(SetPostTopForm request, AppUserEntity user) {
        Boolean isAdmin = topicAdminService.isAdmin(user.getUid(), request.getTopicId());
        if(!isAdmin){
            throw new LinfengException("非管理员无权限");
        }
        TopicTopEntity topicTop = topicTopService.lambdaQuery()
                .eq(TopicTopEntity::getTopicId, request.getTopicId())
                .eq(TopicTopEntity::getPostId, request.getPostId())
                .one();
        if(ObjectUtil.isNull(topicTop)){
            throw new LinfengException("不是置顶贴");
        }
        return topicTopService.removeById(topicTop.getId());
    }

    /**
     * 生成帖子详情海报图
     * @param postId  帖子id
     * @param origin 来源
     * @param url  分享全路径
     * @param user 当前用户
     * @return 海报路径
     * @throws Exception
     */
    @Override
    public String getSharePic(Integer postId, String origin, String url,AppUserEntity user) throws Exception {
        PostEntity post = this.getById(postId);
        if(null == post){
            throw new LinfengException("帖子不存在或已删除");
        }
        String showPic;
        if(null == post.getMedia()){
            showPic=Constant.DEAULT_SHARE_POST;
        }else{
            List<String> list = JsonUtils.JsonToList(post.getMedia());
            if(list.isEmpty()){
                showPic=Constant.DEAULT_SHARE_POST;
            }else{
                showPic=list.get(0);
            }
        }
        String itemName=post.getTitle();

        int width = 640;
        int height = 1052;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = image.createGraphics();

        image = g2d.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.OPAQUE);
        g2d.dispose();
        g2d = image.createGraphics();

        g2d.setBackground(Color.WHITE);
        g2d.clearRect(0, 0, width, height);

        g2d.setColor(new Color(182,249,225));
        g2d.setStroke(new BasicStroke(1));
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        BufferedImage proImg = WechatUtil.readImgInner(showPic);
        if(proImg==null){
            throw new LinfengException("分享图片读取失败，请检查图片地址");
        }
        //关于高度不变 宽度等比例缩放
        int originalWidth = proImg.getWidth();
        int originalHeight = proImg.getHeight();
        double scale = (double) 600 / originalHeight;
        int newWidth = (int) (originalWidth * scale);
        int newHeight = 600;
        int horizontalOffset = (width - newWidth) / 2;
        g2d.drawImage(proImg.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), horizontalOffset, 0, null);

        BufferedImage shareHeadImg = WechatUtil.readImgInner(user.getAvatar());
        if(shareHeadImg==null){
            throw new LinfengException("头像图片读取失败，请检查图片地址");
        }
        int baseHeight = 610;


        g2d.setPaint(Color.GRAY);
        File newFileT = new File("simsunb.ttf");
        if(!newFileT.exists()){
            InputStream streamT =  getClass().getClassLoader()
                    .getResourceAsStream("simsunb.ttf");
            FileUtils.copyInputStreamToFile(streamT, newFileT);
        }
        Font font =  Font.createFont(Font.TRUETYPE_FONT, newFileT);
        g2d.setFont(font.deriveFont(Font.PLAIN,27));
        g2d.drawImage(shareHeadImg.getScaledInstance(120, 120, Image.SCALE_SMOOTH), 50, baseHeight, null);
        g2d.drawString(user.getUsername() + "分享了一条动态", 210, baseHeight + 60);
        //简介
        g2d.setPaint(Color.BLACK);

        final Font logoFont = font.deriveFont(Font.PLAIN,21);
        g2d.setFont(logoFont);
        FontMetrics fm2 = g2d.getFontMetrics(g2d.getFont());
        String displayName = itemName;
        boolean truncated = false;
        while(fm2.stringWidth(displayName) > 260 && displayName.length() > 2) {
            truncated = true;
            displayName = displayName.substring(0,displayName.length()-1);
        }
        if(truncated) {
            while(displayName.length() > 2 && fm2.stringWidth(displayName + "...") > 260) {
                displayName = displayName.substring(0, displayName.length()-1);
            }
            displayName = displayName + "...";
        }
        g2d.setPaint(Color.GRAY);
        final Font logos = font.deriveFont(Font.BOLD,30);
        g2d.setFont(logos);
        g2d.drawString(displayName, 50, baseHeight + 190);
        Font fonts =  Font.createFont(Font.TRUETYPE_FONT, newFileT);
        g2d.setPaint(Color.BLACK);
        g2d.setFont(fonts.deriveFont(Font.BOLD,17));
        BufferedImage qrCode = null;
        if(origin.equals(Constant.H5)){
            g2d.drawString("扫描二维码查看动态详情", 30, baseHeight + 336);
            qrCode = QRCodeUtils.createImage(url, null, true);
        }else if(origin.equals(Constant.WX)){
            g2d.drawString("微信扫码查看动态详情", 30, baseHeight + 336);
            String appId = configService.getValue(Constant.WX_APP_ID);
            String appSecret = configService.getValue(Constant.WX_APP_Secret);
            String accessToken = WechatUtil.getAccessToken(appId, appSecret);
            qrCode = WechatUtil.getWxCode(accessToken, url);
        }
        if (qrCode != null) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2d.drawImage(qrCode.getScaledInstance(160, 160, Image.SCALE_SMOOTH), 448, baseHeight + 266, null);
        }
        g2d.dispose();
        InputStream inputStream = WechatUtil.bufferedImageToInputStream(image);
        return uploadImg(inputStream);
    }

    private String uploadImg(InputStream inputStream){
        String storageMethod = configService.getValue(Constant.STORAGEURL_METHOD);
        if(storageMethod.equals("1")){
            // 本地存储
            String fileName = "post_" + System.currentTimeMillis() + ".jpg";
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
            String dateFolder = dateFormat.format(new Date());
            File file = new File(imagePath + dateFolder + File.separator + fileName);

            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdirs()) {
                    throw new LinfengException("文件夹创建失败");
                }
            }

            try {
                FileUtils.copyInputStreamToFile(inputStream, file);
                String localUrl = configService.getValue(Constant.LOCAL_STORAGEURL_URL);
                return localUrl + "resource/image/" + dateFolder + "/" + fileName;
            } catch (IOException e) {
                throw new LinfengException("本地存储文件上传失败");
            }
        } else if(storageMethod.equals("2")){
            // minio存储
            try {
                // 将InputStream转换为MultipartFile
                MultipartFile multipartFile = WechatUtil.inputStreamToMultipartFile(inputStream, "post.jpg");
                return minioService.uploadFile(multipartFile);
            } catch (Exception e) {
                throw new LinfengException("minio存储文件上传失败");
            }
        }
        // 默认走云存储
        return OSSFactory.build().uploadSuffix(inputStream, "post.jpg");
    }


    @Override
    public List<PostEntity> getHotPost() {
        DateTime dateTime = cn.hutool.core.date.DateUtil.lastMonth();//近一个月最热帖子
//        DateTime dateTime = cn.hutool.core.date.DateUtil.lastWeek();//近一周最热帖子
        List<PostEntity> list = this.lambdaQuery()
                .eq(PostEntity::getPostTop, 0)
                .eq(PostEntity::getStatus, Constant.POST_NORMAL)
                .eq(PostEntity::getIsPrivate, PrivacyStatus.PUBLIC.getValue())
                .eq(PostEntity::getCut,0)
                .gt(PostEntity::getCreateTime, dateTime)
                .orderByDesc(PostEntity::getReadCount)
                .last("limit 10")
                .list();
        if(!list.isEmpty()){
            return list;
        }
        //如果没有热门贴，则查询最新的帖子
        return this.lambdaQuery()
                .eq(PostEntity::getPostTop, 0)
                .eq(PostEntity::getStatus, Constant.POST_NORMAL)
                .eq(PostEntity::getIsPrivate, PrivacyStatus.PUBLIC.getValue())
                .eq(PostEntity::getCut,0)
                .orderByDesc(PostEntity::getId)
                .last("limit 10")
                .list();
    }

    @Override
    public AppPageUtils<PostListResponse> getPostListByType(Integer page, Integer type) {
        QueryWrapper<PostEntity> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(PostEntity::getStatus,Constant.POST_NORMAL);
        queryWrapper.lambda().eq(PostEntity::getIsPrivate,PrivacyStatus.PUBLIC.getValue());
        queryWrapper.lambda().eq(PostEntity::getType,type);
        queryWrapper.lambda().orderByDesc(PostEntity::getId);
        Page<PostEntity> pages = new Page<>(page,10);
        return this.mapPostList(pages, queryWrapper, 0);
    }

    @Override
    public Integer addArticle(AddArticleForm request, Integer uid) {
        AppUserEntity user = appUserService.getById(uid);
        checkUserStatus(user);
        
        // 从富文本内容中提取图片URL
        List<String> imageUrls = HtmlImageExtractor.extractImageUrls(request.getContent());
        log.info("从富文本中提取到 {} 张图片", imageUrls.size());
        //审核文本和封面
        sensitiveService.checkContent(request.getTitle()+request.getContent());
        sensitiveService.checkImage(request.getMedia().get(0));
        PostEntity post = new PostEntity();
        BeanUtils.copyProperties(request, post);
        post.setUid(user.getUid());
        post.setCreateTime(DateUtil.nowDateTime());
        String media = JSON.toJSONString(request.getMedia());
        post.setMedia(media);
        //检查是否要审核，如果富文本不包含图片，则走审核策略。如果包含，则先设置为待审核,由消息队列审核后上架
        String normalPost = configService.getValue(Constant.NORMAL_POST);
        if (normalPost.equals("0") || !imageUrls.isEmpty()) {
            post.setStatus(Constant.POST_REVIEWED);
        }
        TopicEntity topic = topicService.getById(request.getTopicId());
        post.setIsPrivate(topic.getIsPrivacy());
        redisUtils.delete(RedisKeys.getTopicKey(request.getTopicId()));
        if (this.save(post)) {
            //发帖奖励积分
            rewardIntegralByPosting(user);
            //如果审核策略为自动过审且富文本包含图片，那么需要由消息队列审核后上架
            if(!imageUrls.isEmpty() && normalPost.equals("1")){
                messagePublisher.publishMessage(Constant.POST_CONTENT_CHECK + ":" + post.getId());
            }
            return post.getId();
        }
        return 0;
    }

    @Override
    public void postAddByAdmin(AddPostByAdminParam request) {
        PostEntity post=new PostEntity();
        BeanUtils.copyProperties(request,post);
        String media = JSON.toJSONString(request.getMedia());
        post.setMedia(media);
        post.setCreateTime(DateUtil.nowDateTime());
        redisUtils.delete(RedisKeys.getTopicKey(request.getTopicId()));
        boolean save = this.save(post);
        if(!save){
            throw new LinfengException("发帖失败");
        }
    }

    @Override
    public AppPageUtils<ShortVideoListResponse> queryShortVideoPageList(VideoListForm request) {
        Page<PostEntity> page = new Page<>(request.getPage(),10);
        QueryWrapper<PostEntity> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(PostEntity::getStatus,Constant.POST_NORMAL);
        queryWrapper.lambda().eq(PostEntity::getType,Constant.POST_TYPE_VIDEO);
        queryWrapper.lambda().eq(PostEntity::getIsPrivate,PrivacyStatus.PUBLIC.getValue());
        queryWrapper.lambda().eq(PostEntity::getCut,0);
        if(request.getOrder()==1){
            queryWrapper.lambda().orderByDesc(PostEntity::getId);
        }else{
            //热门浏览视频  限制时间查询 查询近360天的热门视频 可自定
            String dateTime = DateUtils.addDateDays(new Date(), -360);
            queryWrapper.lambda().gt(PostEntity::getCreateTime,dateTime);
            queryWrapper.lambda().orderByDesc(PostEntity::getReadCount);
        }
        AppUserEntity user = localUser.getUser();
        AppPageUtils<PostListResponse> appPage;
        if(user==null){
            appPage = this.mapPostList(page, queryWrapper, 0);
        }else{
            appPage = this.mapPostList(page, queryWrapper, user.getUid());
        }
        List<PostListResponse> data = appPage.getData();
        List<ShortVideoListResponse> shortVideoList=new ArrayList<>();
        if(request.getPostId()!=null&&request.getPostId()>0){
            //如果用户携带帖子ID进入视频模块,那需要优先查询该视频贴
            PostDetailResponse detail = this.detail(request.getPostId());
            PostEntity post = this.getById(request.getPostId());
            List<String> list = JsonUtils.JsonToList(post.getMedia());
            ShortVideoListResponse response =new ShortVideoListResponse();
            BeanUtils.copyProperties(detail,response);
            response.setTopicName(detail.getTopicInfo().getTopicName());
            response.setDiscussTitle(detail.getDiscussName());
            response.set_id(detail.getId()+"");
            response.setIsplay(true);
            response.setPlayIng(false);
            response.setState("pause");
            response.setSrc(UrlEncryptor.encryptUrl(list.get(0)));
            shortVideoList.add(response);
        }
        if(data.isEmpty()){
            return new AppPageUtils<>(Collections.emptyList(),0, 10, request.getPage());
        }
        data.forEach(item->{
            ShortVideoListResponse response =new ShortVideoListResponse();
            BeanUtils.copyProperties(item,response);
            response.setMedia(new ArrayList<>());
            response.set_id(item.getId()+"");
            response.setIsplay(true);
            response.setPlayIng(false);
            //视频播放源加密
            response.setSrc(UrlEncryptor.encryptUrl(item.getMedia().get(0)));
            response.setState("pause");
            shortVideoList.add(response);
        });
        return new AppPageUtils<>(shortVideoList, appPage.getLast_page(), appPage.getPer_page(), appPage.getCurrent_page());
    }

    @Override
    public void addReadCount(Integer postId) {
        PostEntity post = this.getById(postId);
        if(post==null){
            return;
        }
        post.setReadCount(post.getReadCount()+1);
        baseMapper.updateById(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePostIdByAdmin(DeletePostParam param) {
        PostEntity post = this.getById(param.getId());
        redisUtils.delete(RedisKeys.getTopicKey(post.getTopicId()));
        if(param.getIsSendDeleteInfo()==1){
            String content = StrUtil.format(Constant.ADMIN_POST_DOWN_REASON,post.getTitle(),param.getDeleteReason());
            messageService.sendMessageNotAsync(0,post.getUid(),-1,Constant.SYSMSG,content,Constant.TITLE_VIOLATION);
        }
        this.removeById(param.getId());
        deleteAllPostInfoById(param.getId());
    }


    @Override
    public void doVideoPostJob(VideoPostForm param) {
        if(param.getStatus().equals(Constant.POST_NORMAL)){
            this.lambdaUpdate()
                    .set(PostEntity::getStatus,Constant.POST_NORMAL)
                    .eq(PostEntity::getStatus,Constant.POST_REVIEWED)
                    .eq(PostEntity::getType, Constant.POST_TYPE_VIDEO)
                    .update();
        }else if(param.getStatus().equals(Constant.POST_REVIEWED)){
            this.lambdaUpdate()
                    .set(PostEntity::getStatus,Constant.POST_REVIEWED)
                    .eq(PostEntity::getStatus,Constant.POST_NORMAL)
                    .eq(PostEntity::getType, Constant.POST_TYPE_VIDEO)
                    .update();
        }

    }

    void checkParam(Integer limit, Integer filter, Integer type) {
        if (limit > 50) {
            throw new LinfengException("分页查询上限50条");
        }
        if (filter != 1 && filter != 0) {
            throw new LinfengException("筛选规则参数非法");
        }
        if (type > 4 || type < 0) {
            throw new LinfengException("帖子类型参数非法");
        }
    }
    /**
     * 检查用户是否被封禁、禁言或注销
     * @param user
     */
    private void checkUserStatus(AppUserEntity user){
        if(user.getStatus().equals(Constant.BAN)){
            log.info("封号用户{}",user.getUid());
            throw new LinfengException(Constant.USER_BAN_MSG,Constant.USER_BAN_CODE);
        }else if(user.getStatus().equals(Constant.USER_PROHIBITION)){
            log.info("禁言用户{}",user.getUid());
            throw new LinfengException(Constant.USER_PROHIBITION_MSG);
        }else if(user.getStatus().equals(Constant.USER_DELETE)){
            log.info("注销用户{}",user.getUid());
            throw new LinfengException(Constant.USER_DELETE_MSG);
        }
    }

    /**
     * 浏览量更新
     * @param id
     */
    private void updatePv(Integer id){
        messagePublisher.publishMessage(Constant.POST_READ_NUM+":"+id);
    }

    /**
     * 发帖奖励积分
     * @param userInfo
     */
    private void rewardIntegralByPosting(AppUserEntity userInfo){
        AppUserEntity user = appUserService.vipExpirationCheck(userInfo);
        String addPostIntegral = configService.getValue(Constant.ADD_POST_INTEGRAL);
        String addPostIntegralLimit = configService.getValue(Constant.ADD_POST_INTEGRAL_LIMIT);
        Integer integral = Integer.valueOf(addPostIntegral);
        Integer limitCount = Integer.valueOf(addPostIntegralLimit);
        if(integral<=0 || limitCount<=0){
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        String startTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00"));
        String endTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd 23:59:59"));
        Timestamp startTimestamp = Timestamp.valueOf(startTime);
        Timestamp endTimestamp = Timestamp.valueOf(endTime);

        Integer count = billService.lambdaQuery()
                .eq(BillEntity::getType, BillDetailEnum.TYPE_9.getValue())
                .eq(BillEntity::getCategory,BillDetailEnum.CATEGORY_2.getValue())
                .eq(BillEntity::getUid,userInfo.getUid())
                .between(BillEntity::getAddTime, startTimestamp, endTimestamp)
                .count()
                .intValue();
        if(count>=limitCount){
            return;
        }
        Integer gain = integral;
        if (user.getVip().equals(Constant.VIP_USER)) {
            String value = configService.getValue(Constant.VIP_INTEGRAL);
            Integer multiple = Integer.valueOf(value);
            if (multiple > 0) {
                gain = integral * multiple;
            }
        }
        boolean update = appUserService.lambdaUpdate()
                .set(AppUserEntity::getIntegral, user.getIntegral() + gain)
                .eq(AppUserEntity::getUid, user.getUid())
                .update();
        if (!update) {
            throw new LinfengException("用户积分更新失败");
        }
        redisUtils.delete(RedisKeys.getUserCacheKey(user.getUid()));
        //积分账单插入
        billService.income(user.getUid().longValue(), "发帖积分奖励", BillDetailEnum.CATEGORY_2.getValue(),
                BillDetailEnum.TYPE_9.getValue(), gain, user.getIntegral().doubleValue(),
                "发帖积分奖励", "");
        userLevelService.checkUserLevel(user.getUid());
    }

    /**
     * 帖子删除后清理其他关联表数据
     * @param postId
     */
    private void deleteAllPostInfoById(Integer postId){
        //1.清理点赞表相关数据
        LambdaQueryWrapper<PostCollectionEntity> postCollectionWrapper = new LambdaQueryWrapper<>();
        postCollectionWrapper.eq(PostCollectionEntity::getPostId,postId);
        postCollectionService.remove(postCollectionWrapper);
        //2.清理评论点赞表相关数据
        List<CommentEntity> list = commentService.lambdaQuery().eq(CommentEntity::getPostId,postId).list();
        if(!list.isEmpty()){
            List<Long> idList = list.stream().map(CommentEntity::getId).collect(Collectors.toList());
            List<CommentThumbsEntity> commentThumbsList = commentThumbsService.lambdaQuery().in(CommentThumbsEntity::getCId, idList).list();
            if(!commentThumbsList.isEmpty()){
                List<Integer> commentThumbsIdList = commentThumbsList.stream().map(CommentThumbsEntity::getId).collect(Collectors.toList());
                commentThumbsService.removeByIds(commentThumbsIdList);
            }
        }
        //3.清理评论表相关数据
        LambdaQueryWrapper<CommentEntity> commentWrapper = new LambdaQueryWrapper<>();
        commentWrapper.eq(CommentEntity::getPostId,postId);
        commentService.remove(commentWrapper);
        //4.删除消息中心关于该帖子的消息
        LambdaQueryWrapper<MessageEntity> messageWrapper = new LambdaQueryWrapper<>();
        messageWrapper.eq(MessageEntity::getPostId,postId);
        messageService.remove(messageWrapper);
        //5.清理圈内置顶贴表数据
        LambdaQueryWrapper<TopicTopEntity> topicTopWrapper = new LambdaQueryWrapper<>();
        topicTopWrapper.eq(TopicTopEntity::getPostId,postId);
        topicTopService.remove(topicTopWrapper);
        //6.清理redis数据
        redisUtils.delete(RedisKeys.getPostKey(postId));
    }

    /**
     * 组装帖子分页
     * @param page 分页
     * @param queryWrapper  查询条件
     * @param uid  用户id
     * @return 用户端帖子分页列表
     */
    public AppPageUtils<PostListResponse>  mapPostList(Page<PostEntity> page,QueryWrapper<PostEntity> queryWrapper,Integer uid){
        Page<PostEntity> pages = baseMapper.selectPage(page,queryWrapper);
        if(pages.getRecords().isEmpty()){
            return new AppPageUtils<>(pages, Collections.emptyList());
        }
        List<PostEntity> data = pages.getRecords();
        List<PostListResponse> responseList=new ArrayList<>();
        List<Integer> uidList = data.stream().map(PostEntity::getUid).collect(Collectors.toList());
        List<Integer> postIdList = data.stream().map(PostEntity::getId).collect(Collectors.toList());
        List<Integer> topicIdList = data.stream().map(PostEntity::getTopicId).collect(Collectors.toList());
        List<CommentCountResponse> commentList = commentService.getAllCountByPostId(postIdList);
        Map<Integer, String> topicNameMap = topicService.getAllByList(topicIdList);
        Map<Integer, Integer> commentMap = commentList.stream().collect(Collectors.toMap(CommentCountResponse::getPostId,CommentCountResponse::getNumber));
        List<AppUserEntity> appUserList = appUserService.getBatchUser(uidList);
        Map<Integer, AppUserEntity> userMap = appUserList.stream().collect(Collectors.toMap(AppUserEntity::getUid,Function.identity()));
        List<PostCountResponse> postCollectionCount = postCollectionService.findBatchCollectCount(postIdList);
        Map<Integer, Integer> postCollectionMap =postCollectionCount.stream().collect(Collectors.toMap(PostCountResponse::getPostId,PostCountResponse::getNumber));

        Map<Integer, Integer> followCollectMap = new HashMap<>();
        Map<Integer, Integer> isCollectBatchMap = new HashMap<>();
        if(uid!=0){
            List<FollowBatchResponse> followBatch = followService.findFollowBatch(uidList, uid);
            followCollectMap=followBatch.stream().collect(Collectors.toMap(FollowBatchResponse::getFollowUid, FollowBatchResponse::getId));
            List<PostIsCollectionResponse> isCollectBatchList = postCollectionService.findIsCollectBatch(postIdList, uid);
            isCollectBatchMap=isCollectBatchList.stream().collect(Collectors.toMap(PostIsCollectionResponse::getPostId,PostIsCollectionResponse::getUid));
        }
        Map<Integer, Integer> finalFollowCollectMap = followCollectMap;
        Map<Integer, Integer> finalIsCollectBatchMap = isCollectBatchMap;

        data.forEach(post->{
            PostListResponse response = new PostListResponse();
            BeanUtils.copyProperties(post, response);
            if (ObjectUtil.isNotNull(response.getDiscussId()) && response.getDiscussId() > 0) {
                DiscussEntity discussEntity = discussService.getById(response.getDiscussId());
                if (ObjectUtil.isNotNull(discussEntity)) {
                    response.setDiscussTitle(discussEntity.getTitle());
                }
            }
            response.setTopicName(topicNameMap.get(post.getTopicId()));
            if (ObjectUtil.isNotNull(postCollectionMap.get(post.getId()))) {
                response.setCollectionCount(postCollectionMap.get(post.getId()));
            } else {
                response.setCollectionCount(0);
            }
            if (ObjectUtil.isNotNull(commentMap.get(post.getId()))) {
                response.setCommentCount(commentMap.get(post.getId()));
            } else {
                response.setCommentCount(0);
            }
            if (ObjectUtil.isNotNull(userMap.get(post.getUid()))) {
                AppUserShortInfoResponse shortInfoResponse =new AppUserShortInfoResponse();
                BeanUtils.copyProperties(userMap.get(post.getUid()),shortInfoResponse);
                response.setUserInfo(shortInfoResponse);
            }
            if(uid != 0){
                if(ObjectUtil.isNotNull(finalFollowCollectMap.get(post.getUid()))){
                    response.setIsFollow(true);
                }else{
                    response.setIsFollow(false);
                }
                if(ObjectUtil.isNotNull(finalIsCollectBatchMap.get(post.getId()))){
                    response.setIsCollection(true);
                }else{
                    response.setIsCollection(false);
                }
            } else {
                response.setIsFollow(false);
                response.setIsCollection(false);
            }
            //付费贴去除内容
            if (post.getCut() == 1) {
                List<String> list = new ArrayList<>();
                list.add(Constant.DEAULT_VIP_POST);
                response.setMedia(list);
                response.setContent(response.getTitle());//这里要去掉具体内容替换为标题，标题可见
            } else {
                response.setMedia(JsonUtils.JsonToList(post.getMedia()));
            }
            if(post.getVoteId()!=null&&post.getVoteId()>0){
                VoteSubjectEntity voteSubject = voteSubjectService.lambdaQuery().eq(VoteSubjectEntity::getId, post.getVoteId()).one();
                List<VoteOptionEntity> voteOptionList = voteOptionService.lambdaQuery().eq(VoteOptionEntity::getVoteId, post.getVoteId()).list();
                VoteInfoResponse voteInfoResponse=new VoteInfoResponse();
                BeanUtils.copyProperties(voteSubject,voteInfoResponse);
                voteInfoResponse.setOptions(voteOptionList);
                voteInfoResponse.setTime(voteSubject.getExpireTime());
                response.setVoteInfo(voteInfoResponse);
            }
            responseList.add(response);
        });
        return new AppPageUtils<>(pages, responseList);
    }


}