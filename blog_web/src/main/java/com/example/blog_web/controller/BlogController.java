package com.example.blog_web.controller;


import com.example.blog_common.util.ThrowableUtils;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return blogService.blogList();
    }



}
