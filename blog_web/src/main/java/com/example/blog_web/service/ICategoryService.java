package com.example.blog_web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.blog_web.entity.Category;
import com.example.blog_web.vo.OptionVO;
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

    CommonResponse<List<OptionVO>> getUserCategories();


}
