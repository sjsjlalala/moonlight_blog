package com.example.blog_auth.controller;


import com.example.blog_auth.model.Myuser;
import com.example.blog_auth.redis.TokenRedisService;
import com.example.blog_auth.security.JwtUtil;
import com.example.blog_auth.service.MyUserDetailsService;
import com.example.blog_common.dto.AuthRequest;
import com.example.blog_common.util.ThrowableUtils;
import org.example.base.response.CommonResponse;
import org.example.base.enums.ErrorCode;
import org.example.base.validtor.group.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenRedisService tokenRedisService;

    @PostMapping("/login")
    public CommonResponse<Map> createAuthenticationToken(@Validated({Common.class}) @RequestBody AuthRequest authRequest, BindingResult result) {
        ThrowableUtils.checkApiParams(result);

        // 将表单数据封装到 UsernamePasswordAuthenticationToken
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
        // authenticate方法会调用loadUserByUsername
        try{
            authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        } catch (Exception e) {

            System.out.println(Arrays.toString(e.getStackTrace()));
            //return CommonResponse.failure(ErrorCode.INVALID_CREDENTIALS.getCode(), ErrorCode.INVALID_CREDENTIALS.getMessage(), null);
        }

        // 加载用户账号密码
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

        // 生成 JWT 令牌
        final String jwt = jwtUtil.generateToken(userDetails);

        // 将 JWT 令牌存储在 Redis 中并设置过期时间
        tokenRedisService.saveToken(jwt, authRequest.getUsername());

        // 将用户数据存储在 Redis 中
        Myuser user = (Myuser) userDetails;
        tokenRedisService.saveUser(jwt, user);

        // 返回 JWT 令牌
        HashMap<Object, Object> auth = new HashMap<>();
        auth.put("token", jwt);
        return CommonResponse.success(auth);
    }

    /*
     校验token，以及用户状态
     */
    @GetMapping("/validate")
    public CommonResponse validateToken( @RequestParam("token") String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token 为空");
        }

        // 检查令牌是否有效
        if (!tokenRedisService.isTokenValid(token)) {
            return CommonResponse.failure(ErrorCode.INVALID_TOKEN.getCode(), ErrorCode.INVALID_TOKEN.getMessage(), null);
        }

        // 刷新令牌有效期
        tokenRedisService.refreshToken(token);

        // 从 Redis 中获取用户数据
        Myuser userDetails = tokenRedisService.getUserByToken(token);

        // 校验token是否有效，和用户是否可登录
        return jwtUtil.validateToken(token, userDetails);
    }

    @PostMapping("/logout")
    public CommonResponse logout(@RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7); // 去掉 "Bearer " 前缀
        tokenRedisService.deleteToken(jwtToken);
        return CommonResponse.success("登出成功");
    }
}
