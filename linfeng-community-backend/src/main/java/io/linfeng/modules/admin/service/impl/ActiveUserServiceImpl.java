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

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.linfeng.common.exception.LinfengException;
import io.linfeng.common.utils.*;
import io.linfeng.common.vo.admin.AdminActiveUserResponse;
import io.linfeng.common.vo.admin.VisitorInfoResponse;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.service.AppUserService;
import io.linfeng.modules.app.param.VisitorForm;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.linfeng.modules.admin.dao.ActiveUserDao;
import io.linfeng.modules.admin.entity.ActiveUserEntity;
import io.linfeng.modules.admin.service.ActiveUserService;


@Service("activeUserService")
public class ActiveUserServiceImpl extends ServiceImpl<ActiveUserDao, ActiveUserEntity> implements ActiveUserService {

    @Autowired
    private AppUserService appUserService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<ActiveUserEntity> queryWrapper =new LambdaQueryWrapper<>();


        String key = (String) params.get("key");
        String type = (String) params.get("type");
        String ter = (String) params.get("ter");
        String orders = (String) params.get("orders");
        if (!WechatUtil.isEmpty(type)) {
            queryWrapper.eq(ActiveUserEntity::getType, Integer.parseInt(type));
        }
        if (!WechatUtil.isEmpty(ter)) {
            queryWrapper.eq(ActiveUserEntity::getTerminal, ter);
        }
        if (!WechatUtil.isEmpty(orders)) {
            if(Integer.parseInt(orders)==1){
                queryWrapper.orderByDesc(ActiveUserEntity::getUpdateTime);
            }else{
                queryWrapper.orderByDesc(ActiveUserEntity::getId);
            }
        }else{
            queryWrapper.orderByDesc(ActiveUserEntity::getId);
        }
        if (!WechatUtil.isEmpty(key)) {
            if (NumberUtil.isInteger(key)) {
                queryWrapper.eq(ActiveUserEntity::getUid, key);
            } else {
                queryWrapper.eq(ActiveUserEntity::getIp, key);
            }
        }
        IPage<ActiveUserEntity> page = this.page(
                new Query<ActiveUserEntity>().getPage(params),
                queryWrapper
        );
        List<ActiveUserEntity> list = page.getRecords();

