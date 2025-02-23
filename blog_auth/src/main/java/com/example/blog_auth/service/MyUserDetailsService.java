package com.example.blog_auth.service;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blog_auth.model.Myuser;
import com.example.blog_common.entity.User;
import com.example.blog_common.feign.FileFeignClient;
import com.example.blog_common.mapper.UserMapper;
import org.example.base.response.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Objects;


@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    @Lazy // 解决循环依赖
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private FileFeignClient fileFeignClient;

    public MyUserDetailsService() {

    }
    /**
     * 根据用户名获取用户对象（获取不到直接抛异常），在登录controller进行权限认证时调用
     */
    @Override
    public Myuser loadUserByUsername(String username) throws UsernameNotFoundException {
        // 数据库中查询用户
        // 使用 MyBatis-Plus 查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);


        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        Myuser myuser = new Myuser(user.getUsername(), passwordEncoder.encode(user.getPassword()), Collections.EMPTY_LIST);
        //转换对象
        BeanUtil.copyProperties(user, myuser);
        // 处理用户头像
        if (user.getAvatarUid() != null) {
            Mono<CommonResponse> fileUrl = fileFeignClient.getFileUrlById(user.getAvatarUid());
            myuser.setAvatarUid(Objects.requireNonNull(fileUrl.block()).getData().toString());
        }
        return myuser;
    }
}
