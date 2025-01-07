package com.example.blog_web.controller;

import com.example.blog_web.entity.Comment;
import com.example.blog_web.service.ICommentService;
import com.example.blog_web.vo.CommentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.base.response.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PostMapping("/getCommentByBlogUid")
    public CommonResponse<List<CommentVO>> getCommentByBlogUid(@RequestBody CommentVO commentVO) {
        return commentService.getCommentByBlogUid(commentVO);
    }

    @ApiOperation(value ="评论/回复评论", notes = "评论/回复评论")
    @PostMapping("/submitComment")
    public CommonResponse getCommentByBlogUid(@RequestBody Comment comment) {
        return commentService.submitComment(comment);
    }
    @ApiOperation(value ="评论点赞/取消点赞", notes = "评论点赞/取消点赞")
    @PostMapping("/toggleLike")
    public CommonResponse toggleLike(@RequestBody CommentVO commentVO) {
        return commentService.toggleLike(commentVO);
    }


//    @ApiOperation(value ="取消评论点赞", notes = "取消评论点赞")
//    @PostMapping("/cancelToggleLike")
//    public CommonResponse cancelToggleLike(@RequestBody Comment comment) {
//        return commentService.cancelToggleLike(comment);
//    }
}
