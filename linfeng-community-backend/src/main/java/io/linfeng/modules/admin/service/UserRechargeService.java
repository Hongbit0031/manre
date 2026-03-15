package io.linfeng.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.vo.app.OrderPay;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.entity.UserRechargeEntity;
import io.linfeng.modules.app.param.PayVipPostForm;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 用户充值
 *
 * @author linfeng
 * @email linfengtech001@163.com
 * @date 2022-04-19 19:27:33
 */
public interface UserRechargeService extends IService<UserRechargeEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 添加充值记录
     * @param user 用户
     * @param price 充值金额
     * @param paidPrice 赠送金额
     * @Param type 支付类型
     */
    String addRecharge(AppUserEntity user, String price, String paidPrice,String type);

    OrderPay goToPay(String orderSn, BigDecimal money, String ip, AppUserEntity user, String type) throws Exception;

    void handleWxLog(String orderId, String transaction_id, String orderNo);

    void payVipPost(PayVipPostForm param,AppUserEntity user);

    double rechargeMoney();

    double rechargeMoneyByMonth();
}

