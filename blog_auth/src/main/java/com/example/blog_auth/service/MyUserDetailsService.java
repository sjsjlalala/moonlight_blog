package com.example.blog_auth.service;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blog_auth.model.Myuser;

import com.example.blog_common.entity.User;
import com.example.blog_common.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.List;



@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;


    public MyUserDetailsService() {

    }

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
        return new Myuser(user.getUsername(), new BCryptPasswordEncoder().encode(user.getPassword()), Collections.EMPTY_LIST);
    }
}
