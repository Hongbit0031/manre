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

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.linfeng.common.enums.BillDetailEnum;
import io.linfeng.common.exception.LinfengException;
import io.linfeng.common.vo.admin.AdminUserSignResponse;
import io.linfeng.common.vo.app.SignResponse;
import io.linfeng.common.vo.app.SignUserResponse;
import io.linfeng.common.utils.*;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.entity.BillEntity;
import io.linfeng.modules.admin.entity.SignConfigEntity;
import io.linfeng.modules.admin.service.*;
import io.linfeng.modules.sys.service.SysConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.linfeng.modules.admin.dao.UserSignDao;
import io.linfeng.modules.admin.entity.UserSignEntity;
import org.springframework.transaction.annotation.Transactional;


@Service("userSignService")
public class UserSignServiceImpl extends ServiceImpl<UserSignDao, UserSignEntity> implements UserSignService {


    @Autowired
    private UserSignDao userSignDao;
    @Autowired
    private AppUserService userService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private BillService billService;
    @Autowired
    private SignConfigService signConfigService;
    @Autowired
    private SysConfigService configService;
    @Autowired
    private UserLevelService userLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<UserSignEntity> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(UserSignEntity::getId);
        IPage<UserSignEntity> page = this.page(
                new Query<UserSignEntity>().getPage(params),
                queryWrapper
        );
        List<UserSignEntity> records = page.getRecords();
        List<AdminUserSignResponse> list=new ArrayList<>();
        records.forEach(item->{
            AdminUserSignResponse response =new AdminUserSignResponse();
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
     * 获取签到用户信息
     * @param appUser
     * @return
     */
    @Override
    public SignUserResponse getUserInfo(AppUserEntity appUser) {
        AppUserEntity currentUser = userService.getById(appUser.getUid());
        AppUserEntity user = userService.vipExpirationCheck(currentUser);
        SignUserResponse vo = new SignUserResponse();
        BeanUtils.copyProperties(user, vo);
        int sumSignDay = this.getSignSumDay(user.getUid().longValue());
        boolean isDaySign = this.getToDayIsSign(user.getUid().longValue());
        boolean isYestDaySign = this.getYestDayIsSign(user.getUid().longValue());
        vo.setSumSignDay(sumSignDay);
        vo.setIsDaySign(isDaySign);
        vo.setIsYestDaySign(isYestDaySign);
        if (!isDaySign && !isYestDaySign) {
            vo.setSignNum(0);
        }
        return vo;
    }

    @Override
    public List<SignResponse> getSignList(Integer uid, Integer page, Integer limit) {
        Page<BillEntity> pageModel = new Page<>(page, limit);
        return userSignDao.getSignList(uid, pageModel);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int sign(AppUserEntity appUser) {
        AppUserEntity user = userService.getById(appUser.getUid());
        boolean isDaySign = this.getToDayIsSign(user.getUid().longValue());
        if (isDaySign) {
            throw new LinfengException("签到过啦");
        }
        List<SignConfigEntity> list = this.getConfigList();
        int signNumber = 0; //积分
        int userSignNum = user.getSignNum(); //签到次数
        if (this.getYestDayIsSign(user.getUid().longValue())) {
            if (user.getSignNum() > (list.size() - 1)) {
                userSignNum = 0;
            }
        } else {
            userSignNum = 0;
        }
        int index = 0;
        for (SignConfigEntity response : list) {
            if (index == userSignNum) {
                signNumber = Integer.valueOf(response.getSignNum());
                break;
            }
            index++;
        }
        userSignNum += 1;
        //会员签到积分翻倍奖励


        if (user.getVip().equals(Constant.VIP_USER)) {
            String value = configService.getValue(Constant.VIP_INTEGRAL);
            Integer multiple = Integer.valueOf(value);
            if (multiple > 0) {
                signNumber = signNumber * multiple;
            }
        }
        //积分信息新增
        UserSignEntity userSign = new UserSignEntity();
        userSign.setUid(user.getUid().longValue());
        String title = "签到奖励";
        if (userSignNum == list.size()) {
            title = "连续签到奖励";
        }
        userSign.setTitle(title);
        userSign.setNumber(signNumber);
        userSign.setBalance(user.getIntegral());
        userSign.setCreateTime(io.linfeng.common.utils.DateUtil.nowDateTime());
        userSignDao.insert(userSign);
        //更新用户积分信息
        boolean update = userService.lambdaUpdate()
                .set(AppUserEntity::getIntegral, user.getIntegral() + signNumber)
                .set(AppUserEntity::getSignNum, userSignNum)
                .eq(AppUserEntity::getUid, user.getUid())
                .update();
        if (!update) {
            throw new LinfengException("用户积分更新失败");
        }
        redisUtils.delete(RedisKeys.getUserCacheKey(user.getUid()));
        //积分账单插入
        billService.income(user.getUid().longValue(), title, BillDetailEnum.CATEGORY_2.getValue(),
                BillDetailEnum.TYPE_10.getValue(), signNumber, user.getIntegral().doubleValue(),
                "签到积分奖励", "");
        userLevelService.checkUserLevel(user.getUid());
        return signNumber;
    }

    @Override
    public List<SignConfigEntity> getConfigList() {
        return signConfigService.lambdaQuery()
                .orderByAsc(SignConfigEntity::getSort)
                .list();
    }

    /**
     * 今天签到用户数
     * @return
     */
    @Override
    public Integer getSignUserCount() {
        Date today = DateUtil.beginOfDay(new Date());
        return this.lambdaQuery()
                .ge(UserSignEntity::getCreateTime, today)
                .count()
                .intValue();
    }

    /**
     * 用户累计签到次数
     * @param uid 用户id
     * @return int
     */
    private int getSignSumDay(Long uid) {
        return this.lambdaQuery().eq(UserSignEntity::getUid, uid).count().intValue();
    }

    /**
     * 用户昨天是否签到
     * @param uid uid
     * @return boolean
     */
    private boolean getYestDayIsSign(Long uid) {
        Date today = DateUtil.beginOfDay(new Date());
        Date yesterday = DateUtil.beginOfDay(DateUtil.yesterday());

        int count = this.lambdaQuery().eq(UserSignEntity::getUid, uid)
                .lt(UserSignEntity::getCreateTime, today)
                .ge(UserSignEntity::getCreateTime, yesterday)
                .count()
                .intValue();
        return count > 0;
    }

    /**
     * 用户今天是否签到
     * @param uid uid
     * @return boolean
     */
    private boolean getToDayIsSign(Long uid) {
        Date today = DateUtil.beginOfDay(new Date());
        int count = this.lambdaQuery().eq(UserSignEntity::getUid, uid)
                .ge(UserSignEntity::getCreateTime, today)
                .count()
                .intValue();
        return count > 0;
    }

}