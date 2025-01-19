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
import org.example.base.config.SysConfig;
import org.example.base.enums.EStatus;
import org.example.base.enums.ErrorCode;
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
import java.util.stream.Collectors;

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
    private UserBlogFavoritesMapper userBlogFavoritesMapper;
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

            // 从缓存中获取点赞数量和点赞状态
            String statusPrefix = SysConfig.BLOG_TOGGLE_LIKE_PREFIX + blog.getUid() + SysConfig.STATUS;
            Integer likes = redisUtil.get(statusPrefix, Integer.class);
            if (likes != null) {
                blogVO.setLikes(likes);
            }
            if (redisUtil.hasKey(SysConfig.BLOG_TOGGLE_LIKE_PREFIX + blog.getUid() + SysConfig.USER + UserContext.getUser().getUid())) {
                blogVO.setIsLiked(true);
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
        User userInfo = UserContext.getUser();

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

        // 从缓存中获取点赞数量和点赞状态
        String statusPrefix = SysConfig.BLOG_TOGGLE_LIKE_PREFIX + blog.getUid() + SysConfig.STATUS;
        Integer likes = redisUtil.get(statusPrefix, Integer.class);
        if (likes != null) {
            blogVO.setLikes(likes);
        }
        if (redisUtil.hasKey(SysConfig.BLOG_TOGGLE_LIKE_PREFIX + blog.getUid() + SysConfig.USER + UserContext.getUser().getUid())) {
            blogVO.setIsLiked(true);
        }

        // 获取分组
        BlogUserCategorys blogUserCategorys = blogUserCategorysMapper.selectOne(new LambdaQueryWrapper<BlogUserCategorys>().eq(BlogUserCategorys::getBlogUid, UUIDUtil.uuidToBytes(blog.getUid())).eq(BlogUserCategorys::getStatus, EStatus.VALID));
        UserCategory temp = userCategoryMapper.selectOne(new LambdaQueryWrapper<UserCategory>().eq(UserCategory::getUid, UUIDUtil.uuidToBytes(blogUserCategorys.getUserCategoryUid())).eq(UserCategory::getStatus, EStatus.VALID));
        BeanUtil.copyProperties(temp, userCategoryVO);

        // 作者信息
        // 获取作者信息
        String authorUid = blog.getAuthorUid();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUid, UUIDUtil.uuidToBytes(authorUid)));

        // 获取用户对此博客的收藏情况
        List<UserBlogFavorites> userBlogFavorites = userBlogFavoritesMapper.selectList(new LambdaQueryWrapper<UserBlogFavorites>()
                .eq(UserBlogFavorites::getBlogUid, UUIDUtil.uuidToBytes(blogVO.getUid()))
                .eq(UserBlogFavorites::getUserUid, UUIDUtil.uuidToBytes(userInfo.getUid()))
                .eq(UserBlogFavorites::getStatus, EStatus.VALID));
        if (!userBlogFavorites.isEmpty()) {
            blogVO.setIsFavorite(true);
        }



        BeanUtil.copyProperties(user, userVO);
        // 组装最终结果视图
        blogDetailVO.setBlogVO(blogVO);
        blogDetailVO.setUserVO(userVO);
        blogDetailVO.setTags(tags);
        blogDetailVO.setUserCategory(userCategoryVO);


        return CommonResponse.success(blogDetailVO);
    }
    /**
     * @description: 博客点赞/取消点赞，用redis存储点赞数据，定时任务持久化到数据库
     * @author: moki
     * @date: 2025/1/7 12:12
     * @param: [blog]
     * @return: org.example.base.response.CommonResponse<com.example.blog_web.vo.BlogDetailVO>
     **/
    @Override
    public CommonResponse blogToggleLike(BlogVO blogVO) {
        User userInfo = UserContext.getUser();
        // 获取缓存
        String userPrefix = SysConfig.BLOG_TOGGLE_LIKE_PREFIX + blogVO.getUid() + SysConfig.USER + userInfo.getUid();
        String statusPrefix = SysConfig.BLOG_TOGGLE_LIKE_PREFIX + blogVO.getUid() + SysConfig.STATUS;

        Integer likes = redisUtil.get(statusPrefix, Integer.class);

        // 1.判断是点赞还是取消点赞
        if (blogVO.getIsLiked()) {
            // 点赞
            // 更新缓存
            UserBlogLike likeUser = new UserBlogLike();
            likeUser.setUserUid(userInfo.getUid());
            likeUser.setBlogUid(blogVO.getUid());
            redisUtil.set(userPrefix, likeUser, SysConfig.TOGGLE_LIKE_EXPIRE_TIME);
        } else {
            // 取消点赞
            // 删除user缓存
            redisUtil.delete(userPrefix);
        }
        // 2.更新缓存状态
        if (likes == null) {
            // 缓存已经失效
            // 更新数据库
            Blog target = this.baseMapper.selectOne(new LambdaQueryWrapper<Blog>().eq(Blog::getUid, UUIDUtil.uuidToBytes(blogVO.getUid())).eq(Blog::getStatus, EStatus.VALID));
            // 博客不存在
            if (target == null) {
                return CommonResponse.failure(ErrorCode.BLOG_NOT_EXIST.getCode(), ErrorCode.BLOG_NOT_EXIST.getMessage());
            }
            target.setLikes(blogVO.getLikes());
            // 更新数据库
            this.baseMapper.update(target, new LambdaQueryWrapper<Blog>().eq(Blog::getUid, UUIDUtil.uuidToBytes(blogVO.getUid())));

        }
        // 更新缓存
        redisUtil.set(statusPrefix, blogVO.getLikes(), SysConfig.TOGGLE_LIKE_EXPIRE_TIME);
        return CommonResponse.success(Messages.TOGGLE_LIKE_SUCCESS);
    }
    /**
     * @description: 博客收藏/取消收藏
     * @author: moki
     * @date: 2025/1/7 16:27
     * @param: [blogVO]
     * @return: org.example.base.response.CommonResponse
     **/
    @Override
    public CommonResponse blogToggleCollection(BlogVO blogVO) {
        User userInfo = UserContext.getUser();
        // 判断收藏还是取消收藏
        if (blogVO.getIsFavorite()) {
            if (userBlogFavoritesMapper.selectOne(new LambdaQueryWrapper<UserBlogFavorites>().eq(UserBlogFavorites::getUserUid, UUIDUtil.uuidToBytes(userInfo.getUid())).eq(UserBlogFavorites::getBlogUid, UUIDUtil.uuidToBytes(blogVO.getUid()))) != null) {
                // 已经收藏
                return CommonResponse.failure(ErrorCode.BLOG_ALREADY_COLLECTED.getCode(), ErrorCode.BLOG_ALREADY_COLLECTED.getMessage());
            } else {
               // 收藏
                UserBlogFavorites userBlogFavorites = new UserBlogFavorites();
                userBlogFavorites.setUserUid(userInfo.getUid());
                userBlogFavorites.setBlogUid(blogVO.getUid());
            }
        }

        return null;
    }
    /**
     * @description: 根据标签uid获取博客列表
     * @author: moki
     * @date: 2025/1/9 15:56
     * @param: [tagUid]
     * @return: org.example.base.response.CommonResponse<java.util.List < com.example.blog_web.vo.BlogVO>>
     **/
    @Override
    public CommonResponse<List<BlogVO>> fetchBlogListByTag(String tagUid) {
        List<BlogVO> blogVOS = new ArrayList<>();
        // 判断该标签是否有子标签
        List<Tag> childTags = tagMapper.selectList(new LambdaQueryWrapper<Tag>().eq(Tag::getParentUid, UUIDUtil.uuidToBytes(tagUid)).eq(Tag::getStatus, EStatus.VALID));
        if (!childTags.isEmpty()) {
            // 有子标签
            for (Tag childTag : childTags) {
                // 获取子标签下的博客
                List<BlogTags> blogTags = blogTagsMapper.selectList(new LambdaQueryWrapper<BlogTags>().eq(BlogTags::getTagUid, UUIDUtil.uuidToBytes(childTag.getUid())).eq(BlogTags::getStatus, EStatus.VALID));
                for (BlogTags blogTag : blogTags) {
                    List<Blog> blogs = blogMapper.selectList(new LambdaQueryWrapper<Blog>().eq(Blog::getUid, UUIDUtil.uuidToBytes(blogTag.getBlogUid())).eq(Blog::getStatus, EStatus.VALID));
                    blogVOS.addAll(BeanUtil.copyToList(blogs, BlogVO.class));
                }
            }
        } else {
            // 无子标签,本身就是子标签
            List<BlogTags> blogTags = blogTagsMapper.selectList(new LambdaQueryWrapper<BlogTags>().eq(BlogTags::getTagUid, UUIDUtil.uuidToBytes(tagUid)).eq(BlogTags::getStatus, EStatus.VALID));
            for (BlogTags blogTag : blogTags) {
                List<Blog> blogs = blogMapper.selectList(new LambdaQueryWrapper<Blog>().eq(Blog::getUid, UUIDUtil.uuidToBytes(blogTag.getBlogUid())).eq(Blog::getStatus, EStatus.VALID));
                blogVOS.addAll(BeanUtil.copyToList(blogs, BlogVO.class));
            }
        }
        // 去重
        blogVOS = blogVOS.stream().distinct().collect(Collectors.toList());
        return CommonResponse.success(blogVOS);
    }
}
