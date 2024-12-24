package com.example.blog_auth.controller;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.blog_auth.model.Myuser;
import com.example.blog_auth.security.JwtUtil;
import com.example.blog_auth.service.MyUserDetailsService;
import com.example.blog_common.dto.AuthRequest;
import com.example.blog_common.vo.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/login")
    public CommonResponse<Map> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            // 认证用户
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        // 加载用户账号密码
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

        // 生成 JWT 令牌
        final String jwt = jwtUtil.generateToken(userDetails);

        // 返回 JWT 令牌
        HashMap<Object, Object> auth = new HashMap<>();
        auth.put("token", jwt);
        return CommonResponse.success(auth);
    }

    /*
     校验token，以及用户状态
     */
    @GetMapping("/validate")
    public CommonResponse validateToken(@RequestParam("token") String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        // 从token中提取用户名
        String userName = jwtUtil.extractUsername(token);
        // 加载用户详情
        Myuser userDetails = userDetailsService.loadUserByUsername(userName);
        // 校验token是否有效，和用户是否可登录
        return jwtUtil.validateToken(token, userDetails);
    }
}
