package io.linfeng.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.utils.AppPageUtils;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.vo.app.AppBillResponse;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.entity.BillEntity;
import io.linfeng.modules.app.param.AddRewardForm;
import io.linfeng.modules.app.param.ExchangeForm;
import io.linfeng.modules.app.param.ExchangeIntegralForm;
import io.linfeng.modules.app.param.GetBillListForm;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户账单表
 *
 * @author linfeng
 * @email linfengtech001@163.com
 * @date 2022-04-20 20:46:48
 */
public interface BillService extends IService<BillEntity> {

    PageUtils queryPage(Map<String, Object> params);


    void expend(Long uid,String title,String category,String type,double number,double balance,String mark,String linkId);


    void income(Long uid,String title,String category,String type,double number,
                double balance,String mark,String linkid);

    boolean vipPostIsPay(Integer postId,Integer userId);

    AppPageUtils<AppBillResponse> billList(GetBillListForm request, AppUserEntity user);

    BigDecimal getAllPay(Integer userId);

    List<BillEntity> getIntegralList(Integer uid, Integer page, Integer limit,Integer type);

    Integer getUsedIntegral(Integer uid);

    void exchange(AppUserEntity user, ExchangeForm request);

    void rewardIntegral(AppUserEntity user, AddRewardForm request);

    void exchangeToIntegral(AppUserEntity user, ExchangeIntegralForm request);
}

