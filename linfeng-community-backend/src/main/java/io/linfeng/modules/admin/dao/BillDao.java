package io.linfeng.modules.admin.dao;

import io.linfeng.modules.admin.entity.BillEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户账单表
 * 
 * @author linfeng
 * @email linfengtech001@163.com
 * @date 2022-04-20 20:46:48
 */
@Mapper
public interface BillDao extends BaseMapper<BillEntity> {

    @Select("select IFNULL(sum(price),0) from lf_user_recharge " +
            "where paid=1 and uid=#{uid}")
    double getAllPay(@Param("uid")Integer uid);

    @Select("select IFNULL(sum(number),0) from lf_bill " +
            "where category='integral' and pm='0' and uid=#{uid}")
    Integer getUsedIntegral(@Param("uid")Integer uid);
}
