package com.example.blog_common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blog_common.entity.User;
import com.example.blog_common.feign.AuthFeignClient;
import com.example.blog_common.mapper.UserMapper;
import com.example.blog_common.service.IUserService;
import org.example.base.response.CommonResponse;
import org.example.base.uuid.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author moki
 * @since 2025-01-01
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private AuthFeignClient authFeignClient;

    @Override
    public CommonResponse updateUserInfo(User userInfo, User currentUser) {
        // 更新用户信息
        if (userInfo.getUsername() != null)
            currentUser.setUsername(userInfo.getUsername());
        if (userInfo.getEmail() != null)
            currentUser.setEmail(userInfo.getEmail());
        if (userInfo.getAvatarUid() != null)
            currentUser.setAvatarUid(userInfo.getAvatarUid());
        if (userInfo.getRemarks() != null)
            currentUser.setRemarks(userInfo.getRemarks());

        int update = this.baseMapper.update(currentUser, new LambdaQueryWrapper<User>().eq(User::getUid, UUIDUtil.uuidToBytes(currentUser.getUid())));
        if (update != 0) {
            return CommonResponse.success("修改成功");
        } else {
            return CommonResponse.failure(5000, "修改失败");
        }
    }

    @Override
    public CommonResponse updateUserEmail(String newEmail, String verificationCode, User user) {

        // 更新用户邮箱
        Mono<CommonResponse> commonResponseMono = authFeignClient.validateVerificationCode(newEmail, verificationCode);
        commonResponseMono.subscribe(commonResponse -> {
            if (commonResponse.getCode() == 200) {
                // 更新用户邮箱
                user.setEmail(newEmail);
            }
        });
        int update = this.baseMapper.update(user, new LambdaQueryWrapper<User>().eq(User::getUid, user.getUid()));
        if (update == 1) {
            return CommonResponse.success("修改邮箱成功");
        } else {
            return CommonResponse.failure(5000, "修改邮箱失败");
        }

    }

    @Override
    public CommonResponse getUserHomePage(String uid) {
        // 1. 获取用户元数据

        return null;
    }

}
