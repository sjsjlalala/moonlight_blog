package com.example.blog_common.service.impl;

import com.example.blog_common.entity.Blog;
import com.example.blog_common.mapper.BlogMapper;
import com.example.blog_common.service.IBlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blog_common.vo.BlogVO;
import org.example.base.enums.ErrorCode;
import org.example.base.response.CommonResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 博客 服务实现类
 * </p>
 *
 * @author moki
 * @since 2024-12-26
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {
    @Autowired
    private BlogMapper blogMapper;
    /**
     * @description: 添加博客
     * @author: moki
     * @date: 2024/12/26 21:27
     * @param: [blogVO]
     * @return: org.example.base.response.CommonResponse<com.example.blog_common.entity.Blog>
     **/
    public CommonResponse<Blog> addBlog(BlogVO blogVO) {
        Blog blog = new Blog();
        BeanUtils.copyProperties(blogVO, blog);
        int res = blogMapper.insert(blog);
        if (res > 0) {
            return CommonResponse.success(blog);
        } else {
            return CommonResponse.failure(ErrorCode.Blog_ADD_FAILED.getCode(), ErrorCode.Blog_ADD_FAILED.getMessage(), null);
        }
    }
}
