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
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.linfeng.common.vo.admin.AdminLuckdrawRecordResponse;
import io.linfeng.common.vo.app.LuckdrawRecordResponse;
import io.linfeng.common.utils.Constant;
import io.linfeng.common.utils.WechatUtil;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.service.AppUserService;
import io.linfeng.modules.sys.service.SysConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.utils.Query;

import io.linfeng.modules.admin.dao.LuckdrawRecordDao;
import io.linfeng.modules.admin.entity.LuckdrawRecordEntity;
import io.linfeng.modules.admin.service.LuckdrawRecordService;


@Service("luckdrawRecordService")
public class LuckdrawRecordServiceImpl extends ServiceImpl<LuckdrawRecordDao, LuckdrawRecordEntity> implements LuckdrawRecordService {

    @Autowired
    private AppUserService appUserService;
    @Autowired
    private LuckdrawRecordDao luckdrawRecordDao;
    @Autowired
    private SysConfigService configService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<LuckdrawRecordEntity> queryWrapper=new QueryWrapper<>();
        //条件查询
        String key = (String)params.get("key");
        String type = (String)params.get("type");
        if(NumberUtil.isInteger(key)){
            if(!WechatUtil.isEmpty(key)){
                queryWrapper.lambda().eq(LuckdrawRecordEntity::getUserId,Integer.parseInt(key));
            }
        }
        if(!WechatUtil.isEmpty(type)){
            queryWrapper.lambda().eq(LuckdrawRecordEntity::getPrizeType, Integer.parseInt(type));
        }
        queryWrapper.lambda().orderByDesc(LuckdrawRecordEntity::getId);

        IPage<LuckdrawRecordEntity> page = this.page(
                new Query<LuckdrawRecordEntity>().getPage(params),
                queryWrapper
        );

        List<LuckdrawRecordEntity> records = page.getRecords();
        List<AdminLuckdrawRecordResponse> recordResponses=new ArrayList<>();
        records.forEach(e->{
            AdminLuckdrawRecordResponse response=new AdminLuckdrawRecordResponse();
            BeanUtils.copyProperties(e,response);
            AppUserEntity user = appUserService.getById(e.getUserId());
            response.setUser(user);
            response.setAvatar(user.getAvatar());
            recordResponses.add(response);
        });
        PageUtils pageUtils=new PageUtils(page);
        pageUtils.setList(recordResponses);
        return pageUtils;
    }

    /**
     * 历史抽奖记录最多最新10条
     * 谢谢惠顾排除
     * @return
     */
    @Override
    public List<LuckdrawRecordResponse> getLuckDrawRecordList() {
        List<LuckdrawRecordEntity> list = this.lambdaQuery()
                .ne(LuckdrawRecordEntity::getPrizeType, Constant.LUCK_THANKS)
                .orderByDesc(LuckdrawRecordEntity::getId)
                .last("limit 10")
                .list();
        List<LuckdrawRecordResponse> responseList=new ArrayList<>();
        list.forEach(e->{
            LuckdrawRecordResponse response=new LuckdrawRecordResponse();
            response.setCreateTime(e.getCreateTime());
            response.setId(e.getId());
            String text="";
            String username = appUserService.getById(e.getUserId()).getUsername();
            if(e.getPrizeType().equals(Constant.LUCK_INTEGRAL)){
                text="恭喜"+username+"抽中"+e.getNumber()+e.getPrizeName();
            }else if(e.getPrizeType().equals(Constant.LUCK_MONEY)){
                text="恭喜"+username+"抽中"+e.getNumber()+"元"+e.getPrizeName();
            }else if(e.getPrizeType().equals(Constant.LUCK_DIY)){
                text="恭喜"+username+"抽中"+e.getNumber()+"件"+e.getPrizeName();
            }
            response.setText(text);
            responseList.add(response);
        });
        return responseList;

    }

    /**
     * 查询该用户今天剩余 抽奖次数
     * @param user
     * @return
     */
    public Integer getSurplus(AppUserEntity user){
        String today = cn.hutool.core.date.DateUtil.date().toString("yyyy-MM-dd");
        QueryWrapper<LuckdrawRecordEntity> wrapper = Wrappers.query();
        wrapper.select("id");
        wrapper.apply("date_format(create_time, '%Y-%m-%d') = {0}", today);
        wrapper.eq("user_id",user.getUid());
        Integer count = luckdrawRecordDao.selectCount(wrapper).intValue();
        String surplus = configService.getValue(Constant.SURPLUS);
        return Integer.valueOf(surplus)-count;
    }

}