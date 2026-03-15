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

import io.linfeng.common.utils.Constant;
import io.linfeng.common.utils.R;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.app.annotation.Login;
import io.linfeng.modules.app.annotation.LoginUser;
import io.linfeng.modules.sys.service.SysConfigService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author linfeng
 */
@RestController
@RequestMapping("app/system")
@Tag(name = "移动端——系统配置模块")
public class AppSystemConfigController {


    @Autowired
    private SysConfigService configService;

    @Value("${swagger.version}")
    private String appVersion;


    @Operation(summary = "用户端——查询微信小程序配置信息")
    @GetMapping("/wxLoginBasicInfo")
    public R wxLoginBasicInfo(){
        String logo = configService.getValue(Constant.IMG);
        String loginType = configService.getValue(Constant.LOGIN_TYPE);
        String loginInfoPop = configService.getValue(Constant.LOGIN_INFO_POP);
        return R.ok()
                .put("logo",logo)
                .put("loginType",loginType)
                .put("loginInfoPop",loginInfoPop);
    }

    @Operation(summary = "用户端——查询app基础配置信息")
    @GetMapping("/basic")
    public R basic(){
        String logo = configService.getValue(Constant.IMG);
        String indexStyle1 = configService.getValue(Constant.INDEX_STYLE_LAST);
        String indexStyle2 = configService.getValue(Constant.INDEX_STYLE_TOPIC);
        String indexStyle3 = configService.getValue(Constant.INDEX_STYLE_WATCH);
        String vipShow = configService.getValue(Constant.VIP_SHOW);
        String iosClose = configService.getValue(Constant.IOS_CLOSE);

        return R.ok()
                .put("logo",logo)
                .put("indexStyle1",indexStyle1)
                .put("indexStyle2",indexStyle2)
                .put("indexStyle3",indexStyle3)
                .put("iosClose",iosClose)
                .put("vipShow",vipShow);
    }

    @Operation(summary = "用户端——查询app登录基础配置信息")
    @GetMapping("/loginBasic")
    public R loginBasic(){
        String logo = configService.getValue(Constant.IMG);
        String smsOpen = configService.getValue(Constant.SMS_OPEN);
        return R.ok()
                .put("logo",logo)
                .put("smsOpen",smsOpen);
    }


    @Operation(summary = "用户端——查询vip开关配置信息")
    @GetMapping("/vipShow")
    public R vipShow(){
        String vipShow = configService.getValue(Constant.VIP_SHOW);

        return R.ok().put("vipShow",vipShow);
    }

    @Operation(summary = "用户端——登录注册页面配置信息")
    @GetMapping("/config")
    public R config(){
        String logoUrl = configService.getValue(Constant.IMG);
        String emailLogin = configService.getValue(Constant.EMAIL_LOGIN);
        String openEmailRegister = configService.getValue(Constant.OPEN_EMAIL_REGISTER);

        return R.ok()
                .put("logoUrl", logoUrl)
                .put("emailLogin",emailLogin)
                .put("openEmailRegister",openEmailRegister);
    }


    @Operation(summary = "用户端——用户页面背景图")
    @GetMapping("/bgImgConfig")
    public R bgImgConfig(){
        String bgImg = configService.getValue(Constant.BG_IMG);

        return R.ok().put("bgImg", bgImg);
    }


    @Operation(summary = "用户端——查询用户服务协议")
    @GetMapping("/protocol")
    public R protocol(){
        String value = configService.getValue(Constant.PROTOCOL);
        return R.ok().put("result", value);
    }

    @Operation(summary = "用户端——查询个人隐私协议")
    @GetMapping("/privacy")
    public R privacy(){
        String value = configService.getValue(Constant.PRIVACY);
        return R.ok().put("result", value);
    }

    @Operation(summary = "用户端——查询平台发帖规范协议")
    @GetMapping("/standard")
    public R standard(){
        String value = configService.getValue(Constant.STANDARD);
        return R.ok().put("result", value);
    }

    @Operation(summary = "用户端——查询会员充值协议")
    @GetMapping("/vipRecharge")
    public R vipRecharge(){
        String value = configService.getValue(Constant.VIP_AGREE_CONTENT);
        return R.ok().put("result", value);
    }

    @Operation(summary = "用户端——查询好友聊天模块是否开启")
    @GetMapping("/socialOpen")
    public R socialOpen(){
        String value = configService.getValue(Constant.SOCIAL_BTN);
        return R.ok().put("result", value.equals("0"));
    }

    @Operation(summary = "用户端——查询流量主广告基本设置")
    @GetMapping("/getAd")
    public R getAd(){
        String adIsOpen = configService.getValue(Constant.AD_IS_OPEN);
        String wxAdpid = configService.getValue(Constant.WX_AD_PID);
        String h5Adpid = configService.getValue(Constant.H5_AD_PID);
        return R.ok()
                .put("wxAdpid", wxAdpid)
                .put("adIsOpen",adIsOpen)
                .put("h5Adpid",h5Adpid);
    }


    @Operation(summary = "用户端——查询首页弹窗广告设置")
    @GetMapping("/getPop")
    public R getPop(){
        String popupOpen = configService.getValue(Constant.POPUP_OPEN);
        if("1".equals(popupOpen)){
            return R.ok().put("popupOpen", popupOpen);
        }
        String popTitle = configService.getValue(Constant.POP_TITLE);
        String popContent = configService.getValue(Constant.POP_CONTENT);
        String popTime = configService.getValue(Constant.POP_TIME);
        return R.ok()
                .put("popupOpen", popupOpen)
                .put("popTitle",popTitle)
                .put("popContent",popContent)
                .put("popTime",popTime);
    }


    @Login
    @Operation(summary = "用户端——查询付费贴开关")
    @GetMapping("/checkPayPostBtn")
    public R checkPayPostBtn(@Parameter(hidden = true) @LoginUser AppUserEntity user){
        boolean isOpen = true;
        String payPostBtn = configService.getValue(Constant.PAY_POST_BTN);
        String isVipOpen = configService.getValue(Constant.PAY_POST_VIP);
        if(payPostBtn.equals("1")){
            isOpen = false;
        }
        if(isVipOpen.equals("1") && user.getVip().equals(Constant.COMMON_USER)){
            isOpen=false;
        }
        return R.ok()
                .put("isOpen", isOpen);
    }



    @Operation(summary = "用户端——查询充值入口是否开启")
    @GetMapping("/rechargeIsOpen")
    public R rechargeIsOpen(){
        String isOpen = configService.getValue(Constant.CHARGE);
        return R.ok().put("result", isOpen.equals("0"));
    }

    @Operation(summary = "用户端——查询提现入口是否开启")
    @GetMapping("/cashOutIsOpen")
    public R cashOutIsOpen(){
        String isOpen = configService.getValue(Constant.CAN_CASH_OUT);
        return R.ok().put("result", isOpen.equals("1"));
    }

    @Operation(summary = "用户端——查询付费贴入口是否开启")
    @GetMapping("/payPostIsOpen")
    public R payPostIsOpen(){
        String isOpen = configService.getValue(Constant.PAY_POST_BTN);
        return R.ok().put("result", isOpen.equals("0"));
    }

    @Operation(summary = "用户端——关于我们页面获取系统信息")
    @GetMapping("/aboutUs")
    public R aboutUs(){
        String logo = configService.getValue(Constant.IMG);
        return R.ok()
                .put("logo", logo)
                .put("version",appVersion);
    }

}
