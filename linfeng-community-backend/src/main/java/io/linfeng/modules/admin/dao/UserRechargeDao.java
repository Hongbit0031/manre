package io.linfeng.modules.admin.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import io.linfeng.common.vo.admin.ChartDataResponse;
import io.linfeng.modules.admin.entity.UserRechargeEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * 用户充值
 * 
 * @author linfeng
 * @email linfengtech001@163.com
 * @date 2022-04-19 19:27:33
 */
@Mapper
public interface UserRechargeDao extends BaseMapper<UserRechargeEntity> {


    @Select("select IFNULL(sum(price),0) from lf_user_recharge " +
            "where paid=1")
    double rechargeMoney();


    @Select("SELECT IFNULL(sum(price),0) " +
            " FROM lf_user_recharge ${ew.customSqlSegment}")
    double rechargeMoneyByMonth(@Param(Constants.WRAPPER) Wrapper<UserRechargeEntity> wrapper);

    @Select("SELECT IFNULL(sum(price),0) as num," +
            "DATE_FORMAT(add_time, '%m-%d') as time " +
            " FROM lf_user_recharge where paid=1 and add_time >= #{time}" +
            " GROUP BY DATE_FORMAT(add_time,'%Y-%m-%d') " +
            " ORDER BY add_time ASC")
    List<ChartDataResponse> chartList(Date nowMonth);
}
