package com.example.blog_common.service.impl;

import com.example.blog_common.entity.Category;
import com.example.blog_common.mapper.CategoryMapper;
import com.example.blog_common.service.ICategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 博客分类 服务实现类
 * </p>
 *
 * @author moki
 * @since 2024-12-26
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

}
