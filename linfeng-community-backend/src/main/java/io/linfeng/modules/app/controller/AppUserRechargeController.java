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

import com.google.common.collect.Maps;
import io.linfeng.common.vo.app.OrderPay;
import io.linfeng.common.vo.app.AppBillResponse;
import io.linfeng.common.utils.*;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.service.BillService;
import io.linfeng.modules.admin.service.UserRechargeService;
import io.linfeng.modules.app.annotation.Login;
import io.linfeng.modules.app.annotation.LoginUser;
import io.linfeng.modules.app.param.*;
import io.linfeng.modules.app.utils.weixin.sdk.HttpKit;
import io.linfeng.modules.app.utils.weixin.sdk.PaymentKit;
import io.linfeng.modules.app.utils.weixin.sdk.WXPayUtil;
import io.linfeng.modules.sys.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;


/**
 * 用户充值
 *
 * @author linfeng
 * @email linfengtech001@163.com
 * @date 2022-04-19 19:27:33
 */
@Tag(name = "移动端——用户充值模块")
@RestController
@RequestMapping("app")
@Slf4j
public class AppUserRechargeController {

    @Autowired
    private UserRechargeService userRechargeService;
    @Autowired
    private BillService billService;
    @Autowired
    private SysConfigService configService;



    /**
     * 付费贴支付
     * @param param
     * @param user
     * @return
     */
    @Login
    @PostMapping("/user/payVipPost")
    @Operation(summary = "付费贴支付")
    public Result payVipPost(@RequestBody PayVipPostForm param,@Parameter(hidden = true) @LoginUser AppUserEntity user){
        userRechargeService.payVipPost(param,user);
        return new Result<>();
    }
    /**
     * 会员充值
     * APP微信支付
     * @return
     */
    @Login
    @PostMapping("/user/rechargeByApp")
    @Operation(summary = "App会员充值微信支付")
    public R rechargeByApp(@RequestBody RechargeForm param,@Parameter(hidden = true) @LoginUser AppUserEntity user, HttpServletRequest request) throws Exception {
        ValidatorUtils.validateEntity(param);

        String orderSn = userRechargeService.addRecharge(user,param.getPrice().toString(),param.getPaidPrice().toString(),Constant.PAY_TYPE_APP);
        String ip= AppletPayUtil.getClientIp(request);
        OrderPay orderPay=userRechargeService.goToPay(orderSn,param.getPrice(),ip,user,Constant.PAY_TYPE_APP);
        return R.ok().put("data",orderPay);
    }
    /**
     * 充值
     * 微信小程序支付
     * @return
     */
    @Login
    @PostMapping("/user/recharge")
    @Operation(summary = "充值-微信小程序支付")
    public R recharge(@RequestBody RechargeForm param, @Parameter(hidden = true) @LoginUser AppUserEntity user, HttpServletRequest request) throws Exception {
        ValidatorUtils.validateEntity(param);

        String orderSn = userRechargeService.addRecharge(user,param.getPrice().toString(),param.getPaidPrice().toString(),Constant.PAY_TYPE_WX);
        String ip= AppletPayUtil.getClientIp(request);
        OrderPay orderPay=userRechargeService.goToPay(orderSn,param.getPrice(),ip,user,Constant.PAY_TYPE_WX);
        return R.ok().put("data",orderPay);
    }
    /**
     * 充值
     * H5支付
     * @return
     */
    @Login
    @PostMapping("/user/rechargeByH5")
    @Operation(summary = "充值-H5支付")
    public R rechargeByH5(@RequestBody RechargeForm param,@Parameter(hidden = true)  @LoginUser AppUserEntity user, HttpServletRequest request) throws Exception {
        ValidatorUtils.validateEntity(param);

        String orderSn = userRechargeService.addRecharge(user,param.getPrice().toString(),param.getPaidPrice().toString(),Constant.PAY_TYPE_H5);
        String ip= AppletPayUtil.getClientIp(request);
        OrderPay orderPay=userRechargeService.goToPay(orderSn,param.getPrice(),ip,user,Constant.PAY_TYPE_H5);
        return R.ok().put("data",orderPay);
    }
    /**
     * 充值
     * 微信公众号支付
     * @return
     */
    @Login
    @PostMapping("/user/rechargeByWXH5")
    @Operation(summary = "充值-微信公众号支付")
    public R rechargeByWXH5(@RequestBody RechargeForm param,@Parameter(hidden = true)  @LoginUser AppUserEntity user, HttpServletRequest request) throws Exception {
        ValidatorUtils.validateEntity(param);

        String orderSn = userRechargeService.addRecharge(user,param.getPrice().toString(),param.getPaidPrice().toString(),Constant.PAY_TYPE_WXH5);
        String ip= AppletPayUtil.getClientIp(request);
        OrderPay orderPay=userRechargeService.goToPay(orderSn,param.getPrice(),ip,user,Constant.PAY_TYPE_WXH5);
        return R.ok().put("data",orderPay);
    }

