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

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.linfeng.common.enums.BillDetailEnum;
import io.linfeng.common.exception.LinfengException;
import io.linfeng.common.vo.app.OrderPay;
import io.linfeng.common.utils.*;
import io.linfeng.modules.admin.entity.*;
import io.linfeng.modules.admin.service.*;
import io.linfeng.modules.app.param.PayVipPostForm;
import io.linfeng.modules.app.utils.weixin.sdk.PaymentApi;
import io.linfeng.modules.app.utils.weixin.sdk.PaymentKit;
import io.linfeng.modules.app.utils.weixin.sdk.WXPayUtil;
import io.linfeng.modules.sys.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.linfeng.modules.admin.dao.UserRechargeDao;
import org.springframework.transaction.annotation.Transactional;


@Service("userRechargeService")
@Slf4j
public class UserRechargeServiceImpl extends ServiceImpl<UserRechargeDao, UserRechargeEntity> implements UserRechargeService {


    @Autowired
    private AppUserService userService;
    @Autowired
    private BillService billService;
    @Autowired
    private PostService postService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private MessageService messageService;
    @Autowired
    private SysConfigService configService;
    @Autowired
    private UserRechargeDao userRechargeDao;
    @Autowired
    private VipOptionService vipOptionService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<UserRechargeEntity> queryWrapper=new QueryWrapper<>();
        //条件查询
        String key = (String) params.get("key");
        String type = (String) params.get("type");
        String type2 = (String) params.get("type2");

