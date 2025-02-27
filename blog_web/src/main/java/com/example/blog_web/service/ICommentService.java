package com.example.blog_web.service;

import com.example.blog_web.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.blog_web.vo.CommentVO;
import org.example.base.response.CommonResponse;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>
 * 评论 服务类
 * </p>
 *
 * @author moki
 * @since 2024-12-30
 */
public interface ICommentService extends IService<Comment> {

    CommonResponse<List<CommentVO>> getCommentByBlogUid(CommentVO commentVO);

    CommonResponse submitComment(Comment comment);

    CommonResponse toggleLike(CommentVO commentVO);

    CommonResponse<List<CommentVO>> getUserComment(@RequestBody CommentVO commentVO);

    CommonResponse updateComment(CommentVO comment);

    CommonResponse deleteComment(CommentVO comment);

//    CommonResponse cancelToggleLike(Comment comment);
}
