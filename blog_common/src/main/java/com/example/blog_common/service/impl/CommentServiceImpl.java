package com.example.blog_common.service.impl;

import com.example.blog_common.entity.Comment;
import com.example.blog_common.mapper.CommentMapper;
import com.example.blog_common.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 评论 服务实现类
 * </p>
 *
 * @author moki
 * @since 2024-12-26
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

}
