package io.linfeng.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.utils.AppPageUtils;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.vo.admin.HomeRateResponse;
import io.linfeng.common.vo.admin.UserInfoResponse;
import io.linfeng.common.vo.app.*;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.param.AdminUserInfoParam;
import io.linfeng.modules.admin.param.AdminUserPunishParam;
import io.linfeng.modules.app.param.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-20 12:10:43
 */
public interface AppUserService extends IService<AppUserEntity> {

    PageUtils queryPage(Map<String, Object> params);

    AppPageUtils<UserInfoResponse> findTopicUserPage(TopicUserForm form);

    AppPageUtils<TopicUserShortResponse> findUserPageByTopicAdmin(TopicUserForm form, AppUserEntity user);

    AppPageUtils<AppUserShortInfoResponse> blockUserList(TopicUserForm form,AppUserEntity user);

    AppUserResponse getUserInfo(AppUserEntity user);

    Integer wxLogin(WxLoginForm form,String ip);

    void updateAppUserInfo(AppUserUpdateForm appUserUpdateForm, AppUserEntity user);

    void addFollow(AddFollowForm request, AppUserEntity user);

    void cancelFollow(AddFollowForm request, AppUserEntity user);

    AppUserInfoResponse findUserInfoById(Integer uid);

    AppPageUtils<UserInfoResponse> search(Integer page, String keyword, Integer uid);

    AppPageUtils<UserInfoResponse> userFans(Integer page, Integer target, Integer uid);

    AppPageUtils<UserInfoResponse> follow(Integer page, Integer uid, AppUserEntity user);

    void ban(Integer id);

    void openBan(Integer id);

    String sendSmsCode(SendCodeForm request);

    String sendEmailCode(SendCodeForm request);

    Integer smsLogin(SmsLoginForm form, String ip);

    Integer register(SmsLoginForm form, String ip);

    Integer getTotalNum();

    /**
     * 根据日期获取注册用户数量
     * @param date 日期
     * @return Integer
     */
    Integer getRegisterNumByDate(String date);

    /**
     * 首页数据
     * @return HomeRateResponse
     */
    HomeRateResponse indexDate();
    /**
     * 本月新增用户
     * @return map
     */
    Map<String,Object> chartCount();

    BigDecimal updateMoney(Integer uid, BigDecimal price, BigDecimal givePrice);

    void updateUser(AdminUserInfoParam user);

    List<AppUserEntity> getBatchUser(List<Integer> uid);

    AppUserEntity vipExpirationCheck(AppUserEntity user);

    List<AppHotUserResponse> getHotUserList();

    Integer bindWxPhone(LoginPhoneParam param,String ip);

    Map<String,Object>  chartPost();

    Map<String,Object>  chartMoney();

    void punishUser(AdminUserPunishParam param);

    void removeTopic(RemoveTopicForm request, AppUserEntity user);

    Map<String,Object> getUserLevelInfo(AppUserEntity user);

    void bindMpOpenid(AppUserEntity user, WxMpLoginForm request);

    Integer loginByWxMp(WxMpLoginForm param, String ip);

    void bindEmail(SmsLoginForm form);

    List<AppUserRankResponse> userRank();

    Integer getThirdLoginToken(UserLoginForm form, String ip);

    void checkVipExpirationBatch();

    void deleteAccount(AppUserEntity user);
}

