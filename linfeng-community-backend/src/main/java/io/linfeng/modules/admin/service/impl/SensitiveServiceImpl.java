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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.linfeng.common.exception.LinfengException;
import io.linfeng.common.utils.*;
import io.linfeng.modules.admin.entity.CategoryEntity;
import io.linfeng.modules.app.utils.baidu.censor.AppCensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.linfeng.modules.admin.dao.SensitiveDao;
import io.linfeng.modules.admin.entity.SensitiveEntity;
import io.linfeng.modules.admin.service.SensitiveService;


@Service("sensitiveService")
public class SensitiveServiceImpl extends ServiceImpl<SensitiveDao, SensitiveEntity> implements SensitiveService {


    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private AppCensorService appCensorService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SensitiveEntity> page = this.page(
                new Query<SensitiveEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }


    /**
     * 文本审核包括 百度审核 以及 后台自定义敏感词审核
     * @param content
     */
    @Override
    public void checkContent(String content) {
        appCensorService.censorText(content);

        List<SensitiveEntity> list = getSensitiveList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == null || list.get(i).getSensitiveWord().isEmpty()) {
                return;
            }
            if (list.get(i).getState().equals(Constant.SENSITIVE_CLOSE)) {
                return;
            }
            //校验是否存在敏感词
            String[] split = list.get(i).getSensitiveWord().split(",");
            for (String word : split) {
                if (content.contains(word)) {
                    //如果存在敏感词 直接打回
                    throw new LinfengException("内容含敏感词禁止发布");
                }
            }
        }
    }

    @Override
    public void checkImage(String url) {
        appCensorService.censorImage(url);
    }

    @Override
    public boolean checkTxt(String content) {
        boolean result = appCensorService.censorTextCheck(content);
        if(result){
            List<SensitiveEntity> list = getSensitiveList();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == null || list.get(i).getSensitiveWord().isEmpty()) {
                    return true;
                }
                if (list.get(i).getState().equals(Constant.SENSITIVE_CLOSE)) {
                    return true;
                }
                //校验是否存在敏感词
                String[] split = list.get(i).getSensitiveWord().split(",");
                for (String word : split) {
                    if (content.contains(word)) {
                        //如果存在敏感词 直接打回
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean checkImg(String url) {
        return appCensorService.censorImageCheck(url);
    }

    @Override
    public boolean checkVideo(String url) {
        return appCensorService.censorVideoCheck(url);
    }

    /**
     * 缓存获取敏感词列表
     * @return
     */
    private List<SensitiveEntity> getSensitiveList(){
        String result = redisUtils.get(ConfigConstant.SENSETIVE_LIST_KEY);
        if(WechatUtil.isEmpty(result)){
            List<SensitiveEntity> list = this.lambdaQuery().list();
            redisUtils.set(ConfigConstant.SENSETIVE_LIST_KEY,JSON.toJSON(list).toString(),60 * 60 * 24);
            return list;
        }
        return JSONObject.parseArray(result, SensitiveEntity.class);
    }

}