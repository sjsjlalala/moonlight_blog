package com.example.blog_common.service.impl;

import com.example.blog_common.entity.BlogComments;
import com.example.blog_common.mapper.BlogCommentsMapper;
import com.example.blog_common.service.IBlogCommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 博客，评论关系表 服务实现类
 * </p>
 *
 * @author moki
 * @since 2024-12-26
 */
@Service
public class BlogCommentsServiceImpl extends ServiceImpl<BlogCommentsMapper, BlogComments> implements IBlogCommentsService {

}
