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
package io.linfeng.modules.app.websocket.component;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.linfeng.modules.app.websocket.entity.SocketMessage;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket跨实例消息订阅者（集群）
 * 用于接收其他实例发送的WebSocket消息
 * @author linfengtech
 * @date 2026/2/4
 */
@Slf4j
@Component
public class WebSocketMessageSubscriber implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channel = new String(message.getChannel());
            String body = new String(message.getBody());

            // 解析消息格式：{"userId": "123", "message": {...}}
            JSONObject messageObj = JSONUtil.parseObj(body);
            String userId = messageObj.getStr("userId");
            String messageStr = messageObj.getStr("message");

            if (userId == null || messageStr == null) {
                log.warn("收到格式错误的跨实例消息: {}", body);
                return;
            }

            // 从SocketServer获取本地会话
            Session session = SocketServer.getLocalSession(userId);
            if (session != null && session.isOpen()) {
                // 直接发送消息
                synchronized (session) {
                    session.getBasicRemote().sendText(messageStr);
                }
                log.debug("跨实例消息已发送给用户: {}", userId);
            } else {
                log.warn("用户 {} 的本地会话不存在或已关闭，无法发送跨实例消息", userId);
            }
        } catch (Exception e) {
            log.error("处理跨实例WebSocket消息时发生错误: {}", e.getMessage(), e);
        }
    }
}

