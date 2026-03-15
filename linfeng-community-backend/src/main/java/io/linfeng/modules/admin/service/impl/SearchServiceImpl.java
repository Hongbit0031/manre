package io.linfeng.modules.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.linfeng.common.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.linfeng.modules.admin.dao.SearchDao;
import io.linfeng.modules.admin.entity.SearchEntity;
import io.linfeng.modules.admin.service.SearchService;


@Service("searchService")
public class SearchServiceImpl extends ServiceImpl<SearchDao, SearchEntity> implements SearchService {

    @Autowired
    private SearchDao searchDao;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SearchEntity> page = this.page(
                new Query<SearchEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<String> selectHotSearch() {
        String result = redisUtils.get(ConfigConstant.HOT_SEARCH_KEY);
        if(WechatUtil.isEmpty(result)){
            List<String> list = searchDao.selectHotSearch();
            redisUtils.set(ConfigConstant.HOT_SEARCH_KEY,JSON.toJSON(list).toString(),60 * 60);
            return list;
        }
        return JSONObject.parseArray(result, String.class);
    }

    @Override
    public void deleteSearchByUId(Integer uid) {
        LambdaQueryWrapper<SearchEntity> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SearchEntity::getUid,uid);
        this.remove(queryWrapper);
    }

    @Override
    public void setSearchContent(String keyword, Integer uid) {
        List<SearchEntity> list = this.lambdaQuery()
                .eq(SearchEntity::getUid, uid)
                .eq(SearchEntity::getContent, keyword)
                .list();
        if(list.size()==0){
            SearchEntity search=new SearchEntity();
            search.setContent(keyword);
            search.setUid(uid.longValue());
            search.setUpdateTime(new Date());
            this.save(search);
        }else{
            SearchEntity search = list.get(0);
            search.setUpdateTime(new Date());
            this.updateById(search);
        }


    }

    @Override
    public List<SearchEntity> getSearchListByUid(Integer uid) {
        return this.lambdaQuery()
                .eq(SearchEntity::getUid,uid.longValue())
                .list();
    }
}