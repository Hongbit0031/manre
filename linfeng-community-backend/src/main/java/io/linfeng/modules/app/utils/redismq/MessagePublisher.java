package io.linfeng.modules.app.utils.redismq;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

/**
 * 消息发布者
 * @author linfeng
 * @date 2024/7/17 17:05
 */
@Component
public class MessagePublisher {
    private final RedisTemplate<String, String> redisTemplate;
    private final ChannelTopic topic;

    public MessagePublisher(RedisTemplate<String, String> redisTemplate, ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    public void publishMessage(String message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
