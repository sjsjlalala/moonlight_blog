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
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    private BlogSubjectMapper blogSubjectMapper;
    @Autowired
    private BlogCommentsMapper blogCommentsMapper;
    @Autowired
    private UserBlogLikeMapper userBlogLikeMapper;
    @Autowired
    private CommentMapper commentMapper;

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
    @Transactional
    public CommonResponse addBlog(BlogRequestVO request) {
        Blog blog = new Blog();
        BeanUtil.copyProperties(request.getBlogVO(), blog);
        // 用户信息
        User userInfo = UserContext.getUser();
        blog.setAuthorUid(userInfo.getUid());
        blog.setUid(null);

        // 添加博客
        if (!blog.insert()) {
            throw new RuntimeException("添加博客失败");
        }
        // 将博客挂载到标签下,保存到数据库
        dealTags(blog, request.getBlogVO());

        // 将博客挂载到用户分组，保存到数据库
        dealCategory(blog, request);

        // 将博客挂载到专题（若有）
        dealSubject(blog, request);

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

    private void dealSubject(Blog blog, BlogRequestVO request) {
        if (request.getBlogVO().getSubject() != null && !request.getBlogVO().getSubject().isEmpty()) {
            // 获取专题uid
            String subjectUid = request.getBlogVO().getSubject();
            BlogSubject res = new BlogSubject();
            res.setSubjectUid(subjectUid);
            res.setBlogUid(blog.getUid());
            blogSubjectMapper.insert(res);
        }
    }


    private void dealCategory(Blog blog, BlogRequestVO request) {
        BlogUserCategorys blogUserCategorys = new BlogUserCategorys();
        blogUserCategorys.setBlogUid(blog.getUid());
        // 只取分组的链尾
        String category = request.getBlogVO().getCategory();
        blogUserCategorys.setUserCategoryUid(category);
        // 保存到数据库
        blogUserCategorysMapper.insert(blogUserCategorys);
    }

    private void dealTags(Blog blog, BlogVO blogVO) {
        List<String> tagsChain = blogVO.getTags();
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
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<Blog>()
                .eq(Blog::getStatus, EStatus.VALID);
        if (requestBlogVO.getKeyword() != null && !requestBlogVO.getKeyword().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Blog::getTitle, requestBlogVO.getKeyword())
                    .or()
                    .like(Blog::getIntroduction, requestBlogVO.getKeyword()));
        }
        if (requestBlogVO.getAuthorUid() != null && !requestBlogVO.getAuthorUid().isEmpty())
            queryWrapper.eq(Blog::getAuthorUid, UUIDUtil.uuidToBytes(requestBlogVO.getAuthorUid()));


        Page<Blog> page = blogMapper.selectPage(new Page<>(currentPage, pageSize), queryWrapper);
        List<String> uids = page.getRecords().stream().map(Blog::getUid).toList();
        List<BlogDetailVO> res = new ArrayList<>();
        for (String uid : uids) {
            CommonResponse<BlogDetailVO> blogDetailVOCommonResponse = blogDetailByUid(uid);
            res.add(blogDetailVOCommonResponse.getData());
        }


        return CommonResponse.success(res, currentPage, pageSize, page.getTotal(), (int) Math.ceil((double) page.getTotal() / pageSize));

    }

    @Override
    public CommonResponse<BlogDetailVO> blogDetail(BlogVO blogVO) {
        CommonResponse<BlogDetailVO> res = blogDetailByUid(blogVO.getUid());
        BlogDetailVO data = res.getData();

        // 更新击数
        if (blogVO.getIsDetailRequest() != null && blogVO.getIsDetailRequest()) {
            Blog blog = blogMapper.selectOne(new LambdaQueryWrapper<Blog>().eq(Blog::getUid, UUIDUtil.uuidToBytes(blogVO.getUid())));
            blog.setClicks(blogVO.getClicks() + 1);
            data.getBlogVO().setClicks(blog.getClicks());
            blogMapper.update(blog, new LambdaQueryWrapper<Blog>().eq(Blog::getUid, UUIDUtil.uuidToBytes(blog.getUid())));
        }

        return CommonResponse.success(data);
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
        blogVO.setVisibilityScope(String.valueOf(blog.getVisibilityScope()));
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
            blogVO.setCoverImageUrl(url);
        }


        // 获取分组
        BlogUserCategorys blogUserCategorys = blogUserCategorysMapper.selectOne(new LambdaQueryWrapper<BlogUserCategorys>().eq(BlogUserCategorys::getBlogUid, UUIDUtil.uuidToBytes(blog.getUid())).eq(BlogUserCategorys::getStatus, EStatus.VALID));
        UserCategory temp = userCategoryMapper.selectOne(new LambdaQueryWrapper<UserCategory>().eq(UserCategory::getUid, UUIDUtil.uuidToBytes(blogUserCategorys.getUserCategoryUid())).eq(UserCategory::getStatus, EStatus.VALID));
        BeanUtil.copyProperties(temp, userCategoryVO);

        // 作者信息
        // 获取作者信息
        String authorUid = blog.getAuthorUid();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUid, UUIDUtil.uuidToBytes(authorUid)));

        // 专题信息
        BlogSubject blogSubject = blogSubjectMapper.selectOne(new LambdaQueryWrapper<BlogSubject>().eq(BlogSubject::getBlogUid, UUIDUtil.uuidToBytes(blog.getUid())).eq(BlogSubject::getStatus, EStatus.VALID));
        if (blogSubject != null) {
            blogVO.setSubject(blogSubject.getSubjectUid());
        }


        // 获取用户对此博客的收藏情况（如果用户已经登录）
        if (userInfo != null) {
            List<UserBlogFavorites> userBlogFavorites = userBlogFavoritesMapper.selectList(new LambdaQueryWrapper<UserBlogFavorites>()
                    .eq(UserBlogFavorites::getBlogUid, UUIDUtil.uuidToBytes(blogVO.getUid()))
                    .eq(UserBlogFavorites::getUserUid, UUIDUtil.uuidToBytes(userInfo.getUid()))
                    .eq(UserBlogFavorites::getStatus, EStatus.VALID));
            if (!userBlogFavorites.isEmpty()) {
                blogVO.setIsFavorite(true);
            }

            // 从缓存中获取点赞数量和点赞状态
            String statusPrefix = SysConfig.BLOG_TOGGLE_LIKE_PREFIX + blog.getUid() + SysConfig.STATUS;
            Integer likes = redisUtil.get(statusPrefix, Integer.class);
            if (likes != null) {
                blogVO.setLikes(likes);
            }
            else {

            }
            if (redisUtil.hasKey(SysConfig.BLOG_TOGGLE_LIKE_PREFIX + blog.getUid() + SysConfig.USER + UserContext.getUser().getUid())) {
                blogVO.setIsLiked(true);
            }
        }


        BeanUtil.copyProperties(user, userVO);
        // 处理用户信息
        assembleUserVO(userVO);

        // 组装最终结果视图
        blogDetailVO.setBlogVO(blogVO);
        blogDetailVO.setUserVO(userVO);
        blogDetailVO.setTags(tags);
        blogDetailVO.setUserCategory(userCategoryVO);


        return CommonResponse.success(blogDetailVO);
    }

    private void assembleUserVO(UserVO userVO) {
        // 获取用户头像
        if (userVO.getAvatarUid() != null) {
            Mono<CommonResponse> fileUrl = fileFeignClient.getFileUrlById(userVO.getAvatarUid());
            CommonResponse response = fileUrl.block();
            userVO.setAvatarUrl(Optional.ofNullable(response)
                    .map(CommonResponse::getData)
                    .map(Object::toString)
                    .orElse(null));
        }
        // 获取用户其他信息
        List<Blog> blogs = blogMapper.selectList(new LambdaQueryWrapper<Blog>().eq(Blog::getAuthorUid, UUIDUtil.uuidToBytes(userVO.getUid())).eq(Blog::getStatus, EStatus.VALID));

        // 1.获取博客数量
        userVO.setBlogCount(blogs.size());

        // 2.获取访问数量
        userVO.setVisitCount(blogs.stream().mapToInt(Blog::getClicks).sum());

        // 3.获取被评论数量
        blogs.forEach(blog -> {
            Integer i = commentMapper.selectCount(new LambdaQueryWrapper<Comment>().eq(Comment::getBlogUid, UUIDUtil.uuidToBytes(blog.getUid())).eq(Comment::getStatus, EStatus.VALID));
            userVO.setCommentCount(i + userVO.getCommentCount());
        });
        // 4.获取点赞数量
        blogs.forEach(blog -> {
            // 从缓存中获取点赞数量和点赞状态
            userVO.setLikes(userVO.getLikes() + blog.getLikes());
        });
        // 5.获取收藏数量
        blogs.forEach(blog -> {
            Integer i = userBlogFavoritesMapper.selectCount(new LambdaQueryWrapper<UserBlogFavorites>().eq(UserBlogFavorites::getBlogUid, UUIDUtil.uuidToBytes(blog.getUid())).eq(UserBlogFavorites::getStatus, EStatus.VALID));
            userVO.setFavorites(i + userVO.getFavorites());
        });
        // 5.获取关注数量

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
    public CommonResponse fetchBlogListByTag(String tagUid) {
        List<BlogDetailVO> res = new ArrayList<>();
        // 判断该标签是否有子标签
        List<Tag> childTags = tagMapper.selectList(new LambdaQueryWrapper<Tag>().eq(Tag::getParentUid, UUIDUtil.uuidToBytes(tagUid)).eq(Tag::getStatus, EStatus.VALID));
        if (!childTags.isEmpty()) {
            // 有子标签
            // 根据博客id去重
            List<String> childTaguids = childTags.stream().map(Tag::getUid).distinct().toList();
            for (String childTaguid : childTaguids) {
                // 获取子标签下的博客
                List<BlogTags> blogTags = blogTagsMapper.selectList(new LambdaQueryWrapper<BlogTags>().eq(BlogTags::getTagUid, UUIDUtil.uuidToBytes(childTaguid)).eq(BlogTags::getStatus, EStatus.VALID));
                // 根据博客id去重
                List<String> uids = blogTags.stream().map(BlogTags::getBlogUid).distinct().toList();
                for (String uid : uids) {
                    CommonResponse<BlogDetailVO> blogDetailVOCommonResponse = blogDetailByUid(uid);
                    res.add(blogDetailVOCommonResponse.getData());
                }
            }
        } else {
            // 无子标签,本身就是子标签
            List<BlogTags> blogTags = blogTagsMapper.selectList(new LambdaQueryWrapper<BlogTags>().eq(BlogTags::getTagUid, UUIDUtil.uuidToBytes(tagUid)).eq(BlogTags::getStatus, EStatus.VALID));
            for (BlogTags blogTag : blogTags) {
                CommonResponse<BlogDetailVO> blogDetailVOCommonResponse = blogDetailByUid(blogTag.getBlogUid());
                res.add(blogDetailVOCommonResponse.getData());
            }
        }


        return CommonResponse.success(deduplicate(res));
    }

    public static List<BlogDetailVO> deduplicate(List<BlogDetailVO> blogDetailVOList) {
        List<BlogDetailVO> result = new ArrayList<>();
        for (BlogDetailVO blogDetailVO : blogDetailVOList) {
            boolean isDuplicate = false;
            for (BlogDetailVO existingVO : result) {
                if (existingVO.getBlogVO().getUid().equals(blogDetailVO.getBlogVO().getUid())) {
                    isDuplicate = true;
                    break;
                }
            }
            if (!isDuplicate) {
                result.add(blogDetailVO);
            }
        }
        return result;
    }

    @Override
    public CommonResponse deleteBlogByUid(String uid) {
        int res = blogMapper.delete(new LambdaQueryWrapper<Blog>().eq(Blog::getUid, UUIDUtil.uuidToBytes(uid)));
        if (res == 1) {
            return CommonResponse.success(Messages.DELETE_SUCCESS);
        }
        return CommonResponse.failure(ErrorCode.DELETE_FAILED.getCode(), ErrorCode.DELETE_FAILED.getMessage());
    }

    @Override
    public CommonResponse getBlogLikeAndCollection(BlogVO blogVO) {
        return blogDetail(blogVO);
    }

    @Override
    public CommonResponse updateBlogById(BlogVO blogVO) {
        // 1. 先获取原博客
        Blog blog = blogMapper.selectOne(new LambdaQueryWrapper<Blog>().eq(Blog::getUid, UUIDUtil.uuidToBytes(blogVO.getUid())).eq(Blog::getStatus, EStatus.VALID));

        // 2. 更改与blog关联的表
        // 2.1 更改标签
        blogTagsMapper.delete(new LambdaQueryWrapper<BlogTags>().eq(BlogTags::getBlogUid, UUIDUtil.uuidToBytes(blogVO.getUid())));
        dealTags(blog, blogVO);

        // 2.2 更改分类
        BlogUserCategorys blogUserCategorys = blogUserCategorysMapper.selectOne(new LambdaQueryWrapper<BlogUserCategorys>().eq(BlogUserCategorys::getBlogUid, UUIDUtil.uuidToBytes(blogVO.getUid())));
        if (!blogUserCategorys.getUserCategoryUid().equals(blogVO.getCategory())) {
            blogUserCategorys.setUserCategoryUid(blogVO.getCategory());
            blogUserCategorysMapper.update(blogUserCategorys, new LambdaQueryWrapper<BlogUserCategorys>().eq(BlogUserCategorys::getUid, UUIDUtil.uuidToBytes(blogUserCategorys.getUid())));
        }

        // 2.3 更改专题
        BlogSubject blogSubject = blogSubjectMapper.selectOne(new LambdaQueryWrapper<BlogSubject>().eq(BlogSubject::getBlogUid, UUIDUtil.uuidToBytes(blogVO.getUid())));
        if (!blogSubject.getSubjectUid().equals(blogVO.getSubject())) {
            blogSubject.setSubjectUid(blogVO.getSubject());
            blogSubjectMapper.update(blogSubject, new LambdaQueryWrapper<BlogSubject>().eq(BlogSubject::getUid, UUIDUtil.uuidToBytes(blogSubject.getUid())));
        }

        // 3. 更改封面
        if (!blogVO.getCoverImageUid().equals(blog.getCoverImageUid())) {
            ArrayList<String> fileUuids = new ArrayList<>();
            fileUuids.add(blog.getCoverImageUid());
            fileFeignClient.deleteFile(fileUuids);

        }
        BeanUtil.copyProperties(blogVO, blog);
        // 用户信息
        User userInfo = UserContext.getUser();
        blog.setAuthorUid(userInfo.getUid());
        blogMapper.update(blog, new LambdaQueryWrapper<Blog>().eq(Blog::getUid, UUIDUtil.uuidToBytes(blogVO.getUid())));

        return CommonResponse.success(Messages.UPDATE_SUCCESS);
    }

    @Override
    public CommonResponse deleteBlogById(String id) {
        Blog blog = blogMapper.selectOne(new LambdaQueryWrapper<Blog>().eq(Blog::getUid, UUIDUtil.uuidToBytes(id)));
        blog.setStatus(EStatus.INVALID);
        blogMapper.update(blog, new LambdaQueryWrapper<Blog>().eq(Blog::getUid, UUIDUtil.uuidToBytes(id)));

        // 同时删除与blog相关的表
        List<BlogTags> blogTags = blogTagsMapper.selectList(new LambdaQueryWrapper<BlogTags>().eq(BlogTags::getBlogUid, UUIDUtil.uuidToBytes(id)));
        if (blogTags != null && !blogTags.isEmpty()) {
            for (BlogTags blogTag : blogTags) {
                blogTag.setStatus(EStatus.INVALID);
                blogTagsMapper.update(blogTag, new LambdaQueryWrapper<BlogTags>().eq(BlogTags::getUid, UUIDUtil.uuidToBytes(blogTag.getUid())));
            }

        }
        List<BlogUserCategorys> blogUserCategorys = blogUserCategorysMapper.selectList(new LambdaQueryWrapper<BlogUserCategorys>().eq(BlogUserCategorys::getBlogUid, UUIDUtil.uuidToBytes(id)));
        if (blogUserCategorys != null && !blogUserCategorys.isEmpty()) {
            for (BlogUserCategorys blogUserCategory : blogUserCategorys) {
                blogUserCategory.setStatus(EStatus.INVALID);
                blogUserCategorysMapper.update(blogUserCategory, new LambdaQueryWrapper<BlogUserCategorys>().eq(BlogUserCategorys::getUid, UUIDUtil.uuidToBytes(blogUserCategory.getUid())));
            }
        }
        List<BlogSubject> blogSubjects = blogSubjectMapper.selectList(new LambdaQueryWrapper<BlogSubject>().eq(BlogSubject::getBlogUid, UUIDUtil.uuidToBytes(id)));
        if (blogSubjects != null && !blogSubjects.isEmpty()) {
            for (BlogSubject blogSubject : blogSubjects) {
                blogSubject.setStatus(EStatus.INVALID);
                blogSubjectMapper.update(blogSubject, new LambdaQueryWrapper<BlogSubject>().eq(BlogSubject::getUid, UUIDUtil.uuidToBytes(blogSubject.getUid())));
            }
        }

        return CommonResponse.success(Messages.DELETE_SUCCESS);
    }

    @Override
    public CommonResponse getUserHomePage(String uid) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUid, UUIDUtil.uuidToBytes(uid)));
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        assembleUserVO(userVO);
        return CommonResponse.success(userVO);
    }


}
