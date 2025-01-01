package com.example.blog_web.Interceptor;

import com.example.blog_common.entity.User;
import com.example.blog_common.util.CommonUserRedisUtil;
import com.example.blog_common.util.ThrowableUtils;
import com.example.blog_web.context.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description: 登录拦截器，用来获取登录用户的信息
 * @author: moki
 * @date: 2024/12/28 19:23
 **/
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private final CommonUserRedisUtil commonUserRedisUtil;

    @Autowired
    public LoginInterceptor(CommonUserRedisUtil commonUserRedisUtil) {
        this.commonUserRedisUtil = commonUserRedisUtil;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = ThrowableUtils.checkToken(request);
        User userInfo = commonUserRedisUtil.getUserByToken(token);
        //将用户信息保存到ThreadLocal中
        UserContext.setUser(userInfo);
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 释放线程变量的内存
        UserContext.clear();
    }
}