        if (NumberUtil.isInteger(key)) {
            if (!WechatUtil.isEmpty(key)) {
                queryWrapper.lambda().like(UserRechargeEntity::getUid,Integer.valueOf(key));
            }
        } else {
            if (!WechatUtil.isEmpty(key)) {
                queryWrapper.lambda().like(UserRechargeEntity::getOrderId, key);
            }
        }
        if (!WechatUtil.isEmpty(type)) {
            queryWrapper.lambda().eq(UserRechargeEntity::getPaid,Integer.valueOf(type));
        }
        if (!WechatUtil.isEmpty(type2)) {
            queryWrapper.lambda().eq(UserRechargeEntity::getType,Integer.valueOf(type2));
        }
        queryWrapper.lambda().orderByDesc(UserRechargeEntity::getId);
        IPage<UserRechargeEntity> page = this.page(
                new Query<UserRechargeEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    @Override
    public String addRecharge(AppUserEntity user, String price, String paidPrice, String type) {
        UserRechargeEntity entity = new UserRechargeEntity();
        String orderSn = IdUtil.getSnowflake(0L, 0L).nextIdStr();

        entity.setNickname(user.getUsername());
        entity.setOrderId(orderSn);
        entity.setUid(user.getUid());
        entity.setPrice(new BigDecimal(price));
        entity.setGivePrice(new BigDecimal(paidPrice));
        entity.setRechargeType(type);
        entity.setPaid(Constant.RECHARGE_UNPAY);
        entity.setAddTime(DateUtil.nowDateTime());
        this.save(entity);
        return orderSn;
    }

    @Override
    public OrderPay goToPay(String orderSn, BigDecimal money,String ip,AppUserEntity user,String type) throws Exception {
        String appId = configService.getValue(Constant.WX_APP_ID);
        String app_appid = configService.getValue(Constant.APP_APP_ID);
        String app_notify_url = configService.getValue(Constant.APP_NOTIFY_URL);
        String mch_id = configService.getValue(Constant.MCH_ID);
        String key = configService.getValue(Constant.KEY);
        String redirectUrl = configService.getValue(Constant.REDIRECT_URL);
        String wxh5appId = configService.getValue(Constant.WX_MP_ID);
        Map<String, String> reqParams = new HashMap<>();
        //生成一个新的订单支付编号
        String outTradeNo=orderSn+"-"+ WechatUtil.getRandomCode(3,0)+"PAY";
        //用户标识
        reqParams.put("openid", user.getOpenid());
        //交易类型
        if(type.equals(Constant.PAY_TYPE_WX)){
            reqParams.put("trade_type", "JSAPI");
            //微信分配的小程序ID
            reqParams.put("appid", appId);
        }else if(type.equals(Constant.PAY_TYPE_H5)){
            reqParams.put("trade_type", "MWEB");
            //微信分配的小程序ID
            reqParams.put("appid", appId);
        }else if(type.equals(Constant.PAY_TYPE_APP)){
            reqParams.put("trade_type", "APP");
            //微信分配的APPID
            reqParams.put("appid", app_appid);
        }else if(type.equals(Constant.PAY_TYPE_WXH5)){
            reqParams.put("openid", user.getMpOpenid());
            reqParams.put("trade_type", "JSAPI");
            //微信公众号AppID
            reqParams.put("appid", wxh5appId);
        }
        //微信支付分配的商户号
        reqParams.put("mch_id", mch_id);
        //随机字符串
        reqParams.put("nonce_str", System.currentTimeMillis() / 1000 + "");
        //签名类型
        reqParams.put("sign_type", "MD5");
        //充值订单 商品描述
        reqParams.put("body", "充值"+orderSn+"订单-微信支付");

        //商户订单编号
        reqParams.put("out_trade_no", outTradeNo);
        //订单总金额，单位为分
        reqParams.put("total_fee", money.multiply(BigDecimal.valueOf(100)).intValue() + "");
        //终端IP
        reqParams.put("spbill_create_ip", ip);
        //通知地址
        reqParams.put("notify_url", app_notify_url);
        //签名
        String sign = WXPayUtil.generateSignature(reqParams, key);
        reqParams.put("sign", sign);
        //调用支付定义下单API,返回预付单信息 prepay_id
        log.info(JSON.toJSONString(reqParams));
        String xmlResult = PaymentApi.pushOrder(reqParams);
        log.info(xmlResult);
        Map<String, String> result = PaymentKit.xmlToMap(xmlResult);
        //预付单信息
        String prepay_id = result.get("prepay_id");
        String timeStamp = System.currentTimeMillis() / 1000 + "";
        String nonceStr = System.currentTimeMillis() + "";
        //小程序调起支付数据签名
        Map<String, String> packageParams = new HashMap<String, String>();
        packageParams.put("appId", reqParams.get("appid"));
        packageParams.put("timeStamp", timeStamp);
        packageParams.put("nonceStr", nonceStr);
        packageParams.put("package", "prepay_id=" + prepay_id);
        packageParams.put("signType", "MD5");
        String packageSign = WXPayUtil.generateSignature(packageParams, key);
        packageParams.put("paySign", packageSign);
        packageParams.put("codeUrl", result.get("code_url"));
        if (type.equals(Constant.PAY_TYPE_H5)) {
            packageParams.put("mwebUrl", result.get("mweb_url") + "&redirect_url="+ URLEncoder.encode(redirectUrl, "GBK"));
        }
        if (type.equals(Constant.PAY_TYPE_APP)) {
            Map<String, String> appMap = new HashMap<>();
            appMap.put("appid", reqParams.get("appid"));
            appMap.put("partnerid", mch_id);
            appMap.put("prepayid", prepay_id);
            appMap.put("package", "Sign=WXPay");
            appMap.put("noncestr", nonceStr);
            appMap.put("timestamp", timeStamp);
            String appSign = WXPayUtil.generateSignature(appMap, key);
            packageParams.put("paySign", appSign);
            packageParams.put("partnerId", mch_id);
        }
        log.info("支付返回结果集:{}" ,packageParams.toString());
        ObjectMapper mapper = new ObjectMapper();
        OrderPay pay = mapper.readValue(mapper.writeValueAsString(packageParams), OrderPay.class);

        return pay;
    }

    /**
     * 处理支付回调后业务
     * @param orderId
     * @param transaction_id
     * @param orderNo
     */
    @Override
    public void handleWxLog(String orderId, String transaction_id, String orderNo) {
        //1.更新订单信息
        UserRechargeEntity userRecharge = this.lambdaQuery()
                .eq(UserRechargeEntity::getOrderId, orderId)
                .one();
        if(ObjectUtil.isNull(userRecharge)){
            throw new LinfengException("该订单号不存在");
        }
        if(userRecharge.getPaid().equals(Constant.RECHARGE_PAY)){
            throw new LinfengException("该订单已支付");
        }
        userRecharge.setPayTime(DateUtil.nowDateTime());
        userRecharge.setPaid(Constant.RECHARGE_PAY);
        userRecharge.setTransactionId(transaction_id);
        userRecharge.setOutTradeNo(orderNo);
        boolean b = this.saveOrUpdate(userRecharge);
        if(!b){
            throw new LinfengException("订单更新失败");
        }
        if(userRecharge.getType()==1){
            //会员充值回调业务
            vipOptionService.rollBackVip(userRecharge);
        }else{
            //钱包充值回调业务
            //2.添加用户余额
            BigDecimal money=userService.updateMoney(userRecharge.getUid(),userRecharge.getPrice(),userRecharge.getGivePrice());
            //3.添加用户账单
            double v = userRecharge.getPrice().add(userRecharge.getGivePrice()).doubleValue();
            String mark="用户充值"+userRecharge.getPrice()+"赠"+userRecharge.getGivePrice()+"元";
            billService.income(userRecharge.getUid().longValue(), BillDetailEnum.TYPE_1.getDesc(),BillDetailEnum.CATEGORY_1.getValue(),BillDetailEnum.TYPE_1.getValue(),v,money.doubleValue(),mark,userRecharge.getOrderId());

        }

    }

    /**
     * 支付付费贴
     *
     * @param param
     * @param users
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payVipPost(PayVipPostForm param, AppUserEntity users) {
        String price = configService.getValue(Constant.POST_PRICE);
        //这里安全起见，不用redis缓存取出的用户余额信息
        AppUserEntity user = userService.getById(users.getUid());
        //判断是否支付
        boolean isPay = billService.vipPostIsPay(param.getPostId(), user.getUid());
        if(isPay){
            throw new LinfengException("请勿重复支付");
        }
        //判断余额是否足够
        PostEntity post = postService.getById(param.getPostId());
        if(post.getPay().compareTo(user.getMoney()) > 0){
            throw new LinfengException("余额不足");
        }
        //余额支付
        BigDecimal balance = user.getMoney().subtract(post.getPay());

        boolean update = userService.lambdaUpdate().set(AppUserEntity::getMoney, balance)
                .eq(AppUserEntity::getUid, user.getUid())
                .update();
        if(!update){
            throw new LinfengException("余额扣除失败");
        }
        //删除用户信息缓存
        redisUtils.delete(RedisKeys.getUserCacheKey(user.getUid()));
        //记录账单
        String mark="付费贴支付"+post.getPay()+"元";
        billService.expend(user.getUid().longValue(),BillDetailEnum.TYPE_3.getDesc(),BillDetailEnum.CATEGORY_1.getValue(),BillDetailEnum.TYPE_3.getValue(),post.getPay().doubleValue(),balance.doubleValue(),mark,post.getId().toString());
        //发布方收取费用
        AppUserEntity author = userService.getById(post.getUid());
        boolean integer = NumberUtil.isInteger(price);
        if(!integer){
            throw new LinfengException("抽成配置不是整数");
        }
        Integer value = Integer.valueOf(price);
        //抽成
        BigDecimal multiply = new BigDecimal(value).multiply(new BigDecimal(0.01));
        //用户获取的金额+原来的余额=用户总余额
        BigDecimal add = author.getMoney().add(post.getPay().multiply(multiply));
        boolean authorUpdate = userService.lambdaUpdate().set(AppUserEntity::getMoney, add)
                .eq(AppUserEntity::getUid, post.getUid())
                .update();
        if(!authorUpdate){
            throw new LinfengException("余额新增失败");
        }
        //删除发布方用户信息缓存
        redisUtils.delete(RedisKeys.getUserCacheKey(post.getUid()));
        //发布方记录账单
        String authorMark="付费贴("+post.getPay()+"元)(提成"+price+"%)入账"+post.getPay().multiply(multiply).setScale(2, RoundingMode.HALF_UP)+"元";
        billService.income(post.getUid().longValue(),BillDetailEnum.TYPE_2.getDesc(),BillDetailEnum.CATEGORY_1.getValue(),BillDetailEnum.TYPE_2.getValue(),post.getPay().multiply(multiply).doubleValue(),add.doubleValue(),authorMark,post.getId().toString());
        //通知发布方
        String content = StrUtil.format(Constant.VIP_POST,user.getUsername(),post.getTitle(),authorMark);
        messageService.sendMessageNotAsync(0,author.getUid(),post.getId(),Constant.SYSMSG,content,Constant.TITLE_PAY);
    }

    /**
     * 用户充值总金额
     * @return
     */
    @Override
    public double rechargeMoney() {

        return userRechargeDao.rechargeMoney();
    }

    /**
     * 本月充值总金额
     * @return
     */
    @Override
    public double rechargeMoneyByMonth() {
        Date nowMonth = cn.hutool.core.date.DateUtil.beginOfMonth(new Date());
        LambdaQueryWrapper<UserRechargeEntity> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(UserRechargeEntity::getPaid,1)
                .ge(UserRechargeEntity::getPayTime,nowMonth);

        return userRechargeDao.rechargeMoneyByMonth(wrapper);
    }


}