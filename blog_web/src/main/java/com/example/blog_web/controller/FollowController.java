package com.example.blog_web.controller;

import com.example.blog_web.service.impl.FollowsServiceImpl;
import com.example.blog_web.vo.FollowVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.base.response.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description
 * @Author LiuMaoJi
 * @Date 2025/2/27
 **/
@RestController
@RequestMapping("/follow")
@Api(value = "用户关注相关接口", tags = {"用户关注相关接口"})
public class FollowController {
    @Autowired
    private FollowsServiceImpl followsService;

    @ApiOperation(value ="关注/取消关注用户")
    @PostMapping("/followUser")
    public CommonResponse followUser(@RequestBody FollowVO followVO){
        return followsService.followUser(followVO);
    }

    @ApiOperation(value ="获取关注状态")
    @PostMapping("/getFollowStatus")
    public CommonResponse getFollowStatus(@RequestBody FollowVO followVO){
        return followsService.getFollowStatus(followVO);
    }
    @ApiOperation(value ="获取关注作者的博客，按时间排序")
    @PostMapping("/getFollowBlog")
    public CommonResponse getFollowBlog(@RequestBody FollowVO followVO){
        return followsService.getFollowBlogs(followVO);
    }
    @ApiOperation(value ="获取关注列表")
    @PostMapping("/getFollowList")
    public CommonResponse getFollowList(@RequestBody FollowVO followVO){
        return followsService.getFollowList(followVO);
    }
}
