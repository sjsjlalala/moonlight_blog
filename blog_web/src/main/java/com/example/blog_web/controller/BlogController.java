package com.example.blog_web.controller;

import io.swagger.annotations.ApiOperation;
import org.example.base.exception.handler.ThrowableUtils;
import org.example.base.validtor.group.Insert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Author LiuMaoJi
 * @Date 2024/12/25
 **/
@RestController
@RequestMapping("/blog")
public class BlogController {

 /*   @ApiOperation(value = "增加博客", notes = "增加博客", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody BlogVO blogVO, BindingResult result) {
        // 对BindingResult参数结果校验
        ThrowableUtils.checkApiParams(result);
        return blogService.addBlog(blogVO);
    }*/


}
