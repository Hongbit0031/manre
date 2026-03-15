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

import cn.hutool.core.net.NetUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.jsonwebtoken.Claims;
import io.linfeng.common.utils.DateUtil;
import io.linfeng.common.utils.SnowFlakeUtil;
import io.linfeng.modules.app.entity.ChatMessageEntity;
import io.linfeng.modules.app.entity.FriendEntity;
import io.linfeng.modules.app.entity.NoticeEntity;
import io.linfeng.modules.app.service.ChatMessageService;
import io.linfeng.modules.app.service.FriendService;
import io.linfeng.modules.app.service.NoticeService;
import io.linfeng.modules.app.utils.JwtUtils;
import io.linfeng.modules.app.websocket.constant.MessageConstant;
import io.linfeng.modules.app.websocket.entity.SocketMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息通信核心业务模块
 *
 * @author linfeng
 */
@Slf4j
@Component
@ServerEndpoint("/app/socket/{token}")
public class SocketServer {

    private static final String WEBSOCKET_SESSION_KEY = "websocket:session:";
    private static final String WEBSOCKET_ONLINE_USERS_KEY = "websocket:online:users";
    private static final String WEBSOCKET_INSTANCE_SESSIONS_KEY = "websocket:instance:sessions:";
    private static final String WEBSOCKET_INSTANCE_CHANNEL_PREFIX = "websocket:instance:";
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    private static String INSTANCE_ID;

    @PostConstruct
    public void init() {
        String localIp = NetUtil.getLocalhostStr();
        INSTANCE_ID = localIp + ":" + serverPort;

        // 清理可能残留的当前实例会话信息
        cleanupStaleSessionsOnStartup();

        log.info("WebSocket服务器实例ID: {}", INSTANCE_ID);
    }

    /**
     * 清理启动时残留的会话信息
     * 使用SCAN命令替代KEYS命令，避免阻塞Redis
     */
    private void cleanupStaleSessionsOnStartup() {
        if (redisTemplate == null || INSTANCE_ID == null) {
            return;
        }
        
        try {
            String instanceSessionsKey = WEBSOCKET_INSTANCE_SESSIONS_KEY + INSTANCE_ID;
            Set<Object> userIds = redisTemplate.opsForSet().members(instanceSessionsKey);
            
            if (userIds != null && !userIds.isEmpty()) {
                for (Object userIdObj : userIds) {
                    String userId = (String) userIdObj;
                    String sessionKey = WEBSOCKET_SESSION_KEY + userId;
                    String sessionInfoStr = (String) redisTemplate.opsForValue().get(sessionKey);
                    
                    if (sessionInfoStr != null) {
                        JSONObject sessionInfo = JSONUtil.parseObj(sessionInfoStr);
                        String instanceId = sessionInfo.getStr("instanceId");
                        // 确认是当前实例的会话，清理残留记录
                        if (INSTANCE_ID.equals(instanceId)) {
                            redisTemplate.delete(sessionKey);
                            redisTemplate.opsForSet().remove(WEBSOCKET_ONLINE_USERS_KEY, userId);
                            redisTemplate.opsForSet().remove(instanceSessionsKey, userId);
                        }
                    }
                }
            }
            
            // 清理实例会话集合（如果为空）
            redisTemplate.delete(instanceSessionsKey);
            
            log.info("清理启动时残留会话完成");
        } catch (Exception e) {
            log.error("清理启动时残留会话失败: {}", e.getMessage(), e);
        }
    }

    // 本地实例的会话存储
    private static final Map<String, Session> localSessionMap = new ConcurrentHashMap<>();
    
    private static RedisTemplate<String, Object> redisTemplate;
    private static JwtUtils jwtUtils;
    private static ChatMessageService chatMessageService;
    private static NoticeService noticeService;
    private static FriendService friendService;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        SocketServer.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setChatMessageService(ChatMessageService chatMessageService) {
        SocketServer.chatMessageService = chatMessageService;
    }

    @Autowired
    public void setFriendService(FriendService friendService) {
        SocketServer.friendService = friendService;
    }

    @Autowired
    public void setJwtUtils(JwtUtils jwtUtils) {
        SocketServer.jwtUtils = jwtUtils;
    }

