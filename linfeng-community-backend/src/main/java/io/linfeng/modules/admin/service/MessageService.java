package io.linfeng.modules.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.linfeng.common.vo.app.MessageNumberResponse;
import io.linfeng.common.vo.app.MessageListResponse;
import io.linfeng.common.vo.app.AppSystemMessageResponse;
import io.linfeng.common.utils.AppPageUtils;
import io.linfeng.common.utils.PageUtils;
import io.linfeng.modules.admin.entity.AppUserEntity;
import io.linfeng.modules.admin.entity.MessageEntity;
import io.linfeng.modules.admin.param.AddMessageParam;
import io.linfeng.modules.app.param.ChatListForm;
import io.linfeng.modules.app.param.MessageReadForm;
import io.linfeng.modules.app.param.UpdateSystemNoticeStatusForm;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-26 13:15:30
 */
public interface MessageService extends IService<MessageEntity> {

    PageUtils queryPage(Map<String, Object> params);

    MessageNumberResponse getMessageNumber();

    AppPageUtils<MessageListResponse> queryMessageList(Integer type, Integer page, AppUserEntity user);

    @Async
    void sendMessage(Integer fromUid,Integer toUid,Integer postId,Integer type,String content,String title);

    void sendMessageNotAsync(Integer fromUid,Integer toUid,Integer postId,Integer type,String content,String title);

    Boolean status(Integer type, Integer uid);

    Boolean articleMsgState(UpdateSystemNoticeStatusForm request, Integer uid);

    void readMessage(MessageReadForm request, Integer uid);

    void readAllWatchInfo(Integer uid);

    void deleteMessageByMonth(Integer month);

    void deleteSomeMessageByDay(Integer day);

    AppPageUtils<MessageEntity> getSystemList(Integer uid, Integer page);

    void updateSystemStatus(Integer uid);

    void delSystemMsg(Integer uid);

    void saveMessage(AddMessageParam param);

    List<AppSystemMessageResponse> systemInfoList(ChatListForm request, AppUserEntity user);

    AppPageUtils<AppSystemMessageResponse> systemInfoListByPc(ChatListForm request, AppUserEntity user);

}

