package com.example.blog_auth.security;

import com.auth0.jwt.JWT;          // JWT 工具类
import com.auth0.jwt.JWTVerifier;    // JWT 验证器
import com.auth0.jwt.algorithms.Algorithm;  // JWT 算法
import com.auth0.jwt.interfaces.DecodedJWT;  // 解码后的 JWT 接口
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blog_common.service.impl.UserServiceImpl;
import org.example.base.response.CommonResponse;
import org.example.base.enums.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;  // 用于注入配置属性
import org.springframework.security.core.userdetails.UserDetails;  // 用户详情接口
import org.springframework.stereotype.Component;  // 标记为 Spring 组件

import java.util.Date;  // 日期类

@Component
public class JwtUtil {
    @Autowired
    private UserServiceImpl userService;

    // 从 application.properties 中注入 JWT 密钥
    @Value("${jwt.secret}")
    private String secretKey;

    // 从 application.properties 中注入 JWT 过期时间（毫秒）
    @Value("${jwt.expiration-milliseconds}")
    private long expirationTime;

    /**
     * 生成 JWT 令牌
     *
     * @param userDetails 用户详情对象
     * @return 生成的 JWT 令牌
     */
    public String generateToken(UserDetails userDetails) {
        // 使用 JWT 创建者创建令牌
        return JWT.create()
                .withSubject(userDetails.getUsername())  // 设置令牌的主题为用户名
                .sign(Algorithm.HMAC256(secretKey));  // 使用 HMAC256 算法和密钥签名令牌
    }

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
     * 验证 JWT 令牌
     *
     * @param token JWT 令牌
     * @param userDetails 用户详情对象
     * @return 如果令牌有效则返回 true，否则返回 false
     */
    public CommonResponse validateToken(String token, UserDetails userDetails) {
        // 用户是否允许登录
        User user = userService.getOne(new QueryWrapper<User>().eq("username", userDetails.getUsername()));
        if (user == null) {
            return CommonResponse.failure(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage());
        }
        // 用户被封禁
        if (user.getStatus() == 0) {
            return CommonResponse.failure(ErrorCode.USER_DISABLED.getCode(), ErrorCode.USER_DISABLED.getMessage());
        }


        return CommonResponse.success("验证通过");
    }
}
