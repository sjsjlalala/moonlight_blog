package com.example.blog_web.service;

import com.example.blog_common.entity.User;
import com.example.blog_web.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.blog_web.vo.UserCategoryVO;
import org.example.base.response.CommonResponse;

import java.util.List;

/**
 * <p>
 * 博客分类 服务类
 * </p>
 *
 * @author moki
 * @since 2024-12-30
 */
public interface ICategoryService extends IService<Category> {

    CommonResponse<List<UserCategoryVO>> getUserCategories(User userInfo);


}
