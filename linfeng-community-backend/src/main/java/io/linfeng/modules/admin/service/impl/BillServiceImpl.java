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

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.linfeng.common.enums.BillDetailEnum;
import io.linfeng.common.enums.BillEnum;
import io.linfeng.common.enums.BillInfoEnum;
import io.linfeng.common.exception.LinfengException;
import io.linfeng.common.utils.*;
import io.linfeng.common.vo.admin.AdminBillInfoResponse;
import io.linfeng.common.vo.app.AppBillResponse;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.entity.PostEntity;
import io.linfeng.modules.admin.service.*;
import io.linfeng.modules.app.param.AddRewardForm;
import io.linfeng.modules.app.param.ExchangeForm;
import io.linfeng.modules.app.param.ExchangeIntegralForm;
import io.linfeng.modules.app.param.GetBillListForm;
import io.linfeng.modules.sys.service.SysConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.linfeng.modules.admin.dao.BillDao;
import io.linfeng.modules.admin.entity.BillEntity;
import org.springframework.transaction.annotation.Transactional;


@Service("billService")
public class BillServiceImpl extends ServiceImpl<BillDao, BillEntity> implements BillService {

    @Autowired
    private BillDao billDao;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private AppUserService userService;
    @Autowired
    private SysConfigService configService;
    @Autowired
    private UserLevelService userLevelService;
    @Autowired
    private PostService postService;
    @Autowired
    private MessageService messageService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<BillEntity> queryWrapper=new QueryWrapper<>();

        //条件查询
        String uid = (String)params.get("key");
        String pm = (String)params.get("type");
        String category = (String)params.get("type2");
        if(!WechatUtil.isEmpty(category)){
            queryWrapper.lambda().eq(BillEntity::getCategory,category);
        }
        if(!WechatUtil.isEmpty(pm)){
            queryWrapper.lambda().eq(BillEntity::getPm,Integer.valueOf(pm));
        }

