package com.example.blog_web.service.impl;

import com.example.blog_web.entity.BlogComments;
import com.example.blog_web.mapper.BlogCommentsMapper;
import com.example.blog_web.service.IBlogCommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 博客，评论关系表 服务实现类
 * </p>
 *
 * @author moki
 * @since 2024-12-30
 */
@Service
public class BlogCommentsServiceImpl extends ServiceImpl<BlogCommentsMapper, BlogComments> implements IBlogCommentsService {

}
