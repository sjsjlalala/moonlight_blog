package com.example.blog_common.util;

import com.example.blog_common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @description: 通用用户redis工具类
 * @author: moki
 * @date: 2024/12/28 20:16
 **/
@Component
public class CommonUserRedisUtil {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String TOKEN_PREFIX = "token:";
    private static final String USER_PREFIX = "user:";


    public String getUsernameByToken(String token) {
        return redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
    }

    public User getUserByToken(String token) {
        String userString = redisTemplate.opsForValue().get(USER_PREFIX + token);
        // 这里假设Myuser类有一个静态方法fromString来反序列化字符串
        return User.fromString(userString);
    }

}