    /**
     * 微信支付回调
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @PostMapping("/pay/rolBack")
    @Operation(summary = "微信支付回调")
    public void wxPayNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("进入微信支付回调");
        String xmlMsg = HttpKit.readData(request);
        log.info("微信通知信息"+xmlMsg);
        Map<String, String> resultMap = PaymentKit.xmlToMap(xmlMsg);
        if(!WechatUtil.isEmpty(resultMap)){
            if(resultMap.get(Constant.WX_RETURN_CODE).equals(Constant.WX_PAY_SUCCESS)){
                String orderNo = resultMap.get("out_trade_no");
                String key = configService.getValue(Constant.KEY);
                //验证签名
                if(WXPayUtil.isSignatureValid(resultMap,key)){
                    log.info("回调out_trade_no="+orderNo);
                    log.info("微信支付回调成功");
                    //截取订单编号
                    String[] split = orderNo.split("-");
                    if(!WechatUtil.isEmpty(split)){
                        String orderId=split[0];
                        if(!WechatUtil.isEmpty(orderId)){
                            log.info("处理支付成功后的业务逻辑");
                            userRechargeService.handleWxLog(orderId,resultMap.get("transaction_id"),orderNo);
                        }

                    }
                }
            }
        }else {
            log.info("map为null");
        }
        String result = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        response.getWriter().write(result);
    }

    /**
     * 用户账单分页查询
     * @param request
     * @param user
     * @return
     */
    @Login
    @PostMapping("/bill/billList")
    @Operation(summary = "用户账单分页查询")
    public Result<AppPageUtils<AppBillResponse>> billList(@RequestBody GetBillListForm request, @Parameter(hidden = true) @LoginUser AppUserEntity user){
        AppPageUtils<AppBillResponse> page = billService.billList(request, user);
        return new Result<AppPageUtils<AppBillResponse>>().ok(page);
    }

    @Login
    @PostMapping("/user/bill")
    @Operation(summary = "用户账户详情")
    public Result<Object> userBill(@Parameter(hidden = true) @LoginUser AppUserEntity user){
        Map<String,Object> map = Maps.newHashMap();
        map.put("nowMoney",user.getMoney());
        map.put("allIntegral",user.getIntegral());
        map.put("orderStatusSum",billService.getAllPay(user.getUid()));
        map.put("isHide",configService.getValue(Constant.CHARGE));
        map.put("exchange",configService.getValue(Constant.EXCHANGE));
        map.put("integral",configService.getValue(Constant.INTEGRAL));
        map.put("canCash",configService.getValue(Constant.CAN_CASH_OUT));
        map.put("consumer",billService.getUsedIntegral(user.getUid()));
        map.put("yuetoint",configService.getValue(Constant.YUE_TO_INT));
        map.put("ratio",configService.getValue(Constant.YUE_TO_INT_RATIO));
        return new Result<>().ok(map);
    }


    /**
     * 积分兑换余额
     * @param user
     * @return
     */
    @Login
    @PostMapping("/bill/exchange")
    @Operation(summary = "积分兑换余额")
    public Result exchange(@Parameter(hidden = true) @LoginUser AppUserEntity user,
                           @RequestBody ExchangeForm request){
        billService.exchange(user,request);
        return new Result();
    }

    @Login
    @PostMapping("/bill/exchangeToIntegral")
    @Operation(summary = "余额兑换积分")
    public Result exchangeToIntegral(@Parameter(hidden = true) @LoginUser AppUserEntity user,
                           @RequestBody ExchangeIntegralForm request){
        billService.exchangeToIntegral(user,request);
        return new Result();
    }

    /**
     * 回调后业务逻辑测试
     * @return
     */
    @Login
    @PostMapping("/pay/test")
    @Operation(summary = "测试回调后业务逻辑")
    public Result billList(){
        userRechargeService.handleWxLog("1521112168802549760","777testorder999","90payorderId787878");
        return new Result();
    }



    @Login
    @PostMapping("/bill/reward")
    @Operation(summary = "打赏积分")
    public Result reward(@Parameter(hidden = true) @LoginUser AppUserEntity user,
                         @RequestBody AddRewardForm request){
        billService.rewardIntegral(user,request);
        return new Result();
    }

}
