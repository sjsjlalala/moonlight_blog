package com.example.blog_web.controller;

import com.example.blog_common.entity.User;
import com.example.blog_web.context.UserContext;
import com.example.blog_web.service.ICategoryService;
import com.example.blog_web.service.IUserCategoryService;
import com.example.blog_web.vo.OptionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.base.response.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