        List<AdminActiveUserResponse> responseList=new ArrayList<>();
        list.forEach(item->{
            AdminActiveUserResponse response=new AdminActiveUserResponse();
            BeanUtils.copyProperties(item,response);
            if(item.getUid()>0){
                AppUserEntity user = appUserService.getById(item.getUid());
                response.setAvatar(user.getAvatar());
                response.setUsername(user.getUsername());
            }
            responseList.add(response);
        });
        PageUtils pageUtils=new PageUtils(page);
        pageUtils.setList(responseList);
        return pageUtils;
    }


    @Override
    public void addVisit(String ip,VisitorForm param,AppUserEntity user) {

        LocalDate today = LocalDate.now();
        LocalDateTime todayMidnight = LocalDateTime.of(today, LocalDateTime.MIN.toLocalTime());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = todayMidnight.format(formatter);
        //已登录用户
        if(user!=null){
            List<ActiveUserEntity> list = this.lambdaQuery()
                    .eq(ActiveUserEntity::getIp, ip)
                    .eq(ActiveUserEntity::getUid, user.getUid())
                    .eq(ActiveUserEntity::getTerminal, param.getTerminal())
                    .ge(ActiveUserEntity::getCreateTime, formattedDateTime)
                    .list();
            //已经有记录则更新记录
            if(list.size()>0){
                ActiveUserEntity activeUser = list.get(0);
                activeUser.setUpdateTime(new Date());
                activeUser.setActiveCount(activeUser.getActiveCount()+1);
                this.updateById(activeUser);
            }else{
                //没访问记录则插入记录
                ActiveUserEntity info=new ActiveUserEntity();
                info.setIp(ip);
                info.setCreateTime(new Date());
                info.setUpdateTime(new Date());
                info.setTerminal(param.getTerminal());
                info.setUid(user.getUid());
                info.setType(1);
                String address = IPUtil.getCityInfo(ip);
                info.setAddress(address);
                this.save(info);
            }
        }else{
            //未登录游客
            List<ActiveUserEntity> list = this.lambdaQuery()
                    .eq(ActiveUserEntity::getIp, ip)
                    .eq(ActiveUserEntity::getUid, 0)
                    .eq(ActiveUserEntity::getTerminal, param.getTerminal())
                    .ge(ActiveUserEntity::getCreateTime, formattedDateTime)
                    .list();
            //已经有记录则更新记录
            if(list.size()>0){
                ActiveUserEntity activeUser = list.get(0);
                activeUser.setUpdateTime(new Date());
                activeUser.setActiveCount(activeUser.getActiveCount()+1);
                this.updateById(activeUser);
            }else{
                //没访问记录则插入记录
                ActiveUserEntity info=new ActiveUserEntity();
                info.setIp(ip);
                info.setCreateTime(new Date());
                info.setUpdateTime(new Date());
                info.setTerminal(param.getTerminal());
                info.setUid(0);
                info.setType(0);
                String address = IPUtil.getCityInfo(ip);
                info.setAddress(address);
                this.save(info);
            }
        }
    }

    @Override
    public VisitorInfoResponse visitData() {
        VisitorInfoResponse response=new VisitorInfoResponse();
        String today = cn.hutool.core.date.DateUtil.date().toString("yyyy-MM-dd");
        String yesterday = cn.hutool.core.date.DateUtil.yesterday().toString("yyyy-MM-dd");

        response.setNewRegisterUserCount(appUserService.getRegisterNumByDate(today));
        response.setYesterdayNewUserNum(appUserService.getRegisterNumByDate(yesterday));
        LocalDate now = LocalDate.now();
        LocalDateTime startTime = LocalDateTime.of(now, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(now, LocalTime.MAX);
        List<ActiveUserEntity> list = this.lambdaQuery().between(ActiveUserEntity::getCreateTime, startTime, endTime).list();
        long todayIpCount = list.stream()
                .map(ActiveUserEntity::getIp)
                .distinct()
                .count();
        long todayUserCount = list.stream()
                .map(ActiveUserEntity::getUid)
                .distinct()
                .count();
        response.setTodayIp(todayIpCount);
        response.setTodayUser(todayUserCount);


        LocalDate localDate = LocalDate.now().minus(1, ChronoUnit.DAYS);
        LocalDateTime startTime2 = LocalDateTime.of(localDate, LocalTime.MIN);
        LocalDateTime endTime2 = LocalDateTime.of(localDate, LocalTime.MAX);
        List<ActiveUserEntity> list2 = this.lambdaQuery().between(ActiveUserEntity::getCreateTime, startTime2, endTime2).list();
        long yesterdayIpCount = list2.stream()
                .map(ActiveUserEntity::getIp)
                .distinct()
                .count();
        long yesterdayUserCount = list2.stream()
                .map(ActiveUserEntity::getUid)
                .distinct()
                .count();
        response.setYesterdayIp(yesterdayIpCount);
        response.setYesterdayUser(yesterdayUserCount);

        response.setTotalActiveToday(list.size());
        response.setTotalActiveYesterday(list2.size());

        return response;
    }

    @Override
    public void cleanRecordByDay(Integer day) {
        String date = DateUtils.addDateDays(new Date(), -day);
        LambdaQueryWrapper<ActiveUserEntity> wrapper=new LambdaQueryWrapper<>();
        wrapper.le(ActiveUserEntity::getCreateTime,date);
        this.remove(wrapper);
    }

    @Override
    public String getAddressByIp(Map<String, Object> params) {
        String ip = (String) params.get("ip");
        String id = (String) params.get("id");
        String address = IPUtil.getCityInfo(ip);
        if(!WechatUtil.isEmpty(address)){
            boolean update = this.lambdaUpdate()
                    .set(ActiveUserEntity::getAddress, address)
                    .eq(ActiveUserEntity::getId, id)
                    .update();
            if(!update){
                throw new LinfengException("更新失败");
            }
        }
        return address;
    }

}