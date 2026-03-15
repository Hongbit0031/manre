/**
 * -----------------------------------
 *  Copyright (c) 2022-2026
 *  All rights reserved, Designed By www.linfengtech.cn
 * 林风社交论坛商业版本请务必保留此注释头信息
 * 商业版授权联系技术客服	 QQ:  3582996245
 * 严禁分享、盗用、转卖源码或非法牟利！
 * 版权所有 ，侵权必究！
 * -----------------------------------
 */
package io.linfeng.modules.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.common.vo.app.AppFriendResponse;
import io.linfeng.modules.app.entity.FriendEntity;
import io.linfeng.modules.app.param.ClearChatMessageUnreadForm;

import java.util.List;
import java.util.Map;

/**
 * 好友模块
 *
 * @author JL.Yu
 * @email linfengtech001@163.com
 * @date 2022-11-16 14:05:06
 */
public interface FriendService extends IService<FriendEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<AppFriendResponse> getFriendList(Integer uid);

    void clearUnread(ClearChatMessageUnreadForm param);

    Boolean checkIsFriend(Integer uid, Integer uid1);

    void deleteFriend(Integer friendId, Integer myId);

    void applyFriend(Object data);

    void agreePersonApply(Integer id);
}

