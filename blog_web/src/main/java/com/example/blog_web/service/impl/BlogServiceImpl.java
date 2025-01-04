package com.example.blog_web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blog_common.entity.User;
import com.example.blog_common.feign.FileFeignClient;
import com.example.blog_common.mapper.UserMapper;
import com.example.blog_common.util.RedisUtils;
import com.example.blog_web.context.UserContext;
import com.example.blog_web.entity.*;
import com.example.blog_web.mapper.*;
import com.example.blog_web.service.IBlogService;
import com.example.blog_web.vo.*;
import org.example.base.enums.EStatus;
import org.example.base.enums.Messages;
import org.example.base.response.CommonResponse;
import org.example.base.uuid.UUIDUtil;
import org.springframework.beans.BeanUtils;
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
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private UserCategoryMapper userCategoryMapper;
    @Autowired
    private RedisUtils redisUtil;


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
    public CommonResponse<List<BlogDetailVO>> blogList(BlogVO requestBlogVO) {
        // 设置默认值
        int currentPage = requestBlogVO.getCurrentPage() != null ? requestBlogVO.getCurrentPage() : 1;
        int pageSize = requestBlogVO.getPageSize() != null ? requestBlogVO.getPageSize() : 10;

        // 分页获取博客列表
        Page<Blog> page = blogMapper.selectPage(new Page<>(currentPage, pageSize), new LambdaQueryWrapper<Blog>().eq(Blog::getStatus, EStatus.VALID));
        List<Blog> blogs = page.getRecords();

        // 组装结果
        List<BlogDetailVO> res = new ArrayList<>();
        for (Blog blog : blogs) {
            BlogVO blogVO = new BlogVO();
            BeanUtil.copyProperties(blog, blogVO);
            // 获取博客-标签列表
            List<BlogTags> blogTagsByBlogUid = blogTagsMapper.selectList(new LambdaQueryWrapper<BlogTags>().eq(BlogTags::getStatus, EStatus.VALID).eq(BlogTags::getBlogUid, blog.getUid()));

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

        // 返回包含分页信息的响应
        return CommonResponse.success(res, currentPage, pageSize, page.getTotal(), (int) Math.ceil((double) page.getTotal() / pageSize));

    }

    /**
     * @description: 根据id获取博客详情
     * @author: moki
     * @date: 2025/1/4 18:35
     * @param: [uid]
     * @return: org.example.base.response.CommonResponse<com.example.blog_web.vo.BlogDetailVO>
     **/
    @Override
    public CommonResponse<BlogDetailVO> blogDetailByUid(String uid) {
        BlogDetailVO blogDetailVO = new BlogDetailVO();
        BlogVO blogVO = new BlogVO();
        UserVO userVO = new UserVO();
        List<TagVO> tags = new ArrayList<>();
        UserCategoryVO userCategoryVO = new UserCategoryVO();

        // 1.先查询redis缓存
        Blog blog = redisUtil.get(uid, Blog.class);
        if (blog == null) {
            // 2.查询数据库
            blog = blogMapper.selectOne(new LambdaQueryWrapper<Blog>().eq(Blog::getUid, UUIDUtil.uuidToBytes(uid)).eq(Blog::getStatus, EStatus.VALID));
        }
        if (blog == null) {
            throw new RuntimeException("博客不存在");
        }
        BeanUtils.copyProperties(blog, blogVO);
        // 获取博客-标签列表
        List<BlogTags> blogTags = blogTagsMapper.selectList(new LambdaQueryWrapper<BlogTags>().eq(BlogTags::getStatus, EStatus.VALID).eq(BlogTags::getBlogUid, UUIDUtil.uuidToBytes(blog.getUid())));
        // 获取标签列表
        blogTags.forEach(temp -> {
            TagVO tagVO = new TagVO();
            Tag tag = tagMapper.selectOne(new LambdaQueryWrapper<Tag>().eq(Tag::getUid, UUIDUtil.uuidToBytes(temp.getTagUid())));
            BeanUtils.copyProperties(tag, tagVO);
            tags.add(tagVO);
        });

        // 获取封面图片url
        if (blog.getCoverImageUid() != null) {
            Mono<CommonResponse> coverImgUrl = fileFeignClient.getFileUrlById(blog.getCoverImageUid());
            String url = Objects.requireNonNull(coverImgUrl.block()).getData().toString();
            blogVO.setCoverImageUid(url);
        }
        // 获取分组
        BlogUserCategorys blogUserCategorys = blogUserCategorysMapper.selectOne(new LambdaQueryWrapper<BlogUserCategorys>().eq(BlogUserCategorys::getBlogUid, UUIDUtil.uuidToBytes(blog.getUid())).eq(BlogUserCategorys::getStatus, EStatus.VALID));
        UserCategory temp = userCategoryMapper.selectOne(new LambdaQueryWrapper<UserCategory>().eq(UserCategory::getUid, UUIDUtil.uuidToBytes(blogUserCategorys.getUserCategoryUid())).eq(UserCategory::getStatus, EStatus.VALID));
        BeanUtil.copyProperties(temp, userCategoryVO);

        // 作者信息
        // 获取作者信息
        String authorUid = blog.getAuthorUid();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUid, UUIDUtil.uuidToBytes(authorUid)));

        BeanUtil.copyProperties(user, userVO);
        // 组装最终结果视图
        blogDetailVO.setBlogVO(blogVO);
        blogDetailVO.setUserVO(userVO);
        blogDetailVO.setTags(tags);
        blogDetailVO.setUserCategory(userCategoryVO);


        return CommonResponse.success(blogDetailVO);
    }
}
