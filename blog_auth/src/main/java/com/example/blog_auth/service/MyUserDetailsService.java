package com.example.blog_auth.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blog_auth.model.Myuser;
import com.example.blog_common.entity.User;
import com.example.blog_common.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;



@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    @Lazy // 解决循环依赖
    @Autowired
    private PasswordEncoder passwordEncoder;

    public MyUserDetailsService() {

    }
    /**
     * 根据用户名获取用户对象（获取不到直接抛异常），再登录controller进行权限认证时调用
     */
    @Override
    public Myuser loadUserByUsername(String username) throws UsernameNotFoundException {
        // 数据库中查询用户
        // 使用 MyBatis-Plus 查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);


        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return new Myuser(user.getUsername(), user.getPassword(), Collections.EMPTY_LIST);
    }
}
