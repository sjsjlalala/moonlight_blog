package com.example.blog_common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.blog_common.entity.User;
import org.example.base.response.CommonResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author moki
 * @since 2025-01-01
 */
public interface IUserService extends IService<User> {

    CommonResponse updateUserInfo(User userInfo, User user);

    CommonResponse updateUserEmail(String newEmail, String verificationCode, User userInfo);

    CommonResponse getUserHomePage(String uid);
}
