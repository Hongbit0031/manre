package io.linfeng.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.modules.admin.entity.SearchEntity;

import java.util.List;
import java.util.Map;

/**
 * 用户历史搜索信息表
 *
 * @author JL.Yu
 * @email linfengtech001@163.com
 * @date 2023-04-26 20:13:08
 */
public interface SearchService extends IService<SearchEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<String> selectHotSearch();

    void deleteSearchByUId(Integer uid);

    void setSearchContent(String keyword,Integer uid);

    List<SearchEntity> getSearchListByUid(Integer uid);
}