    @Autowired
    public void setNoticeService(NoticeService noticeService) {
        SocketServer.noticeService = noticeService;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        String userId;
        try {
            if (jwtUtils == null) {
                log.error("jwtUtils is null");
                this.sendMessage(new SocketMessage<>(MessageConstant.TOKEN_FAILED, null), session);
                return;
            }
            Claims claims = jwtUtils.getClaimByToken(token);
            if (claims == null) {
                throw new Exception("身份验证失败");
            }
            userId = claims.getSubject();
        } catch (Exception e) {
//            log.error("WebSocket连接错误: {}", e.getMessage());
            this.sendMessage(new SocketMessage<>(MessageConstant.TOKEN_FAILED, null), session);
            return;
        }

        try {
            if (redisTemplate == null) {
                String errorMsg = "Redis未正确配置，无法支持集群部署";
                log.error(errorMsg);
//                this.sendMessage(new SocketMessage<>(MessageConstant.TOKEN_FAILED, errorMsg), session);
                return;
            }

            // 存储本地会话
            localSessionMap.put(userId, session);
            
            // 在Redis中记录用户会话信息
            JSONObject sessionInfo = new JSONObject();
            sessionInfo.set("instanceId", INSTANCE_ID);
            sessionInfo.set("lastActiveTime", System.currentTimeMillis());
            String sessionKey = WEBSOCKET_SESSION_KEY + userId;
            redisTemplate.opsForValue().set(sessionKey, sessionInfo.toString());
            
            // 添加到在线用户集合
            redisTemplate.opsForSet().add(WEBSOCKET_ONLINE_USERS_KEY, userId);
            
            // 添加到当前实例的会话集合（用于快速清理）
            String instanceSessionsKey = WEBSOCKET_INSTANCE_SESSIONS_KEY + INSTANCE_ID;
            redisTemplate.opsForSet().add(instanceSessionsKey, userId);
            
            // 获取所有在线用户
            Set<Object> onlineUsers = redisTemplate.opsForSet().members(WEBSOCKET_ONLINE_USERS_KEY);
            this.sendToAll(new SocketMessage<>(MessageConstant.COUNT, onlineUsers != null ? onlineUsers.toArray() : new Object[0]));
            
            log.info("用户:{}上线", userId);
        } catch (Exception e) {
            log.error("处理WebSocket连接时发生错误: {}", e.getMessage());
            this.sendMessage(new SocketMessage<>(MessageConstant.TOKEN_FAILED, "服务器内部错误"), session);
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("token") String token) {
        try {
            Claims claims = jwtUtils.getClaimByToken(token);
            if (claims == null) {
                return;
            }
            String userId = claims.getSubject();
            
            if (redisTemplate == null) {
                log.error("Redis未配置，无法正确处理用户下线");
                return;
            }

            // 清理本地会话
            localSessionMap.remove(userId);
            
            // 清理Redis记录
            String sessionKey = WEBSOCKET_SESSION_KEY + userId;
            redisTemplate.delete(sessionKey);
            redisTemplate.opsForSet().remove(WEBSOCKET_ONLINE_USERS_KEY, userId);
            
            // 从当前实例的会话集合中移除
            String instanceSessionsKey = WEBSOCKET_INSTANCE_SESSIONS_KEY + INSTANCE_ID;
            redisTemplate.opsForSet().remove(instanceSessionsKey, userId);
            
            // 获取更新后的在线用户列表
            Set<Object> onlineUsers = redisTemplate.opsForSet().members(WEBSOCKET_ONLINE_USERS_KEY);
            this.sendToAll(new SocketMessage<>(MessageConstant.COUNT, onlineUsers != null ? onlineUsers.toArray() : new Object[0]));
            
            log.info("用户:{}离线", userId);
        } catch (Exception e) {
            log.error("处理WebSocket关闭时发生错误: {}", e.getMessage());
        }
    }

    @OnError
    public void onError(Session session, Throwable error, @PathParam("token") String token) {
//        log.error("WebSocket发生错误: {}", error.getMessage());
        onClose(session, token);
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("token") String token) {
        JSONObject msg = JSONUtil.parseObj(message);
        JSONObject data = msg.getJSONObject("data");
        switch (msg.getStr("type")) {
            /*接收心跳**/
            case MessageConstant.PING:
                // 更新用户最后活跃时间
                try {
                    Claims claims = jwtUtils.getClaimByToken(token);
                    if (claims != null) {
                        String userId = claims.getSubject();
                        updateUserLastActiveTime(userId);
                    }
                } catch (Exception e) {
                    log.debug("更新心跳时间失败: {}", e.getMessage());
                }
                break;
            /*私聊消息**/
            case MessageConstant.PERSON_MESSAGE:
                ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                        .sessionId(data.getStr("sessionId"))
                        .senderId(data.getStr("senderId"))
                        .receiverId(data.getStr("receiverId"))
                        .sendTime(data.getStr("sendTime"))
                        .content(data.getStr("content"))
                        .messageType(data.getStr("messageType"))
                        .isWithdrawn(0)
                        .updateTime(DateUtil.nowDateTime())
                        .build();
                chatMessageService.saveMessage(chatMessage);
                this.sendToUserById(chatMessage.getSenderId(), new SocketMessage<>(MessageConstant.PERSON_MESSAGE, chatMessage));
                this.sendToUserById(chatMessage.getReceiverId(), new SocketMessage<>(MessageConstant.PERSON_MESSAGE, chatMessage));
                break;
            /*私聊撤回消息**/
            case MessageConstant.PERSON_WITHDRAW:
                String messageId = data.getStr("messageId");
                String senderId = data.getStr("senderId");
                String receiverId = data.getStr("receiverId");
                chatMessageService.withdrawMessage(messageId);
                this.sendToUserById(senderId, new SocketMessage<>(MessageConstant.PERSON_WITHDRAW, data));
                this.sendToUserById(receiverId, new SocketMessage<>(MessageConstant.PERSON_WITHDRAW, data));
                break;
            /*申请好友消息**/
            case MessageConstant.PERSON_APPLY:
                NoticeEntity notice = NoticeEntity.builder()
                        .senderId(data.getLong("senderId"))
                        .receiverId(data.getLong("receiverId"))
                        .type(MessageConstant.PERSON_APPLY)
                        .information(msg.getStr("data"))
                        .isRead(false)
                        .createTime(DateUtil.nowDateTime())
                        .updateTime(DateUtil.nowDateTime())
                        .build();
                noticeService.save(notice);
                this.sendToUserById(data.getStr("receiverId"), new SocketMessage<>(MessageConstant.PERSON_APPLY, msg.getStr("data")));
                break;
            /*申请好友通过消息**/
            case MessageConstant.PERSON_APPLY_AGREE:
                NoticeEntity noticeInfo = noticeService.getById(data.getStr("id"));
                JSONObject jsonObject = JSONUtil.parseObj(noticeInfo.getInformation());
                Boolean isFriend = friendService.checkIsFriend(noticeInfo.getReceiverId().intValue(), noticeInfo.getSenderId().intValue());
                if (isFriend) {
                    noticeInfo.setIsRead(true);
                    noticeService.saveOrUpdate(noticeInfo);
                    this.sendToUserById(noticeInfo.getReceiverId().toString(), new SocketMessage<>(MessageConstant.NOTICE_REFRESH, "TA已经是你的好友啦"));
                    break;
                }
                Long sessionId = SnowFlakeUtil.getSnowFlakeId();
                FriendEntity friend1 = FriendEntity.builder()
                        .myId(noticeInfo.getSenderId())
                        .friendId(noticeInfo.getReceiverId())
                        .notation(jsonObject.getStr("notation"))
                        .sessionId(sessionId)
                        .lastMessage(MessageConstant.DEFAULT_LAST_MESSAGE)
                        .unread(MessageConstant.NOT_READ)
                        .isHidden(false)
                        .createTime(DateUtil.nowDateTime())
                        .updateTime(DateUtil.nowDateTime())
                        .build();
                FriendEntity friend2 = FriendEntity.builder()
                        .myId(noticeInfo.getReceiverId())
                        .friendId(noticeInfo.getSenderId())
                        .notation(jsonObject.getStr("senderName"))
                        .sessionId(sessionId)
                        .lastMessage(MessageConstant.DEFAULT_LAST_MESSAGE)
                        .unread(MessageConstant.NOT_READ)
                        .isHidden(false)
                        .createTime(DateUtil.nowDateTime())
                        .updateTime(DateUtil.nowDateTime())
                        .build();
                friendService.save(friend1);
                friendService.save(friend2);
                noticeInfo.setIsRead(true);
                noticeService.saveOrUpdate(noticeInfo);
                SocketMessage<FriendEntity> socketMessage = new SocketMessage<>(MessageConstant.PERSON_APPLY_AGREE, friend2);
                this.sendToUserById(noticeInfo.getSenderId().toString(), socketMessage);
                this.sendToUserById(noticeInfo.getReceiverId().toString(), socketMessage);
                break;

        }

    }

    private void sendToAll(SocketMessage<?> message) {
        try {
            if (redisTemplate == null) {
                log.error("Redis未配置，无法发送广播消息");
                return;
            }

            // 获取所有在线用户
            Set<Object> onlineUsers = redisTemplate.opsForSet().members(WEBSOCKET_ONLINE_USERS_KEY);
            if (onlineUsers != null) {
                for (Object userObj : onlineUsers) {
                    String userId = (String) userObj;
                    String sessionInfoStr = (String) redisTemplate.opsForValue().get(WEBSOCKET_SESSION_KEY + userId);
                    
                    if (sessionInfoStr != null) {
                        JSONObject sessionInfo = JSONUtil.parseObj(sessionInfoStr);
                        String instanceId = sessionInfo.getStr("instanceId");
                        // 如果用户在当前实例上
                        if (INSTANCE_ID.equals(instanceId)) {
                            Session session = localSessionMap.get(userId);
                            if (session != null) {
                                this.sendMessage(message, session);
                            } else {
                                // 本地会话不存在，清理Redis中的记录
                                log.warn("用户 {} 的本地会话不存在，清理Redis记录", userId);
                                redisTemplate.delete(WEBSOCKET_SESSION_KEY + userId);
                                redisTemplate.opsForSet().remove(WEBSOCKET_ONLINE_USERS_KEY, userId);
                            }
                        } else {
                            // 用户在其他实例，通过Redis Pub/Sub发送消息
                            sendToRemoteInstance(userId, instanceId, message);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("发送全局消息时发生错误: {}", e.getMessage());
        }
    }

    private void sendToUserById(String userId, SocketMessage<?> message) {
        try {
            if (redisTemplate == null) {
                log.error("Redis未配置，无法发送用户消息");
                return;
            }

            String sessionInfoStr = (String) redisTemplate.opsForValue().get(WEBSOCKET_SESSION_KEY + userId);
            
            if (sessionInfoStr != null) {
                JSONObject sessionInfo = JSONUtil.parseObj(sessionInfoStr);
                String instanceId = sessionInfo.getStr("instanceId");
                // 如果用户在当前实例上
                if (INSTANCE_ID.equals(instanceId)) {
                    Session session = localSessionMap.get(userId);
                    if (session != null) {
                        this.sendMessage(message, session);
                    } else {
                        // 本地会话不存在，清理Redis中的记录
                        log.warn("用户 {} 的本地会话不存在，清理Redis记录", userId);
                        redisTemplate.delete(WEBSOCKET_SESSION_KEY + userId);
                        redisTemplate.opsForSet().remove(WEBSOCKET_ONLINE_USERS_KEY, userId);
                    }
                } else {
                    // 用户在其他实例，通过Redis Pub/Sub发送消息
                    sendToRemoteInstance(userId, instanceId, message);
                }
            }
        } catch (Exception e) {
            log.error("发送用户消息时发生错误: {}", e.getMessage());
        }
    }

    private void sendMessage(SocketMessage<?> message, Session toSession) {
        try {
            if (toSession.isOpen()) {
                synchronized (toSession) {
                    toSession.getBasicRemote().sendText(JSONUtil.toJsonStr(message));
                }
            }
        } catch (Exception e) {
            log.error("发送消息时发生错误: {}", e.getMessage());
        }
    }

    /**
     * 发送消息到远程实例（通过Redis Pub/Sub）
     * 
     * @param userId 目标用户ID
     * @param targetInstanceId 目标实例ID
     * @param message 要发送的消息
     */
    private void sendToRemoteInstance(String userId, String targetInstanceId, SocketMessage<?> message) {
        try {
            if (redisTemplate == null) {
                log.error("Redis未配置，无法发送跨实例消息");
                return;
            }

            // 构建跨实例消息
            JSONObject crossInstanceMessage = new JSONObject();
            crossInstanceMessage.set("userId", userId);
            crossInstanceMessage.set("message", JSONUtil.toJsonStr(message));

            // 发布到目标实例的频道
            String channel = WEBSOCKET_INSTANCE_CHANNEL_PREFIX + targetInstanceId;
            redisTemplate.convertAndSend(channel, crossInstanceMessage.toString());
            
            log.debug("跨实例消息已发送到实例 {}，用户 {}", targetInstanceId, userId);
        } catch (Exception e) {
            log.error("发送跨实例消息失败，用户: {}, 实例: {}, 错误: {}", userId, targetInstanceId, e.getMessage(), e);
        }
    }

    /**
     * 更新用户最后活跃时间
     * 
     * @param userId 用户ID
     */
    private void updateUserLastActiveTime(String userId) {
        try {
            if (redisTemplate == null) {
                return;
            }
            
            String sessionKey = WEBSOCKET_SESSION_KEY + userId;
            String sessionInfoStr = (String) redisTemplate.opsForValue().get(sessionKey);
            
            if (sessionInfoStr != null) {
                JSONObject sessionInfo = JSONUtil.parseObj(sessionInfoStr);
                // 确认是本实例的会话才更新
                if (INSTANCE_ID.equals(sessionInfo.getStr("instanceId"))) {
                    sessionInfo.set("lastActiveTime", System.currentTimeMillis());
                    redisTemplate.opsForValue().set(sessionKey, sessionInfo.toString());
                }
            }
        } catch (Exception e) {
            log.debug("更新用户最后活跃时间失败: {}", e.getMessage());
        }
    }

    /**
     * 获取本地会话（供WebSocketMessageSubscriber使用）
     * 
     * @param userId 用户ID
     * @return Session对象，如果不存在返回null
     */
    public static Session getLocalSession(String userId) {
        return localSessionMap.get(userId);
    }

    /**
     * 获取当前实例ID（供配置类使用）
     * 
     * @return 实例ID，如果未初始化返回null
     */
    public static String getInstanceId() {
        return INSTANCE_ID;
    }

}
