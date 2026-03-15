package io.linfeng.common.utils;

/**
 * Redis所有系统配置的Key添加前缀
 *
 */
public class RedisKeys {

    public static String getSysConfigKey(String key){
        return "sys:config:" + key;
    }

    public static String getPostKey(Integer postId){
        return "post:info:" + postId;
    }

    public static String getTopicKey(Integer topicId){
        return "topic:info:" + topicId;
    }

    public static String getDiscussKey(Integer id){
        return "discuss:info:" + id;
    }

    /**
     * 粉丝关注量缓存
     * @param uid 用户ID
     * @return 缓存key
     */
    public static String getUserKey(Integer uid){
        return "user:info:" + uid;
    }

    /**
     * 用户登录信息缓存
     * @param uid 用户ID
     * @return 缓存key
     */
    public static String getUserCacheKey(Integer uid){
        return "userId:" + uid;
    }
}
