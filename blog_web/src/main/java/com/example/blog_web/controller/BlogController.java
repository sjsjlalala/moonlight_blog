package com.example.blog_web.controller;


import com.example.blog_common.util.ThrowableUtils;
import com.example.blog_web.entity.Blog;
import com.example.blog_web.service.impl.BlogServiceImpl;
import com.example.blog_web.vo.BlogDetailVO;
import com.example.blog_web.vo.BlogRequestVO;
import com.example.blog_web.vo.BlogVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.base.response.CommonResponse;
import org.example.base.validtor.group.Insert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description： 博客
 * @Author LiuMaoJi
 * @Date 2024/12/25
 **/
@RestController
@RequestMapping("/blog")
@Api(value = "博客相关接口", tags = {"博客相关接口"})
public class BlogController {
    @Autowired
    private BlogServiceImpl blogService;


    @ApiOperation(value = "增加博客", notes = "增加博客")
    @PostMapping("/submitBlog")
    public CommonResponse add(@Validated({Insert.class})  @RequestBody BlogRequestVO request, BindingResult result) {
        // 对BindingResult参数结果校验
        ThrowableUtils.checkApiParams(result);
        return blogService.addBlog(request);
    }

    @ApiOperation(value = "获取所有博客", notes = "获取所有博客")
    @PostMapping("/blogList")
    public CommonResponse<List<BlogDetailVO>> blogList(@RequestBody BlogVO blogVO) {
        return blogService.blogList(blogVO);
    }

    @ApiOperation(value = "根据uid获取博客", notes = "根据uid获取博客")
    @GetMapping("/blogDetailByUid/{uid}")
    public CommonResponse<BlogDetailVO> blogDetailByUid(@NotNull @PathVariable String uid) {
        return blogService.blogDetailByUid(uid);
    }

    @ApiOperation(value = "博客点赞/取消点赞", notes = "博客点赞/取消点赞")
    @PostMapping("/blogToggleLike")
    public CommonResponse blogToggleLike(@RequestBody BlogVO blogVO) {
        return blogService.blogToggleLike(blogVO);
    }

    @ApiOperation(value = "博客收藏/取消收藏", notes = "博客收藏/取消收藏")
    @PostMapping("/blogToggleCollection")
    public CommonResponse blogToggleCollection(@RequestBody BlogVO blog) {
        return blogService.blogToggleCollection(blog);
    }



}
