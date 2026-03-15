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
package io.linfeng.config;

import io.linfeng.modules.app.websocket.component.SocketServer;
import io.linfeng.modules.app.websocket.component.WebSocketMessageSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * WebSocket Redis配置
 * 用于配置跨实例消息传递的Redis Pub/Sub
 *
 * @author linfeng
 */
@Slf4j
@Configuration
public class WebSocketRedisConfig implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private WebSocketMessageSubscriber webSocketMessageSubscriber;

    @Value("${server.port:8080}")
    private String serverPort;

    private RedisMessageListenerContainer webSocketMessageContainer;

    /**
     * 创建WebSocket消息监听容器
     * 注意：频道订阅在init()方法中动态完成
     */
    @Bean
    public RedisMessageListenerContainer webSocketMessageContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        this.webSocketMessageContainer = container;
        return container;
    }

    /**
     * 初始化时订阅当前实例的频道
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        subscribeToInstanceChannelWithRetry();
    }

    /**
     * 订阅当前实例的频道
     */
    private void subscribeToInstanceChannelWithRetry() {
        int maxRetry = 5;
        int delayMs = 1000;
        for (int i = 0; i < maxRetry; i++) {
            String instanceId = getInstanceId();
            if (instanceId != null) {
                doSubscribe(instanceId);
                return;
            }
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        log.warn("无法获取实例ID，WebSocket跨实例消息功能可能无法正常工作");
    }

    private void doSubscribe(String instanceId) {
        try {
            String channel = "websocket:instance:" + instanceId;
            ChannelTopic topic = new ChannelTopic(channel);
            MessageListenerAdapter adapter = new MessageListenerAdapter(webSocketMessageSubscriber);

            webSocketMessageContainer.addMessageListener(adapter, topic);
            log.info("WebSocket实例 {} 已订阅频道: {}", instanceId, channel);
        } catch (Exception e) {
            log.error("订阅WebSocket实例频道时发生错误: {}", e.getMessage(), e);
        }
    }

    /**
     * 获取当前实例ID
     */
    private String getInstanceId() {
        // 直接调用SocketServer的静态方法
        return SocketServer.getInstanceId();
    }
}

