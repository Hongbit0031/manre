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
package io.linfeng.modules.app.controller;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import io.linfeng.common.annotation.Limit;
import io.linfeng.common.annotation.NoRepeatSubmit;
import io.linfeng.common.exception.LinfengException;
import io.linfeng.common.utils.*;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.vo.admin.UserInfoResponse;
import io.linfeng.common.vo.app.*;
import io.linfeng.common.vo.app.AppSystemMessageResponse;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.entity.RechargeEntity;
import io.linfeng.modules.admin.service.ActiveUserService;
import io.linfeng.modules.admin.service.AppUserService;
import io.linfeng.modules.admin.service.MessageService;
import io.linfeng.modules.admin.service.RechargeService;
import io.linfeng.modules.app.annotation.Login;
import io.linfeng.modules.app.annotation.LoginUser;
import io.linfeng.modules.app.param.*;
import io.linfeng.modules.app.service.UserSettingService;
import io.linfeng.modules.app.utils.JwtUtils;
import io.linfeng.modules.app.utils.LocalUser;
import io.linfeng.modules.app.utils.SmsUtils;
import io.linfeng.modules.app.utils.tecent.TencentCloudSmsTransport;
import io.linfeng.modules.sys.service.SysConfigService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户登录授权
 *
 */
@RestController
@RequestMapping("/app/user")
@Tag(name = "移动端——用户模块")
@Slf4j
public class AppUserInfoController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private RechargeService rechargeService;

    @Autowired
    private SmsUtils smsUtils;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private SysConfigService configService;

    @Autowired
    JavaMailSenderImpl mailSender;

    @Autowired
    private UserSettingService userSettingService;

    @Autowired
    private TencentCloudSmsTransport tencentCloudSmsTransport;

    @Autowired
    private ActiveUserService activeUserService;

    @Autowired
    private LocalUser localUser;

    @Autowired
    private MessageService messageService;

    @Value("${linfeng.email.send}")
    private String send;


    /**
     * 微信小程序登录
     * 该接口用于已绑定过手机号的账户登录
     */
    @PostMapping("/miniWxLogin")
    @Operation(summary = "微信小程序登录")
    public R miniWxLogin(@RequestBody WxLoginForm form, HttpServletRequest request){
        //表单校验
        ValidatorUtils.validateEntity(form);

        //用户登录
        String ip = IPUtil.getIp(request);
        Integer userId = appUserService.wxLogin(form,ip);

        //生成token
        String token = jwtUtils.generateToken(userId);

        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("expire", jwtUtils.getExpire());

        return R.ok(map);
    }

    /**
     * 发送验证码
     * 可配置是否开启验证码
     */
    @Limit(count = 2)
    @PostMapping("/sendSmsCode")
    @Operation(summary = "发送验证码")
    public R sendSmsCode(@RequestBody SendCodeForm request){

        String code=appUserService.sendSmsCode(request);
        String smsOpen = configService.getValue(Constant.SMS_OPEN);
        if(smsOpen.equals("0")){
            String chooseSms = configService.getValue(Constant.CHOOSE_SMS);
            if(chooseSms.equals("0")){
                //发送阿里云短信
                JSONObject json = new JSONObject();
                json.put("code",code);
                try {
                    smsUtils.sendSms(request.getMobile(),json.toJSONString());
                } catch (ClientException e) {
                    String codeKey = WechatUtil.getMobileCodeKey(request.getMobile());
                    redisUtils.delete(codeKey);
                    e.printStackTrace();
                }
                return R.ok("发送成功,注意查收");
            }else{
                //发送腾讯云短信
                tencentCloudSmsTransport.sendSmsCode(request.getMobile(),code);
                return R.ok("发送成功,注意查收");
            }
        }
        return R.ok("演示验证码:"+code);
    }

    /**
     * h5登录发送验证码 先判断该手机号是否注册
     * 可配置是否开启验证码
     */
    @Limit(count = 8)
    @PostMapping("/sendLoginSmsCode")
    @Operation(summary = "发送验证码判断手机号是否注册")
    public R sendLoginSmsCode(@RequestBody SendCodeForm request){
        AppUserEntity user = appUserService.lambdaQuery().eq(AppUserEntity::getMobile, request.getMobile()).one();
        if(ObjectUtil.isNull(user)){
            return R.error("该手机号未注册");
        }else{
            if(user.getStatus().equals(Constant.BAN)){
                log.info("封号用户{}尝试登录",user.getUid());
                return R.error(Constant.USER_NO_LOGIN_CODE,Constant.USER_BAN_MSG);
            }else if(user.getStatus().equals(Constant.USER_DELETE)){
                log.info("注销用户{}尝试登录",user.getUid());
                return R.error(Constant.USER_NO_LOGIN_CODE,Constant.USER_DELETE_MSG);
            }
        }
        String code=appUserService.sendSmsCode(request);
        String smsOpen = configService.getValue(Constant.SMS_OPEN);
        if(smsOpen.equals("0")){
            String chooseSms = configService.getValue(Constant.CHOOSE_SMS);
            if(chooseSms.equals("0")){
                //发送阿里云短信
                JSONObject json = new JSONObject();
                json.put("code",code);
                try {
                    smsUtils.sendSms(request.getMobile(),json.toJSONString());
                } catch (ClientException e) {
                    String codeKey = WechatUtil.getMobileCodeKey(request.getMobile());
                    redisUtils.delete(codeKey);
                    e.printStackTrace();
                }
                return R.ok("发送成功,注意查收");
            }else{
                //发送腾讯云短信
                tencentCloudSmsTransport.sendSmsCode(request.getMobile(),code);
                return R.ok("发送成功,注意查收");
            }


        }
        return R.ok("演示验证码:"+code);
    }

    /**
     * 发送邮箱登录验证码
     *
     */
    @Limit(count = 8)
    @PostMapping("/sendEmailCode")
    @Operation(summary = "发送邮箱登录验证码")
    public R sendEmailCode(@RequestBody SendCodeForm request){

        String code=appUserService.sendEmailCode(request);
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();//创建一个简单的邮件信息对象
            //设置邮件发送信息的内容
            mailMessage.setSubject("慢热验证码");//标题
            mailMessage.setText("您的验证码为:" + code);//内容
            mailMessage.setFrom(send);//内容为发送方的邮箱地址
            mailMessage.setTo(request.getEmail());//内容为接收方邮箱地址
            mailSender.send(mailMessage);//发送邮件
        } catch (Exception e){
            return R.error("输入邮箱号非法");
        }

        return R.ok("发送成功,注意查收");
    }


    /**
     * 手机验证码登录
     */
    @PostMapping("/smsLogin")
    @Operation(summary = "手机验证码登录")
    public R smsLogin(@RequestBody SmsLoginForm form, HttpServletRequest request){

        //用户登录
        String ip = IPUtil.getIp(request);
        Integer userId = appUserService.smsLogin(form,ip);

        //生成token
        String token = jwtUtils.generateToken(userId);

        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("expire", jwtUtils.getExpire());

        return R.ok(map);
    }

    /**
     * H5端注册并登录
     *
     */
    @PostMapping("/register")
    @Operation(summary = "H5端注册")
    public R register(@RequestBody SmsLoginForm form, HttpServletRequest request){

        //用户注册并登录
        String ip = IPUtil.getIp(request);
        Integer userId = appUserService.register(form,ip);

        //生成token
        String token = jwtUtils.generateToken(userId);

        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("expire", jwtUtils.getExpire());

        return R.ok(map);
    }

    @Login
    @PostMapping("/bindEmail")
    @Operation(summary = "邮箱绑定")
    public Result bindEmail(@RequestBody SmsLoginForm form){
        appUserService.bindEmail(form);
        return new Result();
    }

    @Login
    @GetMapping("/userInfo")
    @Operation(summary = "获取用户信息")
    public Result<AppUserResponse> userInfo(@Parameter(hidden = true) @LoginUser AppUserEntity user){
        AppUserResponse response=appUserService.getUserInfo(user);

        return new Result<AppUserResponse>().ok(response);
    }


    @Login
    @GetMapping("userId")
    @Operation(summary = "获取用户ID")
    public R userInfo(@RequestAttribute("userId") Integer userId){
        return R.ok().put("userId", userId);
    }


    @Login
    @GetMapping("userSetting")
    @Operation(summary = "获取用户隐私设置")
    public Result<AppUserSettingResponse> userSetting(@Parameter(hidden = true) @LoginUser AppUserEntity user){
        AppUserSettingResponse response=userSettingService.userSetting(user.getUid());
        return new Result<AppUserSettingResponse>().ok(response);
    }


    @Login
    @PostMapping("updateUserSetting")
    @NoRepeatSubmit
    @Operation(summary = "更新用户隐私设置")
    public Result updateUserSetting(@Parameter(hidden = true) @LoginUser AppUserEntity user,@RequestBody UpdateUserSettingForm param){
        userSettingService.updateUserSetting(user.getUid(),param);
        return new Result().okMessage("修改成功");
    }

    @Login
    @PostMapping("/userInfoEdit")
    @Operation(summary = "用户修改个人信息")
    public Result userInfoEdit(@Parameter(hidden = true) @LoginUser AppUserEntity user, @RequestBody AppUserUpdateForm appUserUpdateForm){
        ValidatorUtils.validateEntity(appUserUpdateForm);
        appUserService.updateAppUserInfo(appUserUpdateForm,user);
        return new Result().okMessage("修改成功");
    }


    @Login
    @PostMapping("/addFollow")
    @Operation(summary = "关注用户")
    @NoRepeatSubmit
    public Result addFollow(@Parameter(hidden = true) @LoginUser AppUserEntity user, @RequestBody AddFollowForm request){
        appUserService.addFollow(request,user);
        return new Result().okMessage("关注用户成功");
    }


    @Login
    @PostMapping("/cancelFollow")
    @Operation(summary = "取消关注用户")
    @NoRepeatSubmit
    public Result cancelFollow(@Parameter(hidden = true) @LoginUser AppUserEntity user, @RequestBody AddFollowForm request){
        appUserService.cancelFollow(request,user);
        return new Result().okMessage("取消关注用户成功");
    }

    @PostMapping("/userInfoById")
    @Operation(summary = "用户个人主页信息")
    public Result<AppUserInfoResponse> userInfoById(@RequestBody AppUserInfoForm request){
        AppUserInfoResponse response=appUserService.findUserInfoById(request.getUid());

        return new Result<AppUserInfoResponse>().ok(response);
    }

    @Login
    @GetMapping("/search")
    @Operation(summary = "搜索用户")
    @NoRepeatSubmit(lockTime = 3000)
	@Parameters({
			@Parameter(name = "page", description = "分页页码", in = ParameterIn.QUERY, required = true,
					schema = @Schema(implementation = Integer.class)),
			@Parameter(name = "keyword", description = "搜索关键词", in = ParameterIn.QUERY, required = false,
					schema = @Schema(implementation = String.class))
	})
    public Result<AppPageUtils<UserInfoResponse>> search(@RequestParam("page") Integer page,
                    @RequestParam("keyword") String keyword,
                    @Parameter(hidden = true) @LoginUser AppUserEntity user){

        AppPageUtils<UserInfoResponse> pages =appUserService.search(page,keyword,user.getUid());
        return new Result<AppPageUtils<UserInfoResponse>>().ok(pages);
    }

    @Login
    @GetMapping("/userFans")
    @Operation(summary = "我的粉丝分页列表")
	@Parameters({
			@Parameter(name = "page", description = "分页页码", in = ParameterIn.QUERY, required = true,
					schema = @Schema(implementation = Integer.class)),
			@Parameter(name = "uid", description = "用户id", in = ParameterIn.QUERY, required = true,
					schema = @Schema(implementation = Integer.class))
	})
    public Result<AppPageUtils<UserInfoResponse>> userFans(@RequestParam("page") Integer page,
                                         @RequestParam("uid") Integer uid,
                                         @Parameter(hidden = true) @LoginUser AppUserEntity user){

        AppPageUtils<UserInfoResponse> pages =appUserService.userFans(page,uid,user.getUid());
        return new Result<AppPageUtils<UserInfoResponse>>().ok(pages);
    }

    @Login
    @GetMapping("/follow")
    @Operation(summary = "我的关注分页列表")
	@Parameters({
			@Parameter(name = "page", description = "分页页码", in = ParameterIn.QUERY, required = true,
					schema = @Schema(implementation = Integer.class)),
			@Parameter(name = "uid", description = "用户id", in = ParameterIn.QUERY, required = true,
					schema = @Schema(implementation = Integer.class))
	})
    public Result<AppPageUtils<UserInfoResponse>> follow(@RequestParam("page") Integer page,
                                       @RequestParam("uid") Integer uid,
                                       @Parameter(hidden = true) @LoginUser AppUserEntity user){

        AppPageUtils<UserInfoResponse> pages =appUserService.follow(page,uid,user);
        return new Result<AppPageUtils<UserInfoResponse>>().ok(pages);
    }


    @GetMapping("/recharge/list")
    @Operation(summary = "用户充值方案")
    public Result<List<RechargeEntity>> recharge(){

        List<RechargeEntity> allRecharge = rechargeService.getAllRecharge();
        return new Result<List<RechargeEntity>>().ok(allRecharge);
    }

    @GetMapping("/isOpen")
    @Operation(summary = "是否开启视频入口")
    public Result isOpen(){
        String isOpen = configService.getValue(Constant.IS_OPEN);
        if(isOpen.equals("1")){
            return new Result<>().ok(1);
        }
        return new Result<>().ok(0);
    }

    @GetMapping("/getContact")
    @Operation(summary = "客服联系方式")
    public Result<Object> getContact(){
        String contactWeChat = configService.getValue(Constant.CONTACT_WECHAT);
        String contactWeChatQr = configService.getValue(Constant.CONTACT_WECHAT_QR);
        String time = configService.getValue(Constant.CONTACT_TIME);
        String phone = configService.getValue(Constant.CONTACT_PHONE);
        Map<String,String> map=new HashMap<>();
        map.put("wechat",contactWeChat);
        map.put("image",contactWeChatQr);
        map.put("time",time);
        map.put("phone",phone);
        return new Result<>().ok(map);
    }

    @Login
    @PostMapping("/systemInfoList")
    @Operation(summary = "系统消息列表")
    public Result<List<AppSystemMessageResponse>> systemInfoList(@RequestBody ChatListForm request, @Parameter(hidden = true) @LoginUser AppUserEntity user) {
        List<AppSystemMessageResponse> list = messageService.systemInfoList(request, user);
        return new Result<List<AppSystemMessageResponse>>().ok(list);
    }

    @Login
    @PostMapping("/systemInfoListByPc")
    @Operation(summary = "系统消息列表_PC端")
    public Result<AppPageUtils<AppSystemMessageResponse>> systemInfoListByPc(@RequestBody ChatListForm request, @Parameter(hidden = true) @LoginUser AppUserEntity user) {
        AppPageUtils<AppSystemMessageResponse> appPage = messageService.systemInfoListByPc(request, user);
        return new Result<AppPageUtils<AppSystemMessageResponse>>().ok(appPage);
    }

    @GetMapping("/getHotUserList")
    @Operation(summary = "热门博主列表")
    public Result<List<AppHotUserResponse>> getHotUserList(){
        List<AppHotUserResponse> list=appUserService.getHotUserList();
        return new Result<List<AppHotUserResponse>>().ok(list);
    }

    @PostMapping("/getSessionKey")
    @NoRepeatSubmit
    @Operation(summary = "获取微信加密秘钥")
    public R getSessionKey(@RequestBody  WxLoginForm param){
        String appId = configService.getValue(Constant.WX_APP_ID);
        String appSecret = configService.getValue(Constant.WX_APP_Secret);
        JSONObject json = WechatUtil.getSessionKeyOropenid(param.getCode(),appId,appSecret);
        String session_key = json.getString("session_key");
        String openid = json.get("openid").toString();
        return R.ok().put("session_key",session_key).put("openid",openid);
    }

    @PostMapping("/bindWxPhone")
    @Operation(summary = "微信小程序绑定手机号")
    public Result<Object> bindWxPhone(@RequestBody LoginPhoneParam param,HttpServletRequest request){
        String ip = IPUtil.getIp(request);
        Integer userId = appUserService.bindWxPhone(param,ip);//如果手机号已被注册过则返回0
        String token = jwtUtils.generateToken(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("expire", jwtUtils.getExpire());
        return new Result<>().ok(map);
    }

    @Login
    @PostMapping("/removeTopic")
    @Operation(summary = "用户移除圈子")
    public Result removeTopic(@Parameter(hidden = true) @LoginUser AppUserEntity user, @RequestBody RemoveTopicForm request){
        appUserService.removeTopic(request,user);
        return new Result().okMessage("移除成功");
    }


    @Login
    @GetMapping("/getUserLevelInfo")
    @Operation(summary = "获取用户等级信息")
    public Result<Map<String, Object>> getHotUserList(@Parameter(hidden = true) @LoginUser AppUserEntity user){
        Map<String, Object> map = appUserService.getUserLevelInfo(user);
        return new Result<Map<String, Object>>().ok(map);

    }

    /**
     * 是否有微信公众号openID
     * @return 是否有微信公众号openID
     */
    @Login
    @GetMapping("/hasMpOpenid")
    @Operation(summary = "是否有微信公众号openid")
    public Result<Boolean> hasMpOpenid(@Parameter(hidden = true) @LoginUser AppUserEntity user){

        return new Result<Boolean>().ok(!WechatUtil.isEmpty(user.getMpOpenid()));
    }

    /**
     * 微信公众号openid绑定
     * @param request 登录请求
     * @return 绑定结果
     */
    @Login
    @PostMapping("/bindMpOpenid")
    @Operation(summary = "微信公众号openid绑定")
    public Result mpWxLogin(@Parameter(hidden = true) @LoginUser AppUserEntity user,  @RequestBody WxMpLoginForm request){

        appUserService.bindMpOpenid(user, request);
        return new Result();
    }

    /**
     * 微信公众号检查code是否注册，注册过自动登录
     * @param param 登录请求
     * @return 绑定结果
     */
    @PostMapping("/loginByWxMp")
    @Operation(summary = "微信公众号code登录")
    public R loginByWxMp(@RequestBody WxMpLoginForm param, HttpServletRequest request){

        String ip = IPUtil.getIp(request);
        Integer userId = appUserService.loginByWxMp(param, ip);
        if(userId==0){
            return R.error(501,"未注册用户");
        }
        //生成token
        String token = jwtUtils.generateToken(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("expire", jwtUtils.getExpire());

        return R.ok(map);
    }


    @Operation(summary = "用户端——统计当前用户")
    @PostMapping("/visitor")
    public Result visitor(@RequestBody VisitorForm param,HttpServletRequest request){
        String ip = IPUtil.getIp(request);
        AppUserEntity user = localUser.getUser();
        activeUserService.addVisit(ip,param,user);
        return new Result();
    }


    @PostMapping("/userRank")
    @Operation(summary = "发帖达人列表")
    public Result<List<AppUserRankResponse>> userRank() {
        List<AppUserRankResponse> list=appUserService.userRank();
        return new Result<List<AppUserRankResponse>>().ok(list);
    }

    @Login
    @PostMapping("/deleteAccount")
    @Operation(summary = "账户注销")
    public Result deleteAccount(@Parameter(hidden = true) @LoginUser AppUserEntity user) {
        appUserService.deleteAccount(user);
        return new Result();
    }

    /**
     * 第三方应用请求登录
     * @param form
     * @param request
     * @return
     */
    @PostMapping("/thirdLogin")
    @Operation(summary = "第三方应用请求登录接口")
    public R getToken(@RequestBody UserLoginForm form, HttpServletRequest request){
        String ip = IPUtil.getIp(request);
        Integer userId = appUserService.getThirdLoginToken(form,ip);
        String token = jwtUtils.generateToken(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("expire", jwtUtils.getExpire());

        return R.ok(map);
    }
}
