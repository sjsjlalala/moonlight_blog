package com.example.blog_web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blog_common.feign.FileFeignClient;
import com.example.blog_web.entity.Blog;
import com.example.blog_web.entity.BlogTags;
import com.example.blog_web.entity.BlogUserCategorys;
import com.example.blog_web.mapper.BlogMapper;
import com.example.blog_web.mapper.BlogTagsMapper;
import com.example.blog_web.mapper.BlogUserCategorysMapper;
import com.example.blog_web.service.IBlogService;
import com.example.blog_web.vo.BlogRequestVO;
import org.example.base.enums.Messages;
import org.example.base.response.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 博客 服务实现类
 * </p>
 *
 * @author moki
 * @since 2024-12-30
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private FileFeignClient fileFeignClient;
    @Autowired
    private BlogTagsMapper blogTagsMapper;
    @Autowired
    private BlogUserCategorysMapper blogUserCategorysMapper;

    @Override
    public CommonResponse addBlog(BlogRequestVO request) {
        Blog blog = new Blog();
        BeanUtil.copyProperties(request.getBlogVO(), blog);
        // 添加博客
        if (!blog.insert()) {
            throw new RuntimeException("添加博客失败");
        }
        // 将博客挂载到标签下,保存到数据库
        dealTags(blog, request);

        // 将博客挂载到用户分组，保存到数据库
        dealCategory(blog, request);


        // 删除本地和数据库多余的图像文件
        if (!request.getUuidsToDelete().isEmpty()) {
            ArrayList<String> uuids = request.getUuidsToDelete();
            Mono<CommonResponse> result = fileFeignClient.deleteFile(uuids);
            if (Objects.requireNonNull(result.block()).getCode() != 200) {
                throw new RuntimeException("删除文件失败");
            }
        }
        return CommonResponse.success(Messages.ADD_BLOG_SUCCESS);
    }

    private void dealCategory(Blog blog, BlogRequestVO request) {
        BlogUserCategorys blogUserCategorys = new BlogUserCategorys();
        blogUserCategorys.setBlogUid(blog.getUid());
        // 只取分组的链尾
        List<String> category = request.getBlogVO().getCategory();
        blogUserCategorys.setUserCategoryUid(category.get(category.size() - 1));
        // 保存到数据库
        blogUserCategorysMapper.insert(blogUserCategorys);
    }

    private void dealTags(Blog blog, BlogRequestVO request) {
        List<List<String>> tagsChain = request.getBlogVO().getTags();
        for (List<String> tag : tagsChain) {
            // 只取标签链尾
            String tagUid = tag.get(tag.size() - 1);
            BlogTags blogTag = new BlogTags();
            blogTag.setBlogUid(blog.getUid());
            blogTag.setTagUid(tagUid);
            // 保存到数据库
            blogTagsMapper.insert(blogTag);
        }
    }
}
