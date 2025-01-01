package com.example.blog_web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blog_common.entity.User;
import com.example.blog_web.entity.Category;
import com.example.blog_web.mapper.CategoryMapper;
import com.example.blog_web.service.ICategoryService;
import com.example.blog_web.vo.UserCategoryVO;
import org.example.base.response.CommonResponse;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 博客分类 服务实现类
 * </p>
 *
 * @author moki
 * @since 2024-12-30
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {
    @Override
    public CommonResponse<List<UserCategoryVO>> getUserCategories(User userInfo) {
        return null;
    }

    /**
     * @description: 获取用户自定义的分类
     * @author: moki
     * @date: 2025/1/1 11:29
     * @param: [userInfo]
     * @return: void
     **/

}
