package com.example.blog_auth.controller;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.blog_auth.model.Myuser;
import com.example.blog_auth.model.RegisterRequest;
import com.example.blog_auth.redis.TokenRedisService;
import com.example.blog_auth.security.JwtUtil;
import com.example.blog_auth.service.EmailService;
import com.example.blog_auth.service.MyUserDetailsService;
import com.example.blog_common.dto.AuthRequest;
import com.example.blog_common.entity.User;
import com.example.blog_common.mapper.UserMapper;
import com.example.blog_common.util.ThrowableUtils;
import io.swagger.annotations.ApiOperation;
import org.example.base.enums.ErrorCode;
import org.example.base.enums.Messages;
import org.example.base.response.CommonResponse;
import org.example.base.validtor.group.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    @Autowired
    private EmailService emailService;
    @Autowired
    private UserMapper userMapper;
    @Lazy // 解决循环依赖
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @PostMapping("/login")
    public CommonResponse<Map> createAuthenticationToken(@Validated({Common.class}) @RequestBody AuthRequest authRequest, BindingResult result) {
        ThrowableUtils.checkApiParams(result);

        // 将表单数据封装到 UsernamePasswordAuthenticationToken
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
        // authenticate方法会调用loadUserByUsername
        try {
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
        auth.put("user", user);
        return CommonResponse.success(auth);
    }

    /*
     校验token，以及用户状态
     */
    @GetMapping("/validate")
    public CommonResponse validateToken(@RequestParam("token") String token) {
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


    @PostMapping("/send-verification-code")
    public CommonResponse sendVerificationCode(@RequestParam("email") String email) {
        // 生成验证码
        String verificationCode = generateVerificationCode();

        // 将验证码存储在 Redis 中，并设置过期时间
        tokenRedisService.saveVerificationCode(email, verificationCode);

        // 发送验证码邮件
        emailService.sendEmail(Set.of(email), "MoonLight博客注册验证码", verificationCode);


        return CommonResponse.success("验证码已发送");
    }

    @PostMapping("/validate-verification-code")
    public CommonResponse validateVerificationCode(@RequestParam("email") String email, @RequestParam("code") String code) {
        if (email == null || code == null) {
            return CommonResponse.failure(ErrorCode.REQUEST_PARAMS_NULL.getCode(), ErrorCode.REQUEST_PARAMS_NULL.getMessage(), null);
        }
        if (!tokenRedisService.isVerificationCodeValid(email, code)) {
            return CommonResponse.failure(ErrorCode.CODE_ERROR.getCode(), ErrorCode.CODE_ERROR.getMessage(), null);
        }
        return CommonResponse.success("验证码正确");
    }

    private String generateVerificationCode() {
        // 生成一个6位数字验证码
        return String.format("%06d", (int) (Math.random() * 999999));
    }

    @PostMapping("/register")
    public CommonResponse register(@RequestBody RegisterRequest user) {
        if (user.getEmail() == null || user.getPassword() == null) {
            return CommonResponse.failure(ErrorCode.REQUEST_PARAMS_NULL.getCode(), ErrorCode.REQUEST_PARAMS_NULL.getMessage(), null);
        }
        boolean verificationCodeValid = tokenRedisService.isVerificationCodeValid(user.getEmail(), user.getCode());
        if (!verificationCodeValid) {
            return CommonResponse.failure(ErrorCode.CODE_ERROR.getCode(), ErrorCode.CODE_ERROR.getMessage(), null);
        }
        // 判断账号是否已经存在
        User user1 = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, user.getEmail()));
        if (user1 != null) {
            return CommonResponse.failure(ErrorCode.USER_ALREADY_EXISTS.getCode(), ErrorCode.USER_ALREADY_EXISTS.getMessage(), null);
        }
        User newUser = new User();
        BeanUtil.copyProperties(user, newUser);
        //  密码加密
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insert(newUser);
        // 注册成功,删除验证码
        tokenRedisService.deleteVerificationCode(user.getEmail());
        return CommonResponse.success(Messages.REGISTRY_SUCCESS);
    }

    @ApiOperation(value = "重置密码", notes = "重置密码")
    @PostMapping("/reset-password")
    public CommonResponse resetPassword(@RequestParam(value = "email", required = true) String email, @RequestParam(value = "password", required = true) String password,  @RequestParam(value = "code", required = true) String code) {
        if (email == null || password == null || code == null) {
            return CommonResponse.failure(ErrorCode.REQUEST_PARAMS_NULL.getCode(), ErrorCode.REQUEST_PARAMS_NULL.getMessage(), null);
        }
        if (!tokenRedisService.isVerificationCodeValid(email, code)) {
            return CommonResponse.failure(ErrorCode.CODE_ERROR.getCode(), ErrorCode.CODE_ERROR.getMessage(), null);
        }
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, email));
        if (user == null) {
            return CommonResponse.failure(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage(), null);
        }
        user.setPassword(passwordEncoder.encode(password));

        return CommonResponse.success(Messages.RESET_PASSWORD_SUCCESS);
    }
}
