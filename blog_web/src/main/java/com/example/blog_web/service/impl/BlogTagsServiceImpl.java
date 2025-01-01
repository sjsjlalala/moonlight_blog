package com.example.blog_web.service.impl;

import com.example.blog_web.entity.BlogTags;
import com.example.blog_web.mapper.BlogTagsMapper;
import com.example.blog_web.service.IBlogTagsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 播客，标签关系表 服务实现类
 * </p>
 *
 * @author moki
 * @since 2024-12-30
 */
@Service
public class BlogTagsServiceImpl extends ServiceImpl<BlogTagsMapper, BlogTags> implements IBlogTagsService {

}
