package com.example.blog_web.context;

import com.example.blog_common.context.BaseUserContext;
import com.example.blog_common.entity.User;

/**
 * @Description
 * @Author LiuMaoJi
 * @Date 2024/12/28
 **/
public class UserContext extends BaseUserContext {
    private static final ThreadLocal<String> ip = new ThreadLocal<>();
    public static void setUser(User user) {
        baseContext.set(user);
    }

    public static User getUser() {
        return baseContext.get();
    }

    public static void clear() {
        BaseUserContext.clear();
        ip.remove();
    }
}
