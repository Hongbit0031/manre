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
package io.linfeng.modules.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.modules.app.entity.UserTopicEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-23 21:24:46
 */
public interface UserTopicService extends IService<UserTopicEntity> {

    PageUtils queryPage(Map<String, Object> params);


    Integer findUserTopicService(Integer topicId);


    Boolean isJoin(Integer uid, Integer id);

    List<Integer> getUidByTopicId(Integer id);

    List<Integer> getSomeUidListByTopicId(Integer id);

    List<UserTopicEntity> getTopicIdByUid(Integer uid);
}

