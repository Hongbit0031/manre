/**
 * -----------------------------------
 * Copyright (c) 2022-2026
 * All rights reserved, Designed By www.linfengtech.cn
 * 林风社交论坛商业版本请务必保留此注释头信息
 * 商业版授权联系技术客服	 QQ:  3582996245
 * 严禁分享、盗用、转卖源码或非法牟利！
 * 版权所有 ，侵权必究！
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
import io.linfeng.common.enums.BillDetailEnum;
import io.linfeng.common.enums.GenderStatus;
import io.linfeng.common.exception.LinfengException;
import io.linfeng.common.utils.*;
import io.linfeng.common.vo.admin.HomeRateResponse;
import io.linfeng.common.vo.admin.UserInfoResponse;
import io.linfeng.common.vo.app.*;
import io.linfeng.config.RestTemplateUtil;
import io.linfeng.modules.admin.dao.PostDao;
import io.linfeng.modules.admin.dao.UserRechargeDao;
import io.linfeng.modules.admin.entity.*;
import io.linfeng.modules.admin.param.AdminUserInfoParam;
import io.linfeng.modules.admin.param.AdminUserPunishParam;
import io.linfeng.modules.admin.service.*;
import io.linfeng.modules.app.dao.FollowDao;
import io.linfeng.modules.app.entity.*;
import io.linfeng.modules.app.param.*;
import io.linfeng.modules.app.service.*;
import io.linfeng.modules.app.utils.LocalUser;
import io.linfeng.modules.sys.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.linfeng.modules.admin.dao.AppUserDao;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;


@Slf4j
@Service
public class AppUserServiceImpl extends ServiceImpl<AppUserDao, AppUserEntity> implements AppUserService {

    @Autowired
    private UserTopicService userTopicService;

    @Autowired
    private TopicAdminService topicAdminService;

    @Autowired
    private FollowService followService;

    @Autowired
    private PostService postService;

    @Autowired
    private SystemService systemService;

    @Resource
    private FollowDao followDao;

    @Autowired
    private TopicService topicService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private AppUserDao userDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private UserRechargeService userRechargeService;

    @Autowired
    private UserSignService userSignService;

    @Autowired
    private BillService billService;

    @Autowired
    private SysConfigService configService;

    @Autowired
    private NameChangeService nameChangeService;

    @Autowired
    private LocalUser localUser;

    @Autowired
    private FriendService friendService;

    @Autowired
    private UserSettingService userSettingService;

    @Autowired
    private SensitiveService sensitiveService;

    @Autowired
    private UserRechargeDao userRechargeDao;

    @Autowired
    private SearchService searchService;

    @Autowired
    private TopicBlockService topicBlockService;

    @Autowired
    private UserLevelService userLevelService;

    @Autowired
    private RestTemplateUtil restTemplateUtil;


    @Value("${linfeng.thirdlogin.open}")
    private boolean open;

    @Value("${linfeng.thirdlogin.secret}")
    private String secret;



    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<AppUserEntity> queryWrapper = new QueryWrapper<>();
        //模糊查询
        String key = (String) params.get("key");
        String status = (String) params.get("status");
        String type = (String) params.get("type");
        String vipStatus = (String) params.get("vipStatus");
        if (!WechatUtil.isEmpty(key)) {
            if (NumberUtil.isInteger(key)) {
                queryWrapper.lambda().eq(AppUserEntity::getUid, key);
            } else {
                queryWrapper.lambda().like(AppUserEntity::getUsername, key)
                        .or().like(AppUserEntity::getMobile, key)
                        .or().like(AppUserEntity::getLastLoginIp, key);
            }
        }
        if (!WechatUtil.isEmpty(status)) {
            queryWrapper.eq("status", Integer.parseInt(status));
        }
        if (!WechatUtil.isEmpty(vipStatus)) {
            queryWrapper.eq("vip", Integer.parseInt(vipStatus));
        }
        if (!WechatUtil.isEmpty(type)) {
            queryWrapper.eq("type", Integer.parseInt(type));
        }

        queryWrapper.lambda().orderByDesc(AppUserEntity::getUid);
        IPage<AppUserEntity> page = this.page(
                new Query<AppUserEntity>().getPage(params),
                queryWrapper
        );
        //如果不需要对手机号打码就注释掉下面这段
        List<AppUserEntity> records = page.getRecords();
        records.forEach(user -> {
            if (!WechatUtil.isEmpty(user.getMobile())) {
                user.setMobile(WechatUtil.maskMobile(user.getMobile()));
            }
        });
        page.setRecords(records);


        return new PageUtils(page);
    }

    @Override
    public AppPageUtils<UserInfoResponse> findTopicUserPage(TopicUserForm form) {
        AppUserEntity user = localUser.getUser();
        List<Integer> uids = userTopicService.getUidByTopicId(form.getId());
        if (uids.isEmpty()) {
            return new AppPageUtils<>(new ArrayList<>(), 0, 20, form.getPage());
        }
        Page<AppUserEntity> page = new Page<>(form.getPage(), 20);
        QueryWrapper<AppUserEntity> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.lambda().in(AppUserEntity::getUid, uids);
        if (!WechatUtil.isEmpty(form.getSearchContent())) {
            queryWrapper1.lambda().like(AppUserEntity::getUsername, form.getSearchContent());
        }
        Page<AppUserEntity> page1 = this.page(page, queryWrapper1);

        List<AppUserEntity> data = page1.getRecords();
        List<UserInfoResponse> responseList = new ArrayList<>();
        data.forEach(l -> {
            UserInfoResponse userInfoResponse = new UserInfoResponse();
            BeanUtils.copyProperties(l, userInfoResponse);
            Boolean isAdmin = topicAdminService.isAdmin(userInfoResponse.getUid(), form.getId());
            userInfoResponse.setIsAdmin(isAdmin);
            if(user==null){
                userInfoResponse.setHasFollow(0);
            }else{
                Integer follow = followService.isFollow(user.getUid(), userInfoResponse.getUid());
                userInfoResponse.setHasFollow(follow);
            }
            userInfoResponse.setMobile(WechatUtil.maskMobile(userInfoResponse.getMobile()));
            responseList.add(userInfoResponse);
        });

        return new AppPageUtils<>(page1, responseList);
    }

    @Override
    public AppPageUtils<TopicUserShortResponse> findUserPageByTopicAdmin(TopicUserForm form, AppUserEntity user) {

        List<Integer> uids = userTopicService.getUidByTopicId(form.getId());
        if (uids.isEmpty()) {
            return new AppPageUtils<>(new ArrayList<>(), 0, 20, form.getPage());
        }
        Page<AppUserEntity> page = new Page<>(form.getPage(), 20);
        QueryWrapper<AppUserEntity> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.lambda().in(AppUserEntity::getUid, uids);
        if (!WechatUtil.isEmpty(form.getSearchContent())) {
            queryWrapper1.lambda().like(AppUserEntity::getUsername, form.getSearchContent());
        }
        Page<AppUserEntity> page1 = this.page(page, queryWrapper1);

        List<AppUserEntity> data = page1.getRecords();
        List<TopicUserShortResponse> responseList = new ArrayList<>();
        data.forEach(l -> {
            TopicUserShortResponse topicUserResponse = new TopicUserShortResponse();
            BeanUtils.copyProperties(l, topicUserResponse);
            topicUserResponse.setIsBlock(topicBlockService.isBlock(form.getId(), topicUserResponse.getUid()));
            responseList.add(topicUserResponse);
        });
        return new AppPageUtils<>(page1, responseList);
    }

    @Override
    public AppPageUtils<AppUserShortInfoResponse> blockUserList(TopicUserForm form, AppUserEntity user) {
        List<TopicBlockEntity> list = topicBlockService.lambdaQuery()
                .eq(TopicBlockEntity::getTopicId, form.getId())
                .list();
        if (list.isEmpty()) {
            return new AppPageUtils<>(new ArrayList<>(), 0, 20, form.getPage());
        }
        List<Integer> uids = list.stream().map(TopicBlockEntity::getUid).collect(Collectors.toList());
        Page<AppUserEntity> page = new Page<>(form.getPage(), 20);
        QueryWrapper<AppUserEntity> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.lambda().in(AppUserEntity::getUid, uids);
        if (!WechatUtil.isEmpty(form.getSearchContent())) {
            queryWrapper1.lambda().like(AppUserEntity::getUsername, form.getSearchContent());
        }
        Page<AppUserEntity> page1 = this.page(page, queryWrapper1);
        List<AppUserEntity> data = page1.getRecords();
        List<AppUserShortInfoResponse> responseList = new ArrayList<>();
        data.forEach(l -> {
            AppUserShortInfoResponse topicUserResponse = new AppUserShortInfoResponse();
            BeanUtils.copyProperties(l, topicUserResponse);
            responseList.add(topicUserResponse);
        });
        return new AppPageUtils<>(page1, responseList);
    }


    @Override
    public AppUserResponse getUserInfo(AppUserEntity users) {
        AppUserResponse response = new AppUserResponse();
        AppUserEntity user = this.getById(users.getUid());
        BeanUtils.copyProperties(user, response);
        response.setFans(followService.getFans(user.getUid()));
        response.setFollow(followService.getFollowCount(user.getUid()));
        response.setPostNum(postService.getPostNumberByUid(user.getUid()));
        response.setMobile(WechatUtil.maskMobile(user.getMobile()));
        response.setTagStr(JsonUtils.JsonToList(user.getTagStr()));
        if (user.getVip().equals(Constant.VIP_USER)) {
            boolean isBefore = response.getVipExpireTime().before(DateUtil.nowDateTime());
            if (isBefore) {
                user.setVip(Constant.COMMON_USER);
                this.saveOrUpdate(user);
                response.setVip(Constant.COMMON_USER);
            }
        }
        userLevelService.updateUserLevel(user);
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer wxLogin(WxLoginForm form, String ip) {

        String appId = configService.getValue(Constant.WX_APP_ID);
        String appSecret = configService.getValue(Constant.WX_APP_Secret);
        JSONObject wx = WechatUtil.getOpenId(form.getCode(), appId, appSecret);
        if (ObjectUtil.isNull(wx) || ObjectUtil.isNull(wx.get("openid"))) {
            throw new LinfengException("openid解析失败");
        }
        String openid = wx.get("openid").toString();
        if (!WechatUtil.isEmpty(openid)) {
            LambdaQueryWrapper<AppUserEntity> lambdaQueryWrapper = Wrappers.lambdaQuery();
            lambdaQueryWrapper.eq(AppUserEntity::getOpenid, openid);
            AppUserEntity appUserEntity = baseMapper.selectOne(lambdaQueryWrapper);
            if (appUserEntity != null) {
                checkUserCanLogin(appUserEntity);
                this.vipExpirationCheck(appUserEntity);
                redisUtils.delete(RedisKeys.getUserCacheKey(appUserEntity.getUid()));
                systemService.saveUserLoginIp(appUserEntity, ip);
                return appUserEntity.getUid();
            } else {
                List<String> list=new ArrayList<>();
                list.add(Constant.DEAULT_TAG);
                AppUserEntity appUser = new AppUserEntity();
                appUser.setOpenid(openid);
                appUser.setMobile("");
                appUser.setAvatar(configService.getValue(Constant.DEFAULT_HEAD));
                appUser.setGender(GenderStatus.UNKNOWN.getValue());
                appUser.setUsername("LF_"+RandomUtil.randomNumbers(7));
                String tag = JSON.toJSONString(list);
                appUser.setTagStr(tag);
                appUser.setCreateTime(DateUtil.nowDateTime());
                appUser.setUpdateTime(DateUtil.nowDateTime());
                baseMapper.insert(appUser);
                AppUserEntity appUsers = this.lambdaQuery().eq(AppUserEntity::getOpenid, openid).one();
                //新用户默认加入官方圈子
                topicService.joinTopic(Constant.OFFICIAL_TOPIC_ID,appUser);
                systemService.saveUserLoginIp(appUsers,ip);

                return appUsers.getUid();
            }

        } else {
            throw new LinfengException("openid获取失败");
        }
    }

    @Override
    public void updateAppUserInfo(AppUserUpdateForm appUserUpdateForm, AppUserEntity appUser) {
        AppUserEntity user = this.getById(appUser.getUid());
        if (!WechatUtil.isEmpty(appUserUpdateForm.getAvatar())) {
            sensitiveService.checkImage(appUserUpdateForm.getAvatar());
            user.setAvatar(appUserUpdateForm.getAvatar());
        }
        if (!WechatUtil.isEmpty(appUserUpdateForm.getBgImg())) {
            sensitiveService.checkImage(appUserUpdateForm.getBgImg());
            user.setBgImg(appUserUpdateForm.getBgImg());
        }
        if (!WechatUtil.isEmpty(appUserUpdateForm.getGender())) {
            user.setGender(appUserUpdateForm.getGender());
        }
        if (!WechatUtil.isEmpty(appUserUpdateForm.getIntro())) {
            //先检测是否违规
            sensitiveService.checkContent(appUserUpdateForm.getIntro());
            user.setIntro(appUserUpdateForm.getIntro());
        }
        if (!WechatUtil.isEmpty(appUserUpdateForm.getTagStr())) {
            String tag = JSON.toJSONString(appUserUpdateForm.getTagStr());
            user.setTagStr(tag);
        }
        if (!WechatUtil.isEmpty(appUserUpdateForm.getUsername())) {
            if(appUserUpdateForm.getUsername().equals(Constant.DELETE_ACCOUNT_NAME)){
                throw new LinfengException("用户名违规禁止使用");
            }
            //先检测是否违规
            sensitiveService.checkContent(appUserUpdateForm.getUsername());
            boolean canChangeName = nameChangeService.canChangeName(user);
            if (canChangeName) {
                NameChangeEntity name = new NameChangeEntity();
                name.setCreateTime(DateUtil.nowDateTime());
                name.setUid(user.getUid());
                nameChangeService.save(name);
                user.setUsername(appUserUpdateForm.getUsername());
            } else {
                throw new LinfengException("本月改名次数已用完");
            }
        }

        baseMapper.updateById(user);
        redisUtils.delete(RedisKeys.getUserCacheKey(user.getUid()));
    }

    @Override
    public void addFollow(AddFollowForm request, AppUserEntity user) {
//		if(request.getId().equals(user.getUid()) ){
//			throw new LinfengException("不能关注自己哦");
//		}
        Boolean follow = followService.isFollowOrNot(user.getUid(), request.getId());
        if (follow) {
            throw new LinfengException("请勿重复关注");
        }
        FollowEntity followEntity = new FollowEntity();
        followEntity.setCreateTime(DateUtil.nowDateTime());
        followEntity.setFollowUid(request.getId());
        followEntity.setUid(user.getUid());
        followService.save(followEntity);
        //缓存修改
        redisUtils.hashChange(RedisKeys.getUserKey(user.getUid()),ConfigConstant.USER_FOLLOW_NUM,1);
        redisUtils.hashChange(RedisKeys.getUserKey(request.getId()),ConfigConstant.USER_FANS_NUM,1);

        String content = StrUtil.format(Constant.CONTENT_WATCH, user.getUsername());
        messageService.sendMessage(user.getUid(), request.getId(), 0, Constant.WATCH, content, Constant.TITLE_WATCH);
    }

    @Override
    public void cancelFollow(AddFollowForm request, AppUserEntity user) {

        followDao.cancelFollow(user.getUid(), request.getId());
        redisUtils.hashChange(RedisKeys.getUserKey(user.getUid()),ConfigConstant.USER_FOLLOW_NUM,-1);
        redisUtils.hashChange(RedisKeys.getUserKey(request.getId()),ConfigConstant.USER_FANS_NUM,-1);

    }

    @Override
    public AppUserInfoResponse findUserInfoById(Integer uid) {

        if(uid==0){
            throw new LinfengException("请先登录哦", HttpStatus.UNAUTHORIZED.value());
        }
        AppUserEntity userEntity = this.getById(uid);
        if(userEntity==null){
            throw new LinfengException("用户不存在");
        }
        AppUserInfoResponse response = new AppUserInfoResponse();
        BeanUtils.copyProperties(userEntity, response);
        List<String> list = JsonUtils.JsonToList(userEntity.getTagStr());
        response.setTagStr(list);
        AppPageUtils appPageUtils = topicService.myCreateTopic(1, userEntity);
        List<TopicListResponse> data = (List<TopicListResponse>) appPageUtils.getData();
        response.setCreateTopicList(data);
        response.setFans(followService.getFans(userEntity.getUid()));
        response.setFollow(followService.getFollowCount(userEntity.getUid()));
        response.setPostNum(postService.getPostNumberByUid(userEntity.getUid()));
        AppUserEntity user = localUser.getUser();
        if(user==null){
            response.setIsFollow(false);
            response.setIsFriend(false);
        }else{
            response.setIsFollow(followService.isFollowOrNot(user.getUid(), uid));
            response.setIsFriend(friendService.checkIsFriend(user.getUid(), uid));
        }
        String value = configService.getValue(Constant.SOCIAL_BTN);
        response.setSocialOpen(value.equals("0"));
        //用户隐私设置
        UserSettingEntity userSetting = userSettingService.lambdaQuery().eq(UserSettingEntity::getUid, uid).one();
        if (ObjectUtil.isNull(userSetting)) {
            response.setIsFan(false);
            response.setIsWatch(false);
            response.setIsPost(false);
        } else {
            if(user==null){
                response.setIsFan(false);
                response.setIsWatch(false);
                response.setIsPost(false);
            }else{
                if (uid.equals(user.getUid())) {
                    response.setIsFan(false);
                    response.setIsWatch(false);
                    response.setIsPost(false);
                } else {
                    response.setIsFan(userSetting.getIsFollow() == 1);
                    response.setIsPost(userSetting.getIsPost() == 1);
                    response.setIsWatch(userSetting.getIsWatch() == 1);
                }
            }
        }
        return response;
    }

    @Override
    public AppPageUtils<UserInfoResponse> search(Integer currPage, String keyword, Integer uid) {

        if (currPage == 1) {
            searchService.setSearchContent(keyword, uid);
        }

        Page<AppUserEntity> page = new Page<>(currPage, 20);
        LambdaQueryWrapper<AppUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(AppUserEntity::getUsername, keyword);
        queryWrapper.orderByDesc(AppUserEntity::getUid);
        Page<AppUserEntity> pageInfo = this.page(page, queryWrapper);

        List<AppUserEntity> userList = pageInfo.getRecords();
        List<UserInfoResponse> responseList = new ArrayList<>();
        userList.forEach(userInfo -> {
            UserInfoResponse userInfoResponse = new UserInfoResponse();
            BeanUtils.copyProperties(userInfo, userInfoResponse);
            Integer follow = followService.isFollow(uid, userInfoResponse.getUid());
            userInfoResponse.setHasFollow(follow);
            userInfoResponse.setMobile(WechatUtil.maskMobile(userInfoResponse.getMobile()));
            responseList.add(userInfoResponse);
        });

        return new AppPageUtils<>(pageInfo, responseList);
    }


    @Override
    public AppPageUtils<UserInfoResponse> userFans(Integer currPage, Integer target, Integer uid) {
        if (target != 0) {
            uid = target;
        }
        List<Integer> uidList = followService.getFansList(uid);
        if (uidList.isEmpty()) {
            return new AppPageUtils<>(Collections.emptyList(), 0, 20,currPage);
        }
        Page<AppUserEntity> page = new Page<>(currPage, 15);
        QueryWrapper<AppUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(AppUserEntity::getUid, uidList);
        Page<AppUserEntity> pageInfo = this.page(page, queryWrapper);

        List<AppUserEntity> userList = pageInfo.getRecords();
        List<UserInfoResponse> responseList = new ArrayList<>();
        Integer finalUid = uid;
        userList.forEach(userInfo -> {
            UserInfoResponse userInfoResponse = new UserInfoResponse();
            BeanUtils.copyProperties(userInfo, userInfoResponse);
            Integer follow = followService.isFollow(finalUid, userInfoResponse.getUid());
            userInfoResponse.setHasFollow(follow);
            userInfoResponse.setMobile(WechatUtil.maskMobile(userInfoResponse.getMobile()));
            responseList.add(userInfoResponse);
        });
        return new AppPageUtils<>(pageInfo, responseList);
    }

    @Override
    public AppPageUtils<UserInfoResponse> follow(Integer currPage, Integer uid, AppUserEntity user) {
        if (uid == 0) {
            uid = user.getUid();
        }
        List<FollowEntity> list = followService.lambdaQuery().eq(FollowEntity::getUid, uid).list();
        List<Integer> followUidList = list.stream().map(FollowEntity::getFollowUid).collect(Collectors.toList());
        if (followUidList.isEmpty()) {
            return new AppPageUtils<>(Collections.emptyList(), 0, 20,currPage);
        }
        Page<AppUserEntity> page = new Page<>(currPage, 15);
        QueryWrapper<AppUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(AppUserEntity::getUid, followUidList);
        Page<AppUserEntity> pageInfo = this.page(page, queryWrapper);

        List<AppUserEntity> userList = pageInfo.getRecords();
        List<UserInfoResponse> responseList = new ArrayList<>();
        userList.forEach(userInfo -> {
            UserInfoResponse userInfoResponse = new UserInfoResponse();
            BeanUtils.copyProperties(userInfo, userInfoResponse);
            Integer follow = followService.isFollow(user.getUid(), userInfoResponse.getUid());
            userInfoResponse.setHasFollow(follow);
            userInfoResponse.setMobile(WechatUtil.maskMobile(userInfoResponse.getMobile()));
            responseList.add(userInfoResponse);
        });
        return new AppPageUtils<>(pageInfo, responseList);
    }

    @Override
    public void ban(Integer id) {
        Integer status = this.lambdaQuery().eq(AppUserEntity::getUid, id).one().getStatus();
        if (status == 1) {
            throw new LinfengException("该账号已被禁用");
        }
        UpdateWrapper<AppUserEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("status", 1);
        updateWrapper.eq("uid", id);
        update(updateWrapper);
        redisUtils.delete(RedisKeys.getUserCacheKey(id));
    }

    @Override
    public void openBan(Integer id) {
        Integer status = this.lambdaQuery().eq(AppUserEntity::getUid, id).one().getStatus();
        if (status == 0) {
            throw new LinfengException("该账号已解除禁用");
        }
        UpdateWrapper<AppUserEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("status", 0);
        updateWrapper.eq("uid", id);
        update(updateWrapper);
        redisUtils.delete(RedisKeys.getUserCacheKey(id));
    }

    @Override
    public String sendSmsCode(SendCodeForm request) {
        String code = RandomUtil.randomNumbers(Constant.SMS_SIZE);
        String codeKey = WechatUtil.getMobileCodeKey(request.getMobile());
        String s = redisUtils.get(codeKey);
        if (ObjectUtil.isNotNull(s)) {
            return s;
        }
        redisUtils.set(codeKey, code, 300);
        return code;
    }

    @Override
    public String sendEmailCode(SendCodeForm request) {
        String code = RandomUtil.randomNumbers(Constant.SMS_SIZE);
        String codeKey = WechatUtil.getMobileCodeKey(request.getEmail());
        String s = redisUtils.get(codeKey);
        if (ObjectUtil.isNotNull(s)) {
            return s;
        }
        redisUtils.set(codeKey, code, 300);
        return code;
    }

    /**
     * 手机号登录和邮箱登录
     * @param form
     * @param ip
     * @return
     */
    @Override
    public Integer smsLogin(SmsLoginForm form, String ip) {
        if (!WechatUtil.isEmpty(form.getMobile())) {
            AppUserEntity appUserEntity = this.lambdaQuery().eq(AppUserEntity::getMobile, form.getMobile()).one();
            String codeKey = WechatUtil.getMobileCodeKey(form.getMobile());
            String s = redisUtils.get(codeKey);
            if (WechatUtil.isEmpty(s)) {
                throw new LinfengException("请先发送验证码");
            }
            if (!s.equals(form.getCode())) {
                throw new LinfengException("验证码错误！");
            }
            if (appUserEntity != null) {
                checkUserCanLogin(appUserEntity);
                this.vipExpirationCheck(appUserEntity);
                systemService.saveUserLoginIp(appUserEntity, ip);
                return appUserEntity.getUid();
            } else {
                List<String> list = new ArrayList<>();
                list.add(Constant.DEAULT_TAG);
                AppUserEntity appUser = new AppUserEntity();
                appUser.setMobile(form.getMobile());
                appUser.setAvatar(configService.getValue(Constant.DEFAULT_HEAD));
                appUser.setGender(GenderStatus.UNKNOWN.getValue());
                appUser.setUsername(WechatUtil.generateRandomName());
                appUser.setTagStr(JSON.toJSONString(list));
                appUser.setCreateTime(DateUtil.nowDateTime());
                appUser.setUpdateTime(DateUtil.nowDateTime());
                baseMapper.insert(appUser);
                AppUserEntity appUsers = this.lambdaQuery().eq(AppUserEntity::getMobile, form.getMobile()).one();
                //新用户默认加入官方圈子
                topicService.joinTopic(Constant.OFFICIAL_TOPIC_ID, appUsers);
                systemService.saveUserLoginIp(appUsers, ip);

                return appUsers.getUid();
            }
        } else if (!WechatUtil.isEmpty(form.getEmail())) {
            AppUserEntity appUserEntity = this.lambdaQuery().eq(AppUserEntity::getEmail, form.getEmail()).one();
            String codeKey = WechatUtil.getMobileCodeKey(form.getEmail());
            String s = redisUtils.get(codeKey);
            if (WechatUtil.isEmpty(s)) {
                throw new LinfengException("请先发送验证码");
            }
            if (!s.equals(form.getCode())) {
                throw new LinfengException("验证码错误！");
            }
            if (appUserEntity != null) {
                checkUserCanLogin(appUserEntity);
                systemService.saveUserLoginIp(appUserEntity, ip);
                return appUserEntity.getUid();
            } else {
                String value = configService.getValue(Constant.OPEN_EMAIL_REGISTER);
                if(value.equals("0")){
                    throw new LinfengException("邮箱不存在请先注册");
                }else{
                    //自动注册邮箱号并登录(个人运营 如果没有阿里云短信开通权限，那就直接邮箱注册并登录)
                    List<String> list = new ArrayList<>();
                    list.add(Constant.DEAULT_TAG);
                    AppUserEntity appUser = new AppUserEntity();
                    appUser.setMobile(RandomUtil.randomNumbers(11));//随机填充
                    appUser.setEmail(form.getEmail());
                    appUser.setAvatar(configService.getValue(Constant.DEFAULT_HEAD));
                    appUser.setGender(GenderStatus.UNKNOWN.getValue());
                    appUser.setUsername(WechatUtil.generateRandomName());
                    appUser.setTagStr(JSON.toJSONString(list));
                    appUser.setCreateTime(DateUtil.nowDateTime());
                    appUser.setUpdateTime(DateUtil.nowDateTime());
                    baseMapper.insert(appUser);
                    AppUserEntity appUsers = this.lambdaQuery().eq(AppUserEntity::getEmail, form.getEmail()).one();
                    topicService.joinTopic(Constant.OFFICIAL_TOPIC_ID, appUsers);
                    systemService.saveUserLoginIp(appUsers, ip);
                    return appUsers.getUid();
                }
            }
        } else {
            throw new LinfengException("登录参数异常");
        }


    }

    @Override
    public Integer register(SmsLoginForm form, String ip) {
        if (WechatUtil.isEmpty(form.getMobile())) {
            throw new LinfengException("注册账号必须绑定手机号");
        }
        AppUserEntity appUserEntity = this.lambdaQuery().eq(AppUserEntity::getMobile, form.getMobile()).one();
        String codeKey = WechatUtil.getMobileCodeKey(form.getMobile());
        String s = redisUtils.get(codeKey);
        if (WechatUtil.isEmpty(s)) {
            throw new LinfengException("请先发送验证码");
        }
        if (!s.equals(form.getCode())) {
            throw new LinfengException("验证码错误！");
        }
        if (appUserEntity != null) {
            throw new LinfengException("该手机号已注册");
        } else {
            AppUserEntity appUser = new AppUserEntity();
            if (!WechatUtil.isEmpty(form.getEmail())) {
                AppUserEntity userEntity = this.lambdaQuery().eq(AppUserEntity::getEmail, form.getEmail()).one();
                if (userEntity != null) {
                    throw new LinfengException("该邮箱已注册");
                }
                appUser.setEmail(form.getEmail());
            }
            List<String> list = new ArrayList<>();
            list.add(Constant.DEAULT_TAG);
            appUser.setMobile(form.getMobile());
            appUser.setAvatar(configService.getValue(Constant.DEFAULT_HEAD));
            appUser.setGender(GenderStatus.UNKNOWN.getValue());
            appUser.setUsername(WechatUtil.generateRandomName());
            appUser.setTagStr(JSON.toJSONString(list));
            appUser.setCreateTime(DateUtil.nowDateTime());
            appUser.setUpdateTime(DateUtil.nowDateTime());
            baseMapper.insert(appUser);
            AppUserEntity appUsers = this.lambdaQuery().eq(AppUserEntity::getMobile, form.getMobile()).one();
            //新用户默认加入官方圈子
            topicService.joinTopic(Constant.OFFICIAL_TOPIC_ID, appUsers);
            systemService.saveUserLoginIp(appUsers, ip);

            return appUsers.getUid();
        }


    }


    @Override
    public Integer getTotalNum() {
        return this.lambdaQuery().select(AppUserEntity::getUid).count().intValue();
    }

    @Override
    public Integer getRegisterNumByDate(String date) {
        QueryWrapper<AppUserEntity> wrapper = Wrappers.query();
        wrapper.select("uid");
        wrapper.apply("date_format(create_time, '%Y-%m-%d') = {0}", date);
        return userDao.selectCount(wrapper).intValue();
    }

    /**
     * 首页面板数据
     * @return
     */
    @Override
    public HomeRateResponse indexDate() {
        String today = cn.hutool.core.date.DateUtil.date().toString("yyyy-MM-dd");
        String yesterday = cn.hutool.core.date.DateUtil.yesterday().toString("yyyy-MM-dd");
        Integer count = postService.lambdaQuery().eq(PostEntity::getStatus, Constant.POST_REVIEWED).count().intValue();
        Integer postCount = postService.lambdaQuery().select(PostEntity::getId).count().intValue();
        double rechargeMoney = userRechargeService.rechargeMoney();
        double rechargeMoneyByMonth = userRechargeService.rechargeMoneyByMonth();
        Integer userCount = userSignService.getSignUserCount();
        HomeRateResponse response = new HomeRateResponse();
        response.setTotalPostOfReview(count);
        response.setTotalPost(postCount);
        response.setNewUserNum(this.getRegisterNumByDate(today));
        response.setYesterdayNewUserNum(this.getRegisterNumByDate(yesterday));
        response.setTotalUser(this.getTotalNum());
        response.setRechargeMoney(rechargeMoney);
        response.setRechargeMoneyByMonth(rechargeMoneyByMonth);
        response.setUserSignCount(userCount);
        return response;
    }

    /**
     * 本月新增用户
     *
     * @return map
     */
    @Override
    public Map<String, Object> chartCount() {
        Map<String, Object> map = new LinkedHashMap<>();
//        Date month = cn.hutool.core.date.DateUtil.beginOfMonth(new Date());
        Date month = cn.hutool.core.date.DateUtil.lastMonth();
        map.put("chart", userDao.chartList(month));
        return map;
    }

    @Override
    public Map<String, Object> chartPost() {
        Map<String, Object> map = new LinkedHashMap<>();
//        Date month = cn.hutool.core.date.DateUtil.beginOfMonth(new Date());
        Date month = cn.hutool.core.date.DateUtil.lastMonth();
        map.put("postChart", postDao.chartList(month));

        return map;
    }

    /**
     * 本月交易额统计
     * @return
     */
    @Override
    public Map<String, Object> chartMoney() {
        Map<String, Object> map = new LinkedHashMap<>();
        Date month = cn.hutool.core.date.DateUtil.beginOfMonth(new Date());
//        Date month = cn.hutool.core.date.DateUtil.lastMonth();
        map.put("chartMoney", userRechargeDao.chartList(month));

        return map;
    }

    @Override
    public void punishUser(AdminUserPunishParam param) {
        AppUserEntity user = this.getById(param.getUid());
        if (user == null) {
            throw new LinfengException("用户不存在");
        }
        if (param.getResetAvatar() == 1) {
            user.setAvatar(configService.getValue(Constant.DEFAULT_HEAD));
        }
        if (param.getResetIntro() == 1) {
            user.setIntro(Constant.DEAULT_INTRO);
        }
        if (param.getResetUsername() == 1) {
            user.setUsername(WechatUtil.generateRandomName());
        }
        if (param.getResetBg() == 1) {
            user.setBgImg("");
        }
        if (param.getResetPost() == 1) {
            List<PostEntity> list = postService.lambdaQuery()
                    .eq(PostEntity::getUid, param.getUid())
                    .ne(PostEntity::getStatus, Constant.POST_BANNER)
                    .list();
            list.forEach(item -> {
                item.setStatus(Constant.POST_BANNER);
            });
            boolean down = postService.updateBatchById(list);
            if (!down) {
                throw new LinfengException("帖子下架失败");
            }
        } else if (param.getResetPost() == 2) {
            List<PostEntity> list = postService.lambdaQuery()
                    .eq(PostEntity::getUid, param.getUid())
                    .list();
            if (!list.isEmpty()) {
                List<Integer> postIdList = list.stream().map(PostEntity::getId).collect(Collectors.toList());
                boolean remove = postService.removeByIds(postIdList);
                if (!remove) {
                    throw new LinfengException("帖子删除失败");
                }
            }
        }
        this.updateById(user);
        redisUtils.delete(RedisKeys.getUserCacheKey(param.getUid()));
    }

    /**
     * 更新用户余额
     * @param uid
     * @param price
     * @param givePrice
     */
    @Override
    public BigDecimal updateMoney(Integer uid, BigDecimal price, BigDecimal givePrice) {
        AppUserEntity user = this.getById(uid);
        BigDecimal money = user.getMoney().add(price).add(givePrice);
        boolean update = this.lambdaUpdate()
                .eq(AppUserEntity::getUid, uid)
                .set(AppUserEntity::getMoney, money)
                .update();
        if (!update) {
            throw new LinfengException("更新用户余额失败");
        }
        redisUtils.delete(RedisKeys.getUserCacheKey(user.getUid()));
        return money;

    }

    /**
     * 管理端更新用户
     * @param user
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(AdminUserInfoParam user) {
        AppUserEntity appUser = this.getById(user.getUid());
        appUser.setType(user.getType());
        appUser.setStatus(user.getStatus());
        appUser.setAvatar(user.getAvatar());
        appUser.setUsername(user.getUsername());
        appUser.setIntro(user.getIntro());
        appUser.setUpdateTime(DateUtil.nowDateTime());
        if (user.getChangeMoney() == 0) {
            if(user.getChangeValue()==null){
                throw new LinfengException("金额必须填写");
            }
            if (user.getChangeValue().compareTo(BigDecimal.ZERO) <= 0) {
                throw new LinfengException("金额必须大于0");
            }
            if(user.getUpOrDown() == 1&&user.getChangeValue().compareTo(appUser.getMoney())>0){
                throw new LinfengException("用户可扣除余额不足");
            }
            if (user.getUpOrDown() == 0) {
                //增加余额
                appUser.setMoney(appUser.getMoney().add(user.getChangeValue()));
                billService.income(appUser.getUid().longValue(), BillDetailEnum.TYPE_6.getDesc(),
                        BillDetailEnum.CATEGORY_1.getValue(), BillDetailEnum.TYPE_6.getValue(),
                        user.getChangeValue().doubleValue(), appUser.getMoney().doubleValue(),
                        "后台系统增加余额", "");
            } else if (user.getUpOrDown() == 1) {
                //减少余额
                appUser.setMoney(appUser.getMoney().subtract(user.getChangeValue()));
                billService.expend(appUser.getUid().longValue(), BillDetailEnum.TYPE_7.getDesc(),
                        BillDetailEnum.CATEGORY_1.getValue(), BillDetailEnum.TYPE_7.getValue(),
                        user.getChangeValue().doubleValue(), appUser.getMoney().doubleValue(),
                        "后台系统减少余额", "");
            }
        }
        if (user.getChangeIntegral() == 0) {
            if(user.getChangeIntegralValue()==null){
                throw new LinfengException("积分数量必须填写");
            }
            if (user.getChangeIntegralValue()<=0) {
                throw new LinfengException("积分必须大于0");
            }
            if(appUser.getIntegral()< user.getChangeIntegralValue()&&user.getUpOrDownIntegral() == 1){
                throw new LinfengException("用户可扣除积分不足");
            }
            if (user.getUpOrDownIntegral() == 0) {
                //增加积分
                appUser.setIntegral(appUser.getIntegral()+user.getChangeIntegralValue());
                billService.income(appUser.getUid().longValue(), BillDetailEnum.TYPE_6.getDesc(),
                        BillDetailEnum.CATEGORY_2.getValue(), BillDetailEnum.TYPE_6.getValue(),
                        user.getChangeIntegralValue(), appUser.getIntegral(),
                        "后台系统增加积分", "");
            } else if (user.getUpOrDownIntegral() == 1) {
                //减少积分
                appUser.setIntegral(appUser.getIntegral()-user.getChangeIntegralValue());
                billService.expend(appUser.getUid().longValue(), BillDetailEnum.TYPE_7.getDesc(),
                        BillDetailEnum.CATEGORY_2.getValue(), BillDetailEnum.TYPE_7.getValue(),
                        user.getChangeIntegralValue().doubleValue(), appUser.getMoney().doubleValue(),
                        "后台系统减少积分", "");
            }
        }
        if(user.getChangeVip() == 0){
            if(user.getVip()==1){
                //如果设置为会员
                if(user.getVipValidDay()==null){
                    throw new LinfengException("会员有效天数必须填写");
                }
                if(user.getVipValidDay()<=0){
                    throw new LinfengException("会员有效天数必须大于0");
                }
                if(appUser.getVip().equals(Constant.VIP_USER) && ObjectUtil.isNotNull(appUser.getVipExpireTime())){
                    //如果已经是会员，那么在原基础上 续天数
                    String toStr = DateUtil.dateToStr(appUser.getVipExpireTime(), "yyyy-MM-dd HH:mm:ss");
                    appUser.setVipExpireTime(DateUtil.addDay(toStr,user.getVipValidDay()));
                }else{
                    //如果不是会员
                    appUser.setVipExpireTime(DateUtil.addDay(DateUtil.nowDateTimeStr(),user.getVipValidDay()));
                }
                appUser.setVip(Constant.VIP_USER);

            }else{
                //如果不设置为会员
                appUser.setVip(Constant.COMMON_USER);
            }
        }
        boolean b = this.updateById(appUser);
        if (!b) {
            throw new LinfengException("更新失败");
        }
        redisUtils.delete(RedisKeys.getUserCacheKey(appUser.getUid()));

    }

    @Override
    public List<AppUserEntity> getBatchUser(List<Integer> uid) {
        return userDao.getBatchUser(uid);
    }



    /**
     * 1.检查用户会员是否过期
     * 2.检查用户等级LV
     * @param user
     */
    @Override
    public AppUserEntity vipExpirationCheck(AppUserEntity user) {
        if (user.getVip().equals(Constant.VIP_USER)) {
            boolean isBefore = user.getVipExpireTime().before(DateUtil.nowDateTime());
            if (isBefore) {
                user.setVip(Constant.COMMON_USER);
                this.saveOrUpdate(user);
            }
        }
        //更新用户等级
        userLevelService.updateUserLevel(user);
        return user;
    }

    /**
     * 热门博主
     * 加缓存
     * @return
     */
    @Override
    public List<AppHotUserResponse> getHotUserList() {
        String res = redisUtils.get(ConfigConstant.HOT_USER_KEY);
        AppUserEntity user = localUser.getUser();
        boolean isNull = ObjectUtil.isNull(user);
        if (WechatUtil.isEmpty(res)) {
            //查询近一个月粉丝增长最快的用户
            DateTime dateTime = cn.hutool.core.date.DateUtil.lastMonth();
            List<HotUserResponse> list = followDao.getHotUserList(dateTime);
            if (list.isEmpty()) {
                return new ArrayList<>();
            }
            List<AppHotUserResponse> result = new ArrayList<>();
            list.forEach(item -> {
                AppHotUserResponse response = new AppHotUserResponse();
                BeanUtils.copyProperties(this.getById(item.getUid()), response);
                response.setFans(followService.getFans(item.getUid()));
                response.setPostNum(postService.getPostNumberByUid(item.getUid()));
                if (isNull) {
                    response.setIsFollow(false);
                } else {
                    response.setIsFollow(followService.isFollowOrNot(user.getUid(), item.getUid()));
                }
                result.add(response);
            });
            redisUtils.set(ConfigConstant.HOT_USER_KEY, JSON.toJSON(result).toString(), 60 * 30);
            return result;
        }
        List<AppHotUserResponse> list = JSONObject.parseArray(res, AppHotUserResponse.class);
        //判断用户是否关注
        Map<Integer, Integer> followCollectMap = new HashMap<>();
        if (!isNull) {
            List<Integer> uidList = list.stream().map(AppHotUserResponse::getUid).collect(Collectors.toList());
            List<FollowBatchResponse> followBatch = followService.findFollowBatch(uidList, user.getUid());
            followCollectMap = followBatch.stream().collect(Collectors.toMap(FollowBatchResponse::getFollowUid, FollowBatchResponse::getId));
        }
        Map<Integer, Integer> finalFollowCollectMap = followCollectMap;
        list.forEach(item -> {
            if (isNull) {
                item.setIsFollow(false);
            } else {
                if (ObjectUtil.isNotNull(finalFollowCollectMap.get(item.getUid()))) {
                    item.setIsFollow(true);
                } else {
                    item.setIsFollow(false);
                }
            }
        });
        return list;

    }

    /**
     * 微信小程序绑定手机号
     * @param param
     * @param ip
     * @return
     */
    @Override
    public Integer bindWxPhone(LoginPhoneParam param, String ip) {
        Map<String, Object> map = WechatUtil.getPhoneNumber(param);
        if (WechatUtil.isEmpty(map)) {
            throw new LinfengException("微信授权失败");
        }
        String phone = "";
        Object phoneNumber = map.get("param");
        String jsonString = JSONObject.toJSONString(phoneNumber);
        JSONObject obj = JSONObject.parseObject(jsonString);
        if (!WechatUtil.strIsEmpty(jsonString)) {
            phone = obj.get("phoneNumber").toString();
        }
        AppUserEntity target = this.lambdaQuery().eq(AppUserEntity::getOpenid, param.getWechatOpenId()).one();
        //如果用户手机号之前未注册过，那么正常绑定即可
        if (target == null) {
            //注册本用户
            List<String> list = new ArrayList<>();
            list.add(Constant.DEAULT_TAG);
            AppUserEntity appUser = new AppUserEntity();
            appUser.setMobile(phone);
            appUser.setOpenid(param.getWechatOpenId());
            appUser.setAvatar(configService.getValue(Constant.DEFAULT_HEAD));
            appUser.setGender(GenderStatus.UNKNOWN.getValue());
            appUser.setUsername(WechatUtil.generateRandomName());
            appUser.setTagStr(JSON.toJSONString(list));
            appUser.setCreateTime(DateUtil.nowDateTime());
            appUser.setUpdateTime(DateUtil.nowDateTime());
            baseMapper.insert(appUser);
            AppUserEntity appUsers = this.lambdaQuery().eq(AppUserEntity::getMobile, phone).one();
            topicService.joinTopic(Constant.OFFICIAL_TOPIC_ID, appUser);//新用户默认加入官方圈子
            systemService.saveUserLoginIp(appUsers, ip);
            return appUsers.getUid();
        } else {
            if(WechatUtil.isEmpty(target.getMoney())){
                //已注册用户但是未绑定手机号
                target.setMobile(phone);
            }
            checkUserCanLogin(target);
            this.vipExpirationCheck(target);
            target.setOpenid(param.getWechatOpenId());
            target.setUpdateTime(DateUtil.nowDateTime());
            this.saveOrUpdate(target);
            redisUtils.delete(RedisKeys.getUserCacheKey(target.getUid()));
            systemService.saveUserLoginIp(target, ip);
            return target.getUid();
        }


    }


    @Override
    public void removeTopic(RemoveTopicForm request, AppUserEntity user) {
        TopicEntity topic = topicService.getById(request.getTopicId());
        if (!topic.getUid().equals(user.getUid())) {
            if (!topicAdminService.isAdmin(user.getUid(), request.getTopicId())) {
                throw new LinfengException("你不是管理员没有权限");
            }
        }
        if (request.getId().equals(topic.getUid())) {
            throw new LinfengException("圈主无法移除");
        }
        if (topicAdminService.isAdmin(request.getId(), request.getTopicId())) {
            throw new LinfengException("请先解除其圈子管理员权限");
        }
        LambdaQueryWrapper<UserTopicEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTopicEntity::getUid, request.getId());
        wrapper.eq(UserTopicEntity::getTopicId, request.getTopicId());
        userTopicService.remove(wrapper);
    }

    @Override
    public Map<String, Object> getUserLevelInfo(AppUserEntity user) {
        Map<String, Object> map = new HashMap<>();
        List<UserLevelEntity> list = userLevelService.getList();
        Integer integral = user.getIntegral();
        int tmp=list.size();
        Integer userLevel = list.get(list.size() - 1).getLevelId();
        for (int i = 0; i < list.size(); i++) {
            if (integral < list.get(i).getMaxNum()) {
                userLevel = list.get(i).getLevelId();
                tmp = i;
                break;
            }
        }
        if (!user.getLevel().equals(userLevel)) {
            boolean update = this.lambdaUpdate()
                    .set(AppUserEntity::getLevel, userLevel)
                    .eq(AppUserEntity::getUid, user.getUid())
                    .update();
            if (!update) {
                throw new LinfengException("用户等级信息更新失败");
            }
            redisUtils.delete(RedisKeys.getUserCacheKey(user.getUid()));
        }

        UserLevelEntity userLevelLast = list.get(list.size() - 1);
        Integer lastLevelId = userLevelLast.getLevelId();
        if (userLevel.equals(lastLevelId)) {
            UserLevelEntity userLevelEntity = list.get(list.size()-1);
            map.put("minNum", userLevelEntity.getMinNum());
            map.put("maxNum", userLevelEntity.getMaxNum());
            map.put("needCount", 0);
            map.put("rate", 100);
        } else {
            UserLevelEntity userLevelEntity = list.get(tmp);
            map.put("needCount", userLevelEntity.getMaxNum()-user.getIntegral());
            map.put("minNum", userLevelEntity.getMinNum());
            map.put("maxNum", userLevelEntity.getMaxNum());
            double result= (double)(user.getIntegral() - userLevelEntity.getMinNum()) / (userLevelEntity.getMaxNum() - userLevelEntity.getMinNum());
            double roundedResult = Math.round(result * 100.0);
            map.put("rate", roundedResult);
        }
        map.put("userLevelId", userLevel);
        map.put("lastLevelId", lastLevelId);


        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindMpOpenid(AppUserEntity user, WxMpLoginForm request) {
        String openid = getOpenidByWxMpCode(request.getCode());
        //检查openid是否被其他用户绑定过
        AppUserEntity beforeUser = this.lambdaQuery().eq(AppUserEntity::getMpOpenid, openid).one();
        if(beforeUser!=null){
            //如果openid被绑定过,则清空之前用户openid,把openid给当前用户绑定
            beforeUser.setMpOpenid("");
            this.updateById(beforeUser);
            redisUtils.delete(RedisKeys.getUserCacheKey(beforeUser.getUid()));
        }
        user.setMpOpenid(openid);
        user.setUpdateTime(DateUtil.nowDateTime());
        this.updateById(user);
        redisUtils.delete(RedisKeys.getUserCacheKey(user.getUid()));

    }

    @Override
    public Integer loginByWxMp(WxMpLoginForm param, String ip) {
        String openid = getOpenidByWxMpCode(param.getCode());
        AppUserEntity appUser = this.lambdaQuery().eq(AppUserEntity::getMpOpenid, openid).one();
        if(appUser==null){
            return 0;
        }
        checkUserCanLogin(appUser);
        if (WechatUtil.isEmpty(appUser.getMobile())) {
            return 0;
        }
        this.vipExpirationCheck(appUser);
        redisUtils.delete(RedisKeys.getUserCacheKey(appUser.getUid()));
        systemService.saveUserLoginIp(appUser, ip);
        return appUser.getUid();
    }

    /**
     * 邮箱校验
     * @param form
     */
    @Override
    public void bindEmail(SmsLoginForm form) {
        AppUserEntity user = localUser.getUser();
        AppUserEntity users = this.getById(user.getUid());
        String codeKey = WechatUtil.getMobileCodeKey(form.getEmail());
        String s = redisUtils.get(codeKey);
        if (WechatUtil.isEmpty(s)) {
            throw new LinfengException("请先发送验证码");
        }
        if (!s.equals(form.getCode())) {
            throw new LinfengException("验证码错误！");
        }
        AppUserEntity userInfo = this.lambdaQuery()
                .eq(AppUserEntity::getEmail, form.getEmail())
                .ne(AppUserEntity::getUid,user.getUid())
                .one();
        if(userInfo!=null){
            throw new LinfengException("邮箱已被占用！");
        }
        users.setEmail(form.getEmail());
        baseMapper.updateById(users);
        redisUtils.delete(RedisKeys.getUserCacheKey(user.getUid()));

    }

    /**
     * 查询近一周发帖活跃用户
     * @return 发帖达人列表
     */
    @Override
    public List<AppUserRankResponse> userRank() {
//        DateTime week = cn.hutool.core.date.DateUtil.beginOfWeek(new Date());
        DateTime week = cn.hutool.core.date.DateUtil.lastWeek();//近一周

        List<PostEntity> postList = postService.lambdaQuery()
                .eq(PostEntity::getStatus,Constant.POST_NORMAL)
                .gt(PostEntity::getCreateTime, week)
                .list();
        if(postList.isEmpty()){
            return new ArrayList<>();
        }
        Map<Integer, Long> collect = postList.stream().collect(Collectors.groupingBy(PostEntity::getUid, Collectors.counting()));
        Map<Integer, Long> sorted = collect
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(comparingByValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                        LinkedHashMap::new));
        List<Integer> userIdList = new ArrayList<>(sorted.keySet());
        List<AppUserEntity> batchUser = userDao.getBatchUser(userIdList);
        Map<Integer, AppUserEntity> userMap = batchUser.stream()
                .collect(Collectors.toMap(AppUserEntity::getUid, Function.identity()));
        List<AppUserRankResponse> list=new ArrayList<>();
        sorted.forEach((k,v)->{
            AppUserEntity appUser = userMap.get(k);
            if(appUser!=null){
                AppUserRankResponse response=new AppUserRankResponse();
                BeanUtils.copyProperties(appUser,response);
                response.setPostNumber(v.intValue());
                list.add(response);
            }
        });
        return list;
    }

    /**
     * 第三方应用携带参数请求获取token实现自动登录
     * 手机号是用户唯一标识，必须要携带该参数请求，其他参数可以没有
     * @param form
     * @param ip
     * @return
     */
    @Override
    public Integer getThirdLoginToken(UserLoginForm form, String ip) {
        if(!open){
            throw new LinfengException("第三方应用请求登录接口未开启");
        }
        if(!form.getSecret().equals(secret)){
            throw new LinfengException("请求密钥错误");
        }
        if(WechatUtil.isEmpty(form.getThirdPhone())){
            throw new LinfengException("手机号不能为空");
        }
        AppUserEntity appUserEntity = this.lambdaQuery().eq(AppUserEntity::getMobile, form.getThirdPhone()).one();
        if (appUserEntity != null) {
            checkUserCanLogin(appUserEntity);
            if(!WechatUtil.isEmpty(form.getThirdName())){
                appUserEntity.setUsername(form.getThirdName());
            }
            if(!WechatUtil.isEmpty(form.getAvatar())){
                appUserEntity.setAvatar(form.getAvatar());
            }
            if(!WechatUtil.isEmpty(form.getRemark())){
                appUserEntity.setIntro(form.getRemark());
            }
            if(!WechatUtil.isEmpty(form.getGender())){
                appUserEntity.setGender(form.getGender());
            }
            this.updateById(appUserEntity);
            this.vipExpirationCheck(appUserEntity);
            systemService.saveUserLoginIp(appUserEntity, ip);
            return appUserEntity.getUid();
        } else {
            List<String> list = new ArrayList<>();
            list.add(Constant.DEAULT_TAG);
            AppUserEntity appUser = new AppUserEntity();
            appUser.setMobile(form.getThirdPhone());
            if(!WechatUtil.isEmpty(form.getGender())){
                appUser.setGender(form.getGender());
            }else{
                appUser.setGender(GenderStatus.UNKNOWN.getValue());
            }
            if(!WechatUtil.isEmpty(form.getAvatar())){
                appUser.setAvatar(form.getAvatar());
            }else{
                appUser.setAvatar(configService.getValue(Constant.DEFAULT_HEAD));
            }
            if(!WechatUtil.isEmpty(form.getThirdName())){
                appUser.setUsername(form.getThirdName());
            }else{
                appUser.setUsername(WechatUtil.generateRandomName());
            }
            if(!WechatUtil.isEmpty(form.getRemark())){
                appUser.setIntro(form.getRemark());
            }
            if(!WechatUtil.isEmpty(form.getGender())){
                appUser.setGender(form.getGender());
            }
            appUser.setTagStr(JSON.toJSONString(list));
            appUser.setCreateTime(DateUtil.nowDateTime());
            appUser.setUpdateTime(DateUtil.nowDateTime());
            baseMapper.insert(appUser);
            AppUserEntity appUsers = this.lambdaQuery().eq(AppUserEntity::getMobile, form.getThirdPhone()).one();
            //新用户默认加入官方圈子
            topicService.joinTopic(Constant.OFFICIAL_TOPIC_ID, appUsers);
            systemService.saveUserLoginIp(appUsers, ip);

            return appUsers.getUid();
        }
    }

    @Override
    public void checkVipExpirationBatch() {
        List<AppUserEntity> list = this.lambdaQuery()
                .eq(AppUserEntity::getVip, Constant.VIP_USER)
                .lt(AppUserEntity::getVipExpireTime, DateUtil.nowDateTime())
                .list();
        if(list.size()>0){
            for (AppUserEntity userEntity : list) {
                userEntity.setVip(Constant.COMMON_USER);
            }
            this.updateBatchById(list);
        }
    }


    /**
     * 账户注销
     * @param user
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAccount(AppUserEntity user) {
        user.setAvatar(configService.getValue(Constant.DEFAULT_HEAD));
        user.setIntro(Constant.DEAULT_INTRO);
        user.setUsername(Constant.DELETE_ACCOUNT_NAME);
        user.setBgImg("");
        user.setStatus(Constant.USER_DELETE);
        List<PostEntity> list = postService.lambdaQuery()
                .eq(PostEntity::getUid, user.getUid())
                .list();
        if (!list.isEmpty()) {
            List<Integer> postIdList = list.stream().map(PostEntity::getId).collect(Collectors.toList());
            boolean remove = postService.removeByIds(postIdList);
            if (!remove) {
                throw new LinfengException("帖子删除失败");
            }
        }
        this.updateById(user);
        redisUtils.delete(RedisKeys.getUserCacheKey(user.getUid()));
    }

    /**
     * 获取用户公众号openid
     * @param code code码
     * @return
     */
    String getOpenidByWxMpCode(String code){
        String appId = configService.getValue(Constant.WX_MP_ID);
        if(WechatUtil.isEmpty(appId)){
            throw new LinfengException("公众号AppId未配置");
        }
        String secret = configService.getValue(Constant.WX_MP_Secret);
        if(WechatUtil.isEmpty(secret)){
            throw new LinfengException("公众号密钥未配置");
        }
        String url = StrUtil.format(Constant.WECHAT_OAUTH2_ACCESS_TOKEN_URL, appId, secret, code);
        JSONObject data = restTemplateUtil.getData(url);
        if (ObjectUtil.isNull(data)) {
            throw new LinfengException("微信公众平台请求accesstoken失败");
        }
        if (data.containsKey("errcode") && !data.getString("errcode").equals("0")) {
            if (data.containsKey("errmsg")) {
                throw new LinfengException("微信接口错误：" + data.getString("errcode") + data.getString("errmsg"));
            }
        }
        String openid = data.getString("openid");
        if(WechatUtil.isEmpty(openid)){
            throw new LinfengException("openid获取失败");
        }
        return openid;
    }

    /**
     * 检查用户是否被封禁或注销
     * @param user
     */
    private void checkUserCanLogin(AppUserEntity user){
        if(user.getStatus().equals(Constant.BAN)){
            log.info("封号用户{}尝试登录",user.getUid());
            throw new LinfengException(Constant.USER_BAN_MSG,Constant.USER_BAN_CODE);
        }else if(user.getStatus().equals(Constant.USER_DELETE)){
            log.info("注销用户{}尝试登录",user.getUid());
            throw new LinfengException(Constant.USER_DELETE_MSG);
        }
    }

}