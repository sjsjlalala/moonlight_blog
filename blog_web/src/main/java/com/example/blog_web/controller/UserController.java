package com.example.blog_web.controller;

import com.example.blog_common.entity.User;
import com.example.blog_common.service.impl.UserServiceImpl;
import com.example.blog_web.context.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.base.response.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description
 * @Author LiuMaoJi
 * @Date 2025/2/23
 **/
@RestController
@RequestMapping("/user")
@Api(value = "用户相关接口", tags = {"用户相关接口"})
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @ApiOperation(value = "修改用户信息", notes = "修改用户信息")
    @PostMapping("/updateUserInfo")
    public CommonResponse updateUserInfo(@RequestBody User userInfo){
        User user = UserContext.getUser();
        return userService.updateUserInfo(userInfo, user);

    }

    @ApiOperation(value = "修改用户邮箱", notes = "修改用户邮箱")
    @PostMapping("/updateUserEmail")
    public CommonResponse updateUserEmail(@RequestParam(value = "email") String newEmail, @RequestParam(value = "code") String verificationCode){
        User user = UserContext.getUser();
        return userService.updateUserEmail(newEmail, verificationCode, user);
    }

    @ApiOperation(value = "获取用户主页信息", notes = "获取用户主页信息")
    @GetMapping("/getUserHomePage/{uid}")
    public CommonResponse getUserHomePage(@PathVariable String uid){
        return userService.getUserHomePage(uid);
    }

}
