package com.example.blog_auth.redis;

import com.example.blog_auth.model.Myuser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
/*
 * @description:
 * @author: moki
 * @date: 2024/12/25 11:35
 * @param: 用于维护在线token和用户表的redis工具类
 * @return:
 **/
@Service
public class TokenRedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String TOKEN_PREFIX = "token:";
    private static final String USER_PREFIX = "user:";
    private static final Long TOKEN_EXPIRATION = 315360000L; // 令牌过期时间，单位分钟

    public void saveToken(String token, String username) {
        redisTemplate.opsForValue().set(TOKEN_PREFIX + token, username, TOKEN_EXPIRATION, TimeUnit.MINUTES);
    }

    public void saveUser(String token, Myuser user) {
        redisTemplate.opsForValue().set(USER_PREFIX + token, user.toString(), TOKEN_EXPIRATION, TimeUnit.MINUTES);
    }

    public void refreshToken(String token) {
        redisTemplate.expire(TOKEN_PREFIX + token, TOKEN_EXPIRATION, TimeUnit.MINUTES);
        redisTemplate.expire(USER_PREFIX + token, TOKEN_EXPIRATION, TimeUnit.MINUTES);
    }

    public void deleteToken(String token) {
        redisTemplate.delete(TOKEN_PREFIX + token);
        redisTemplate.delete(USER_PREFIX + token);
    }

    public String getUsernameByToken(String token) {
        return redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
    }

    public Myuser getUserByToken(String token) {
        String userString = redisTemplate.opsForValue().get(USER_PREFIX + token);
        // 这里假设Myuser类有一个静态方法fromString来反序列化字符串
        return Myuser.fromString(userString);
    }

    public boolean isTokenValid(String token) {
        return redisTemplate.hasKey(TOKEN_PREFIX + token);
    }
}
