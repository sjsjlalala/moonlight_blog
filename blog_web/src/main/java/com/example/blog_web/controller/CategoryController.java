package com.example.blog_web.controller;

import com.example.blog_common.entity.User;
import com.example.blog_web.context.UserContext;
import com.example.blog_web.service.ICategoryService;
import com.example.blog_web.service.IUserCategoryService;
import com.example.blog_web.vo.OptionVO;
import com.example.blog_web.vo.UserBlogFavoritesVO;
import com.example.blog_web.vo.UserCategoryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.base.response.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description 分组
 * @Author LiuMaoJi
 * @Date 2025/1/1
 **/
@RestController
@RequestMapping("/category")
@Api(value = "博客分组相关接口", tags = {"博客分组相关接口"})
public class CategoryController {
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private IUserCategoryService userCategoryService;


    @ApiOperation(value = "获取用户自定义分组", notes = "获取用户自定义分组")
    @GetMapping("/getUserCategories")
    public CommonResponse<List<OptionVO>> getUserCategories() {
        User userInfo = UserContext.getUser();
        return userCategoryService.getUserCategories(userInfo);
    }
    @ApiOperation(value = "博客分组创建", notes = "博客分组创建")
    @PostMapping("/createUserCategory")
    public CommonResponse createUserCategory(@RequestBody UserCategoryVO categoryVO) {
        return userCategoryService.createUserCategory(categoryVO);
    }
    @ApiOperation(value = "博客收藏夹创建", notes = "博客收藏夹创建")
    @PostMapping("/createBlogCollectionCategory")
    public CommonResponse<UserCategoryVO> createBlogCollectionCategory(@RequestBody UserCategoryVO categoryVO) {
        return userCategoryService.createBlogCollectionCategory(categoryVO);
    }

    @ApiOperation(value = "加载博客收藏夹", notes = "加载博客收藏夹")
    @GetMapping("/fetchBlogCollectionCategory/{uid}")
    public CommonResponse<List<UserCategoryVO>> fetchBlogCollectionCategory(@PathVariable String uid) {
        return userCategoryService.fetchBlogCollectionCategory(uid);
    }

    @ApiOperation(value = "收藏/取消收藏博客", notes = "收藏/取消收藏博客")
    @PostMapping("/toggleBlogCollection")
    public CommonResponse toggleBlogCollection(@RequestBody UserBlogFavoritesVO vo) {
        return userCategoryService.toggleBlogCollection(vo);
    }

    @ApiOperation(value = "获取系统分类", notes = "获取系统分类")
    @GetMapping("/fetchSysCategory")
    public CommonResponse<List<OptionVO>> fetchSubjectCategory() {
        return categoryService.getUserCategories();
    }
}

