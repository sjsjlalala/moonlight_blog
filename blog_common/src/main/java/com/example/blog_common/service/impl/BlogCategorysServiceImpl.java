package com.example.blog_common.service.impl;

import com.example.blog_common.entity.BlogCategorys;
import com.example.blog_common.mapper.BlogCategorysMapper;
import com.example.blog_common.service.IBlogCategorysService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 博客，分类关系表 服务实现类
 * </p>
 *
 * @author moki
 * @since 2024-12-26
 */
@Service
public class BlogCategorysServiceImpl extends ServiceImpl<BlogCategorysMapper, BlogCategorys> implements IBlogCategorysService {

}
