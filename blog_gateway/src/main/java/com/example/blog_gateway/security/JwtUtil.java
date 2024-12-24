package com.example.blog_gateway.security;

import com.auth0.jwt.JWT;          // JWT 工具类
import com.auth0.jwt.JWTVerifier;    // JWT 验证器
import com.auth0.jwt.algorithms.Algorithm;  // JWT 算法
import com.auth0.jwt.interfaces.DecodedJWT;  // 解码后的 JWT 接口
import org.springframework.beans.factory.annotation.Value;  // 用于注入配置属性

import org.springframework.stereotype.Component;  // 标记为 Spring 组件

import java.util.Date;  // 日期类

@Component
public class JwtUtil {

    // 从 application.properties 中注入 JWT 密钥
    @Value("${jwt.secret}")
    private String secretKey;

    // 从 application.properties 中注入 JWT 过期时间（毫秒）
    @Value("${jwt.expiration-milliseconds}")
    private long expirationTime;



    /**
     * 从 JWT 令牌中提取用户名
     *
     * @param token JWT 令牌
     * @return 提取的用户名
     */
    public String extractUsername(String token) {
        DecodedJWT decodedJWT = decodeToken(token);  // 解码 JWT 令牌
        return decodedJWT.getSubject();  // 返回令牌中的主题（用户名）
    }

    /**
     * 从 JWT 令牌中提取过期时间
     *
     * @param token JWT 令牌
     * @return 提取的过期时间
     */
    public Date extractExpiration(String token) {
        DecodedJWT decodedJWT = decodeToken(token);  // 解码 JWT 令牌
        return decodedJWT.getExpiresAt();  // 返回令牌的过期时间
    }

    /**
     * 解码 JWT 令牌
     *
     * @param token JWT 令牌
     * @return 解码后的 JWT 对象
     */
    private DecodedJWT decodeToken(String token) {
        // 创建 JWT 验证器
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretKey)).build();
        return verifier.verify(token);  // 验证并解码 JWT 令牌
    }

    /**
     * 检查 JWT 令牌是否过期
     *
     * @param token JWT 令牌
     * @return 如果令牌过期则返回 true，否则返回 false
     */
    public Boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);  // 提取令牌的过期时间
        return expiration.before(new Date());  // 检查过期时间是否在当前时间之前
    }

    /**
     * 验证 JWT 令牌
     *
     * @param token JWT 令牌
     * @return 如果令牌有效则返回 true，否则返回 false
     */
    public Boolean validateToken(String token) {

        return !isTokenExpired(token);
    }
}
