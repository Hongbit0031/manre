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

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import io.linfeng.common.enums.BillDetailEnum;
import io.linfeng.common.exception.LinfengException;
import io.linfeng.common.vo.app.OrderPay;
import io.linfeng.common.utils.*;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.entity.UserRechargeEntity;
import io.linfeng.modules.admin.service.AppUserService;
import io.linfeng.modules.admin.service.BillService;
import io.linfeng.modules.admin.service.UserRechargeService;
import io.linfeng.modules.app.param.VipPayForm;
import io.linfeng.modules.app.param.VipRechargeForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.linfeng.modules.admin.dao.VipOptionDao;
import io.linfeng.modules.admin.entity.VipOptionEntity;
import io.linfeng.modules.admin.service.VipOptionService;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;


@Service("vipOptionService")
public class VipOptionServiceImpl extends ServiceImpl<VipOptionDao, VipOptionEntity> implements VipOptionService {

    @Autowired
    private UserRechargeService userRechargeService;

    @Autowired
    private AppUserService userService;

    @Autowired
    private BillService billService;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<VipOptionEntity> page = this.page(
                new Query<VipOptionEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public String rechargeVip(AppUserEntity user,VipRechargeForm param) {
        VipOptionEntity vipOption = this.getById(param.getVipId());
        if(ObjectUtil.isNull(vipOption)){
            throw new LinfengException("充值方案不存在");
        }
        UserRechargeEntity entity=new UserRechargeEntity();
        String orderSn = IdUtil.getSnowflake(0L, 0L).nextIdStr();
        entity.setNickname(user.getUsername());
        entity.setOrderId(orderSn);
        entity.setUid(user.getUid());
        entity.setPrice(vipOption.getPrice());
        entity.setGivePrice(new BigDecimal(vipOption.getId()));//这个参数在会员充值中用于存储充值方案id
        entity.setRechargeType(param.getPayType());
        entity.setPaid(Constant.RECHARGE_UNPAY);
        entity.setAddTime(DateUtil.nowDateTime());
        entity.setType(1);
        userRechargeService.save(entity);
        return orderSn;
    }

    @Override
    public OrderPay vipPay(AppUserEntity user, VipPayForm param, HttpServletRequest request) throws Exception {
        UserRechargeEntity userRecharge = userRechargeService.lambdaQuery().eq(UserRechargeEntity::getOrderId, param.getOrderId()).one();
        if(ObjectUtil.isNull(userRecharge)){
            throw new LinfengException("充值订单不存在");
        }
        String ip= AppletPayUtil.getClientIp(request);
        OrderPay orderPay=userRechargeService.goToPay(userRecharge.getOrderId(),userRecharge.getPrice(),ip,user,param.getPayType());
        return orderPay;
    }

    /**
     * 会员支付回调业务
     * 更新用户会员信息 添加账单记录
     * @param userRecharge
     */
    @Override
    public void rollBackVip(UserRechargeEntity userRecharge) {
        AppUserEntity user = userService.getById(userRecharge.getUid());
        int vipOptionId = userRecharge.getGivePrice().intValue();
        VipOptionEntity vipOption= this.getById(vipOptionId);
        if(user.getVip().equals(Constant.VIP_USER) && ObjectUtil.isNotNull(user.getVipExpireTime())){
            //续费用户
            String toStr = DateUtil.dateToStr(user.getVipExpireTime(), "yyyy-MM-dd HH:mm:ss");
            user.setVipExpireTime(DateUtil.addDay(toStr,vipOption.getValidDays()));
        }else{
            user.setVip(Constant.VIP_USER);
            user.setVipExpireTime(DateUtil.addDay(DateUtil.nowDateTimeStr(),vipOption.getValidDays()));
        }
        boolean b = userService.saveOrUpdate(user);
        if(!b){
            throw new LinfengException("会员信息更新失败");
        }
        redisUtils.delete(RedisKeys.getUserCacheKey(user.getUid()));
        String mark="用户会员充值消费"+userRecharge.getPrice()+"元";
        billService.expend(userRecharge.getUid().longValue(), BillDetailEnum.TYPE_16.getDesc(),BillDetailEnum.CATEGORY_1.getValue(),BillDetailEnum.TYPE_16.getValue(),userRecharge.getPrice().doubleValue(),user.getMoney().doubleValue(),mark,"");


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rechargeVipByYue(Integer uid, VipRechargeForm param) {
        VipOptionEntity vipOption = this.getById(param.getVipId());
        if(ObjectUtil.isNull(vipOption)){
            throw new LinfengException("充值方案不存在");
        }
        if(!param.getPayType().equals("yue")){
            throw new LinfengException("不是余额支付订单");
        }
        //校验余额是否足够
        AppUserEntity user = userService.getById(uid);
        if(vipOption.getPrice().compareTo(user.getMoney()) > 0){
            throw new LinfengException("账户余额不足");
        }
        //更新会员状态
        if(user.getVip().equals(Constant.VIP_USER) && ObjectUtil.isNotNull(user.getVipExpireTime())){
            //续费用户
            String toStr = DateUtil.dateToStr(user.getVipExpireTime(), "yyyy-MM-dd HH:mm:ss");
            user.setVipExpireTime(DateUtil.addDay(toStr,vipOption.getValidDays()));
        }else{
            user.setVip(Constant.VIP_USER);
            user.setVipExpireTime(DateUtil.addDay(DateUtil.nowDateTimeStr(),vipOption.getValidDays()));
        }
        boolean b = userService.saveOrUpdate(user);
        if(!b){
            throw new LinfengException("会员信息更新失败");
        }
        //余额支付
        BigDecimal balance = user.getMoney().subtract(vipOption.getPrice());

        boolean update = userService.lambdaUpdate()
                .set(AppUserEntity::getMoney, balance)
                .eq(AppUserEntity::getUid, user.getUid())
                .update();
        if(!update){
            throw new LinfengException("余额扣除失败");
        }
        //删除用户信息缓存
        redisUtils.delete(RedisKeys.getUserCacheKey(user.getUid()));
        //记录用户充值
        UserRechargeEntity entity=new UserRechargeEntity();
        String orderSn = IdUtil.getSnowflake(0L, 0L).nextIdStr();
        Date date = DateUtil.nowDateTime();
        entity.setNickname(user.getUsername());
        entity.setOrderId(orderSn);
        entity.setUid(uid);
        entity.setPrice(vipOption.getPrice());
        entity.setGivePrice(new BigDecimal(vipOption.getId()));//这个参数在会员充值中用于存储充值方案id
        entity.setRechargeType(param.getPayType());
        entity.setPaid(Constant.RECHARGE_PAY);
        entity.setPayTime(date);
        entity.setAddTime(date);
        entity.setType(1);
        boolean save = userRechargeService.save(entity);
        if(!save){
            throw new LinfengException("用户充值错误");
        }
        //更新账单记录
        String mark="用户会员充值余额消费"+vipOption.getPrice()+"元";
        billService.expend(uid.longValue(),BillDetailEnum.TYPE_16.getDesc(),BillDetailEnum.CATEGORY_1.getValue(),BillDetailEnum.TYPE_16.getValue(),vipOption.getPrice().doubleValue(),user.getMoney().doubleValue(),mark,"");

    }

}