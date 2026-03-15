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

import io.linfeng.common.annotation.NoRepeatSubmit;
import io.linfeng.common.utils.Constant;
import io.linfeng.common.utils.DataConvertUtils;
import io.linfeng.common.utils.R;
import io.linfeng.common.utils.Result;
import io.linfeng.common.vo.app.OrderPay;
import io.linfeng.common.vo.app.AppVipOptionResponse;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.entity.VipOptionEntity;
import io.linfeng.modules.admin.service.VipOptionService;
import io.linfeng.modules.app.annotation.Login;
import io.linfeng.modules.app.annotation.LoginUser;
import io.linfeng.modules.app.param.VipPayForm;
import io.linfeng.modules.app.param.VipRechargeForm;
import io.linfeng.modules.sys.service.SysConfigService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;


/**
 * 会员充值选项
 *
 * @author JL.Yu
 * @email linfengtech001@163.com
 * @date 2022-09-28 14:26:17
 */
@RestController
@RequestMapping("app/vip")
@Tag(name = "移动端——会员充值选项模块")
public class AppVipOptionController {

    @Autowired
    private VipOptionService vipOptionService;
    @Autowired
    private SysConfigService configService;


    @GetMapping("/vipList")
    @Operation(summary = "会员充值选项列表")
    public Result<List<AppVipOptionResponse>> vipList() {
        List<VipOptionEntity> entities = vipOptionService.lambdaQuery()
                .orderByAsc(VipOptionEntity::getSort)
                .list();
        List<AppVipOptionResponse> result = DataConvertUtils.sourceToTarget(
                entities, AppVipOptionResponse.class
        );
        return new Result<List<AppVipOptionResponse>>().ok(result);
    }


    @Login
    @PostMapping("/rechargeVip")
    @Operation(summary = "会员预充值")
    public Result<String> rechargeVip(@Parameter(hidden = true) @LoginUser AppUserEntity user, @RequestBody VipRechargeForm param) {

        String orderId = vipOptionService.rechargeVip(user, param);
        return new Result<String>().ok(orderId);
    }


    @Login
    @PostMapping("/rechargeVipByYue")
    @Operation(summary = "会员余额支付")
    @NoRepeatSubmit
    public Result rechargeVipByYue(@Parameter(hidden = true) @LoginUser AppUserEntity user,
                                   @RequestBody VipRechargeForm param) {

        vipOptionService.rechargeVipByYue(user.getUid(), param);
        return new Result();
    }


    @Login
    @PostMapping("/pay")
    @Operation(summary = "会员充值微信支付")
    public R pay(@Parameter(hidden = true) @LoginUser AppUserEntity user, @RequestBody VipPayForm param, HttpServletRequest request) throws Exception {

        OrderPay orderPay = vipOptionService.vipPay(user, param, request);
        return R.ok().put("data", orderPay);
    }


    @GetMapping("/vipConfig")
    @Operation(summary = "会员配置查询")
    public R vipConfig() {
        return R.ok()
                .put("integralNum", configService.getValue(Constant.VIP_INTEGRAL))
                .put("renameCount", configService.getValue(Constant.VIP_RENAME))
                .put("topicNum", configService.getValue(Constant.VIP_TOPIC_NUMBER));
    }

}
