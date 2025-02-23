package com.example.blog_web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.blog_web.entity.Blog;
import com.example.blog_web.vo.BlogDetailVO;
import com.example.blog_web.vo.BlogRequestVO;
import com.example.blog_web.vo.BlogVO;
import org.example.base.response.CommonResponse;

import java.util.List;

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

    CommonResponse<List<BlogDetailVO>> blogList(BlogVO requestBlogVO);

    CommonResponse<BlogDetailVO> blogDetailByUid(String uid);

    CommonResponse blogToggleLike(BlogVO blogVO);

    CommonResponse blogToggleCollection(BlogVO blog);

    CommonResponse<List<BlogVO>> fetchBlogListByTag(String tagUid);

    CommonResponse deleteBlogByUid(String uid);

    CommonResponse getBlogLikeAndCollection(String uid);
}
