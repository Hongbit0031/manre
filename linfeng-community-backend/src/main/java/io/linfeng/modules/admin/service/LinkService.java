package io.linfeng.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.utils.AppPageUtils;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.modules.admin.entity.LinkEntity;
import io.linfeng.modules.app.param.LinkListForm;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-26 14:05:38
 */
public interface LinkService extends IService<LinkEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<LinkEntity> getPageList(LinkListForm request);
}

