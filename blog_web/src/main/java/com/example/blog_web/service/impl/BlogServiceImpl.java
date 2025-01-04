package com.example.blog_web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blog_common.entity.User;
import com.example.blog_common.feign.FileFeignClient;
import com.example.blog_common.mapper.UserMapper;
import com.example.blog_web.context.UserContext;
import com.example.blog_web.entity.Blog;
import com.example.blog_web.entity.BlogTags;
import com.example.blog_web.entity.BlogUserCategorys;
import com.example.blog_web.mapper.BlogMapper;
import com.example.blog_web.mapper.BlogTagsMapper;
import com.example.blog_web.mapper.BlogUserCategorysMapper;
import com.example.blog_web.service.IBlogService;
import com.example.blog_web.vo.BlogDetailVO;
import com.example.blog_web.vo.BlogRequestVO;
import com.example.blog_web.vo.BlogVO;
import com.example.blog_web.vo.UserVO;
import org.example.base.enums.EStatus;
import org.example.base.enums.Messages;
import org.example.base.response.CommonResponse;
import org.example.base.uuid.UUIDUtil;
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
    @Autowired
    private UserMapper userMapper;

    /**
     * @description: 新增博客
     * @author: moki
     * @date: 2025/1/3 11:27
     * @param: [request]
     * @return: org.example.base.response.CommonResponse
     **/
    @Override
    public CommonResponse addBlog(BlogRequestVO request) {
        Blog blog = new Blog();
        BeanUtil.copyProperties(request.getBlogVO(), blog);
        // 用户信息
        User userInfo = UserContext.getUser();
        blog.setAuthorUid(userInfo.getUid());

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
        List<String> tagsChain = request.getBlogVO().getTags();
        for (String tag : tagsChain) {
            BlogTags blogTag = new BlogTags();
            blogTag.setBlogUid(blog.getUid());
            blogTag.setTagUid(tag);
            // 保存到数据库
            blogTagsMapper.insert(blogTag);
        }
    }

    /**
     * @description: 获取博客列表，all，用于首页展示
     * @author: moki
     * @date: 2025/1/3 11:27
     * @param: []
     * @return: org.example.base.response.CommonResponse<com.example.blog_web.vo.BlogVO>
     **/
    @Override
    public CommonResponse<List<BlogDetailVO>> blogList() {
        // 获取博客列表
        List<Blog> blogs = blogMapper.selectList(new LambdaQueryWrapper<Blog>().eq(Blog::getStatus, EStatus.VALID));
        // 获取博客-标签列表
        List<BlogTags> blogTags = blogTagsMapper.selectList(new LambdaQueryWrapper<BlogTags>().eq(BlogTags::getStatus, EStatus.VALID));
        // 组装结果
        List<BlogDetailVO> res = new ArrayList<>();
        for (Blog blog : blogs) {
            BlogVO blogVO = new BlogVO();
            BeanUtil.copyProperties(blog, blogVO);
            // 获取博客-标签列表
            List<BlogTags> blogTagsByBlogUid = blogTags.stream().filter(blogTag -> blogTag.getBlogUid().equals(blog.getUid())).toList();
            // 获取标签列表
            List<String> tags = blogTagsByBlogUid.stream().map(BlogTags::getTagUid).toList();
            blogVO.setTags(tags);
            // 获取封面图片url
            if (blog.getCoverImageUid() != null) {
                Mono<CommonResponse> coverImgUrl = fileFeignClient.getFileUrlById(blog.getCoverImageUid());
                String url = Objects.requireNonNull(coverImgUrl.block()).getData().toString();
                blogVO.setCoverImageUid(url);
            }

            // 获取作者信息
            String authorUid = blog.getAuthorUid();
            User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUid, UUIDUtil.uuidToBytes(authorUid)));
            UserVO userVO = new UserVO();
            BeanUtil.copyProperties(user, userVO);

            // 组装最终结果视图
            BlogDetailVO blogDetailVO = new BlogDetailVO();
            blogDetailVO.setBlogVO(blogVO);
            blogDetailVO.setUserVO(userVO);
            res.add(blogDetailVO);
        }


        return CommonResponse.success(res);
    }
}
