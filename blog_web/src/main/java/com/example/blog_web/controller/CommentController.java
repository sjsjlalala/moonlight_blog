package com.example.blog_web.controller;

import com.example.blog_web.service.ICommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.base.response.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 评论相关接口
 * @Author LiuMaoJi
 * @Date 2025/1/4
 **/
@RestController
@RequestMapping("/comment")
@Api(value = "博客评论相关接口", tags = {"博客评论相关接口"})
public class CommentController {
    @Autowired
    private ICommentService commentService;

    @ApiOperation(value ="根据博客id获取评论", notes = "根据博客id获取评论")
    @GetMapping("/getCommentByBlogUid/{uid}")
    public CommonResponse getCommentByBlogUid(@PathVariable String uid) {
        return null;
    }

}