        if(NumberUtil.isInteger(uid)&&!WechatUtil.isEmpty(uid)){
            queryWrapper.lambda().eq(BillEntity::getUid,Integer.valueOf(uid));
        }
        queryWrapper.lambda().orderByDesc(BillEntity::getId);
        IPage<BillEntity> page = this.page(
                new Query<BillEntity>().getPage(params),
                queryWrapper
        );
        List<BillEntity> records = page.getRecords();
        List<AdminBillInfoResponse> list=new ArrayList<>();
        records.forEach(item->{
            AdminBillInfoResponse response =new AdminBillInfoResponse();
            BeanUtils.copyProperties(item,response);
            AppUserEntity user = userService.getById(item.getUid());
            response.setUser(user);
            response.setAvatar(user.getAvatar());
            response.setUsername(user.getUsername());
            list.add(response);
        });
        PageUtils pageUtils=new PageUtils(page);
        pageUtils.setList(list);
        return pageUtils;
    }

    /**
     * 增加支出流水
     * @param uid uid
     * @param title 账单标题
     * @param category 明细种类
     * @param type 明细类型
     * @param number 明细数字
     * @param balance 剩余
     * @param mark 备注
     */
    @Override
    public void expend(Long uid,String title,String category,String type,double number,double balance,String mark,String linkId){
        BillEntity userBill = BillEntity.builder()
                .uid(uid.intValue())
                .title(title)
                .category(category)
                .type(type)
                .number(BigDecimal.valueOf(number))
                .balance(BigDecimal.valueOf(balance))
                .mark(mark)
                .pm(BillEnum.PM_0.getValue())
                .addTime(DateUtil.nowDateTime())
                .linkId(linkId)
                .build();

        int insert = billDao.insert(userBill);
        if(insert!=1){
            throw new LinfengException("增加支出流水失败");
        }
    }

    /**
     * 增加收入/支入流水
     * @param uid uid
     * @param title 账单标题
     * @param category 明细种类
     * @param type 明细类型
     * @param number 明细数字
     * @param balance 剩余
     * @param mark 备注
     * @param linkid 关联id
     */
    @Override
    public void income(Long uid,String title,String category,String type,double number,
                       double balance,String mark,String linkid){
        BillEntity userBill = BillEntity.builder()
                .uid(uid.intValue())
                .title(title)
                .category(category)
                .type(type)
                .number(BigDecimal.valueOf(number))
                .balance(BigDecimal.valueOf(balance))
                .mark(mark)
                .pm(BillEnum.PM_1.getValue())
                .linkId(linkid)
                .addTime(DateUtil.nowDateTime())
                .build();

        int insert = billDao.insert(userBill);
        if(insert!=1){
            throw new LinfengException("增加支入流水失败");
        }
    }

    /**
     * 判断是否支付
     * 用户自己看自己的付费贴不需要判断
     * @param postId
     * @param userId
     * @return
     */
    @Override
    public boolean vipPostIsPay(Integer postId, Integer userId) {
        //会员查看付费贴无需支付(需要的话打开注释代码即可)
//        AppUserEntity user = userService.getById(userId);
//        if(user.getVip().equals(Constant.VIP_USER)){
//            return true;
//        }
        BillEntity bill = this.lambdaQuery()
                .eq(BillEntity::getLinkId, postId)
                .eq(BillEntity::getUid, userId)
                .eq(BillEntity::getPm, BillEnum.PM_0.getValue())
                .eq(BillEntity::getType, BillDetailEnum.TYPE_3.getValue())
                .one();
        return bill!=null;
    }

    @Override
    public AppPageUtils<AppBillResponse> billList(GetBillListForm request, AppUserEntity user) {
        QueryWrapper<BillEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(BillEntity::getUid,user.getUid()).orderByDesc(BillEntity::getId);
        switch (BillInfoEnum.toType(request.getType())){
            case PAY:
                wrapper.lambda().eq(BillEntity::getCategory,BillDetailEnum.CATEGORY_1.getValue());
                wrapper.lambda().eq(BillEntity::getPm, BillEnum.PM_0.getValue());
                break;
            case RECHAREGE:
                wrapper.lambda().eq(BillEntity::getCategory,BillDetailEnum.CATEGORY_1.getValue());
                wrapper.lambda().eq(BillEntity::getPm, BillEnum.PM_1.getValue());
                break;
            case EXTRACT:
                wrapper.lambda().eq(BillEntity::getCategory,BillDetailEnum.CATEGORY_1.getValue());
                wrapper.lambda().eq(BillEntity::getType,BillDetailEnum.TYPE_4.getValue());
                break;
            case SIGN_INTEGRAL:
                wrapper.lambda().eq(BillEntity::getCategory,BillDetailEnum.CATEGORY_2.getValue());
                wrapper.lambda().eq(BillEntity::getType,BillDetailEnum.TYPE_10.getValue());
                break;
            default:
                wrapper.lambda().eq(BillEntity::getCategory,BillDetailEnum.CATEGORY_1.getValue());
        }
        Page<BillEntity> page = new Page<>(request.getPage(),request.getLimit());
        Page<BillEntity> pages = this.page(page, wrapper);
        List<AppBillResponse> responses = DataConvertUtils.sourceToTarget(pages.getRecords(), AppBillResponse.class);
        return new AppPageUtils<>(pages, responses);
    }

    @Override
    public BigDecimal getAllPay(Integer userId) {
        double sum=billDao.getAllPay(userId);
        return new BigDecimal(sum);
    }

    @Override
    public List<BillEntity> getIntegralList(Integer uid, Integer page, Integer limit,Integer type) {
        LambdaQueryWrapper<BillEntity> wrapper = new LambdaQueryWrapper<>();
        if(type==1){
            wrapper.eq(BillEntity::getPm,0);
        }else if (type==2){
            wrapper.eq(BillEntity::getPm,1);
        }
        wrapper.eq(BillEntity::getStatus, 1)
                .eq(BillEntity::getUid, uid)
                .eq(BillEntity::getCategory, BillDetailEnum.CATEGORY_2.getValue())
                .orderByDesc(BillEntity::getId);
        Page<BillEntity> pageModel = new Page<>(page, limit);
        IPage<BillEntity> pageList = billDao.selectPage(pageModel, wrapper);
        return pageList.getRecords();
    }

    /**
     * 获取用户消耗的积分
     * @param uid
     * @return
     */
    @Override
    public Integer getUsedIntegral(Integer uid) {

        return billDao.getUsedIntegral(uid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exchange(AppUserEntity users, ExchangeForm request) {
        String isOpen = configService.getValue(Constant.EXCHANGE);
        if(isOpen.equals("1")){
            throw new LinfengException("积分兑换余额的开关已关闭");
        }
        AppUserEntity user = userService.getById(users.getUid());
        String integral = configService.getValue(Constant.INTEGRAL);
        if(request.getRechargeValue()*Integer.valueOf(integral)>user.getIntegral()){
            throw new LinfengException("兑换积分不足");
        }

        //更新积分和余额并删除缓存
        BigDecimal add = new BigDecimal(request.getRechargeValue()).add(user.getMoney());
        Integer sub=user.getIntegral()-(int) (request.getRechargeValue()*Integer.valueOf(integral));
        boolean update = userService.lambdaUpdate()
                .set(AppUserEntity::getMoney, add)
                .set(AppUserEntity::getIntegral, sub)
                .eq(AppUserEntity::getUid, user.getUid())
                .update();
        if(!update){
            throw new LinfengException("用户信息更新失败");
        }
        redisUtils.delete(RedisKeys.getUserCacheKey(user.getUid()));
        //插入积分扣除的账单
        this.expend(user.getUid().longValue(),BillDetailEnum.TYPE_11.getDesc(),
                BillDetailEnum.CATEGORY_2.getValue(),BillDetailEnum.TYPE_11.getValue(),
                request.getRechargeValue()*Integer.valueOf(integral),user.getIntegral(),"积分兑换余额扣除积分","");
        //插入余额增加的账单
        this.income(user.getUid().longValue(),BillDetailEnum.TYPE_12.getDesc(),
                BillDetailEnum.CATEGORY_1.getValue(),BillDetailEnum.TYPE_12.getValue(),
                request.getRechargeValue(),user.getMoney().doubleValue(),"积分兑换余额增加余额","");
        userLevelService.checkUserLevel(user.getUid());

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rewardIntegral(AppUserEntity user, AddRewardForm request) {
        if(WechatUtil.isEmpty(request.getRewardCount())){
            throw new LinfengException("请输入打赏数量");
        }
        AppUserEntity appUser = userService.getById(user.getUid());
        if (appUser.getIntegral() < request.getRewardCount()){
            throw new LinfengException("你的账户积分不足");
        }
        PostEntity post = postService.getById(request.getPostId());
        if(post.getUid().equals(user.getUid())){
            throw new LinfengException("不能打赏自己");
        }
        if(request.getRewardCount()>5000){
            throw new LinfengException("打赏数目过大,请重选");
        }
        if(request.getRewardCount()<=0){
            throw new LinfengException("打赏数目不合法");
        }

        boolean update = userService.lambdaUpdate()
                .set(AppUserEntity::getIntegral, appUser.getIntegral()-request.getRewardCount())
                .eq(AppUserEntity::getUid, user.getUid())
                .update();
        if(!update){
            throw new LinfengException("打赏用户信息更新失败");
        }
        AppUserEntity appUserEntity = userService.getById(post.getUid());
        boolean update2 = userService.lambdaUpdate()
                .set(AppUserEntity::getIntegral, appUserEntity.getIntegral()+request.getRewardCount())
                .eq(AppUserEntity::getUid, post.getUid())
                .update();
        if(!update2){
            throw new LinfengException("被打赏用户信息更新失败");
        }
        redisUtils.delete(RedisKeys.getUserCacheKey(post.getUid()));
        redisUtils.delete(RedisKeys.getUserCacheKey(user.getUid()));
        //检查等级LV
        userLevelService.checkUserLevel(user.getUid());
        userLevelService.checkUserLevel(post.getUid());
        //打赏用户 插入积分消耗的账单
        this.expend(user.getUid().longValue(),BillDetailEnum.TYPE_20.getDesc(),
                BillDetailEnum.CATEGORY_2.getValue(),BillDetailEnum.TYPE_20.getValue(),
                request.getRewardCount(),appUser.getIntegral(),"打赏扣除积分","");
        //被打赏用户 插入积分增加的账单
        this.income(post.getUid().longValue(),BillDetailEnum.TYPE_19.getDesc(),
                BillDetailEnum.CATEGORY_2.getValue(),BillDetailEnum.TYPE_19.getValue(),
                request.getRewardCount(),appUserEntity.getIntegral(),"打赏增加积分","");


        String content = StrUtil.format(Constant.REWARD_NOTICE,user.getUsername(),post.getTitle(),request.getRewardCount());
        messageService.sendMessageNotAsync(0,post.getUid(),post.getId(),Constant.SYSMSG,content,Constant.TITLE_REWARD);
    }

    /**
     * 余额兑换积分
     * @param users
     * @param request
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exchangeToIntegral(AppUserEntity users, ExchangeIntegralForm request) {
        String isOpen = configService.getValue(Constant.YUE_TO_INT);
        if(isOpen.equals("1")){
            throw new LinfengException("余额兑换积分的开关已关闭");
        }
        AppUserEntity user = userService.getById(users.getUid());
        String ratio = configService.getValue(Constant.YUE_TO_INT_RATIO);
        BigDecimal rechargeValueBigDecimal = BigDecimal.valueOf(request.getRechargeValue());
        BigDecimal ratioBigDecimal = BigDecimal.valueOf(Integer.valueOf(ratio));
        BigDecimal needMoney = rechargeValueBigDecimal.divide(ratioBigDecimal, 2, RoundingMode.HALF_UP);
        if(needMoney.compareTo(user.getMoney())>0){
            throw new LinfengException("用户余额不足");
        }
        //更新用户信息并清理缓存
        boolean update = userService.lambdaUpdate()
                .set(AppUserEntity::getMoney, user.getMoney().subtract(needMoney))
                .set(AppUserEntity::getIntegral, request.getRechargeValue()+user.getIntegral())
                .eq(AppUserEntity::getUid, user.getUid())
                .update();
        if(!update){
            throw new LinfengException("用户信息更新失败");
        }
        redisUtils.delete(RedisKeys.getUserCacheKey(user.getUid()));
        //插入余额扣除的账单
        this.expend(user.getUid().longValue(),BillDetailEnum.TYPE_21.getDesc(),
                BillDetailEnum.CATEGORY_1.getValue(),BillDetailEnum.TYPE_21.getValue(),
                needMoney.doubleValue(),user.getMoney().doubleValue(),"余额兑换积分扣除余额","");
        //插入积分增加的账单
        this.income(user.getUid().longValue(),BillDetailEnum.TYPE_22.getDesc(),
                BillDetailEnum.CATEGORY_2.getValue(),BillDetailEnum.TYPE_22.getValue(),
                request.getRechargeValue(),user.getIntegral().doubleValue(),"余额兑换积分增加积分","");
        userLevelService.checkUserLevel(user.getUid());
    }

}