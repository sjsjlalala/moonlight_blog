package com.example.blog_web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.blog_common.entity.User;
import com.example.blog_web.entity.UserCategory;
import com.example.blog_web.vo.OptionVO;
import com.example.blog_web.vo.UserBlogFavoritesVO;
import com.example.blog_web.vo.UserCategoryVO;
import org.example.base.response.CommonResponse;

import java.util.List;

/**
 * <p>
 * 博客用户自定义分类 服务类
 * </p>
 *
 * @author moki
 * @since 2025-01-01
 */
public interface IUserCategoryService extends IService<UserCategory> {

    CommonResponse<List<OptionVO>> getUserCategories(User userInfo);

    CommonResponse<UserCategoryVO> createBlogCollectionCategory(UserCategoryVO categoryVO);

    CommonResponse<List<UserCategoryVO>> fetchBlogCollectionCategory(String uid);

    CommonResponse toggleBlogCollection(UserBlogFavoritesVO vo);
}
