package com.example.blog_common.service.impl;

import com.example.blog_common.entity.User;
import com.example.blog_common.mapper.UserMapper;
import com.example.blog_common.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author moki
 * @since 2024-12-26
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
