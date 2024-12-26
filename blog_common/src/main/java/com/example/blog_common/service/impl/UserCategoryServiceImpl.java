package com.example.blog_common.service.impl;

import com.example.blog_common.entity.UserCategory;
import com.example.blog_common.mapper.UserCategoryMapper;
import com.example.blog_common.service.IUserCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 博客用户自定义分类 服务实现类
 * </p>
 *
 * @author moki
 * @since 2024-12-26
 */
@Service
public class UserCategoryServiceImpl extends ServiceImpl<UserCategoryMapper, UserCategory> implements IUserCategoryService {

}
