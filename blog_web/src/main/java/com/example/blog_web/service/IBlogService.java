package com.example.blog_web.service;

import com.example.blog_web.entity.Blog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.blog_web.vo.BlogRequestVO;
import org.example.base.response.CommonResponse;

/**
 * <p>
 * 博客 服务类
 * </p>
 *
 * @author moki
 * @since 2024-12-30
 */
public interface IBlogService extends IService<Blog> {

    CommonResponse addBlog(BlogRequestVO request);
}
