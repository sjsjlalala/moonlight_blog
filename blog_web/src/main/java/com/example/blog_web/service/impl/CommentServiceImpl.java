package com.example.blog_web.service.impl;

import com.example.blog_web.entity.Comment;
import com.example.blog_web.mapper.CommentMapper;
import com.example.blog_web.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 评论 服务实现类
 * </p>
 *
 * @author moki
 * @since 2024-12-30
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

}
