package com.example.blog_web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blog_common.entity.User;
import com.example.blog_web.context.UserContext;
import com.example.blog_web.entity.Blog;
import com.example.blog_web.entity.BlogUserCategorys;
import com.example.blog_web.entity.UserBlogFavorites;
import com.example.blog_web.entity.UserCategory;
import com.example.blog_web.mapper.BlogMapper;
import com.example.blog_web.mapper.BlogUserCategorysMapper;
import com.example.blog_web.mapper.UserBlogFavoritesMapper;
import com.example.blog_web.mapper.UserCategoryMapper;
import com.example.blog_web.service.IUserCategoryService;
import com.example.blog_web.vo.*;
import org.example.base.enums.EStatus;
import org.example.base.enums.ErrorCode;
import org.example.base.enums.Messages;
import org.example.base.response.CommonResponse;
import org.example.base.uuid.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 博客用户自定义分类 服务实现类
 * </p>
 *
 * @author moki
 * @since 2025-01-01
 */
@Service
public class UserCategoryServiceImpl extends ServiceImpl<UserCategoryMapper, UserCategory> implements IUserCategoryService {
    @Autowired
    private UserCategoryMapper userCategoryMapper;
    @Autowired
    private UserBlogFavoritesMapper userBlogFavoritesMapper;
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private BlogUserCategorysMapper blogUserCategorysMapper;
    @Autowired
    private BlogServiceImpl blogServiceImpl;
    @Override
    public CommonResponse<List<OptionVO>> getUserCategories(User userInfo) {
        List<OptionVO> optionsVO = new ArrayList<>();
        // 根据用户uid找出所有有效顶级分组
        List<UserCategory> userCategories = userCategoryMapper.selectList(
                new LambdaQueryWrapper<UserCategory>()
                        .eq(UserCategory::getUserUid, UUIDUtil.uuidToBytes(userInfo.getUid()))
                        .eq(UserCategory::getStatus, EStatus.VALID)
                        .eq(UserCategory::getCategoryType, EStatus.BLOG_FOLDER)
                        .isNull(UserCategory::getParentUid)
        );
        // 递归获取所有层级的分类
        userCategories.forEach(userCategory -> {
            OptionVO optionVO = new OptionVO();
            DtoToVORecursive(userCategory, optionVO);
            optionsVO.add(optionVO);
        });
        return CommonResponse.success(optionsVO);
    }

    /**
     * @description: 创建新收藏夹
     * @author: moki
     * @date: 2025/1/8 13:16
     * @param: [categoryVO]
     * @return: org.example.base.response.CommonResponse<com.example.blog_web.vo.UserCategoryVO>
     **/
    @Override
    public CommonResponse<UserCategoryVO> createBlogCollectionCategory(UserCategoryVO categoryVO) {
        // 获取用户信息
        User userInfo = UserContext.getUser();
        // 创建新收藏夹
        UserCategory userCategory = new UserCategory();
        BeanUtil.copyProperties(categoryVO, userCategory);
        userCategory.setUserUid(userInfo.getUid());
        userCategory.setCategoryType(EStatus.BLOG_COLLECTION);
        int res = userCategoryMapper.insert(userCategory);
        if (res > 0) {
            UserCategoryVO userCategoryVO = new UserCategoryVO();
            BeanUtil.copyProperties(userCategory, userCategoryVO);
            return CommonResponse.success(userCategoryVO);
        }
        return CommonResponse.failure(ErrorCode.CREATE_COLLECTION_FAILED.getCode(), ErrorCode.CREATE_COLLECTION_FAILED.getMessage(), null);
    }

    /**
     * @description: 加载用户博客收藏夹
     * @author: moki
     * @date: 2025/1/8 13:47
     * @param: []
     * @return: org.example.base.response.CommonResponse<java.util.List < com.example.blog_web.vo.UserCategoryVO>>
     **/
    @Override
    public CommonResponse<List<UserCategoryVO>> fetchBlogCollectionCategory(String uid) {
        // 获取用户信息
        User userInfo = UserContext.getUser();
        // 获取收藏夹
        List<UserCategory> targets = this.baseMapper.selectList(new LambdaQueryWrapper<UserCategory>()
                .eq(UserCategory::getUserUid, UUIDUtil.uuidToBytes(userInfo.getUid()))
                .eq(UserCategory::getCategoryType, EStatus.BLOG_COLLECTION)
                .eq(UserCategory::getStatus, EStatus.VALID));
        List<UserCategoryVO> res = BeanUtil.copyToList(targets, UserCategoryVO.class);
        // 判断是否有文件夹已经收录该博客
        res.forEach(userCategoryVO -> {
            UserBlogFavorites userBlogFavorites = userBlogFavoritesMapper.selectOne(new LambdaQueryWrapper<UserBlogFavorites>()
                    .eq(UserBlogFavorites::getUserUid, UUIDUtil.uuidToBytes(userInfo.getUid()))
                    .eq(UserBlogFavorites::getCategoryUid, UUIDUtil.uuidToBytes(userCategoryVO.getUid()))
                    .eq(UserBlogFavorites::getBlogUid, UUIDUtil.uuidToBytes(uid))
                    .eq(UserBlogFavorites::getStatus, EStatus.VALID));
            if (userBlogFavorites != null) {
                userCategoryVO.setIsCollected(true);
            }
        });
        return CommonResponse.success(res);
    }

    /**
     * @description: 收藏/取消收藏
     * @author: moki
     * @date: 2025/1/8 14:59
     * @param: [vo]
     * @return: org.example.base.response.CommonResponse
     **/
    @Override
    @Transactional
    public CommonResponse toggleBlogCollection(UserBlogFavoritesVO vo) {
        User userInfo = UserContext.getUser();
        // 判断是收藏还是取消收藏
        if (vo.getIsCollected()) {
            UserBlogFavorites target = userBlogFavoritesMapper.selectOne(new LambdaQueryWrapper<UserBlogFavorites>()
                    .eq(UserBlogFavorites::getUserUid, UUIDUtil.uuidToBytes(userInfo.getUid()))
                    .eq(UserBlogFavorites::getBlogUid, UUIDUtil.uuidToBytes(vo.getBlogUid()))
                    .eq(UserBlogFavorites::getCategoryUid, UUIDUtil.uuidToBytes(vo.getCategoryUid())));
            if (target != null && target.getStatus() == EStatus.VALID) {
               // 已经收藏
                return CommonResponse.failure(ErrorCode.BLOG_ALREADY_COLLECTED.getCode(), ErrorCode.BLOG_ALREADY_COLLECTED.getMessage());

            }  else if (target != null && target.getStatus() == EStatus.INVALID) {
                target.setStatus(EStatus.VALID);
                // 更新数据库
                int update = userBlogFavoritesMapper.update(target, new LambdaQueryWrapper<UserBlogFavorites>().eq(UserBlogFavorites::getUid, UUIDUtil.uuidToBytes(target.getUid())));
                // 更新blog收藏数
                Blog blog = blogMapper.selectOne(new LambdaQueryWrapper<Blog>().eq(Blog::getUid, UUIDUtil.uuidToBytes(target.getBlogUid())));
                // 查找收藏信息，统一多少个用户收藏
                Integer userCount = userBlogFavoritesMapper.selectCount(
                        new LambdaQueryWrapper<UserBlogFavorites>()
                                .eq(UserBlogFavorites::getBlogUid, UUIDUtil.uuidToBytes(target.getBlogUid()))
                                .eq(UserBlogFavorites::getStatus, EStatus.VALID)
                                .groupBy(UserBlogFavorites::getUserUid));
                blog.setFavorites(userCount);
                blogMapper.update(blog, new LambdaQueryWrapper<Blog>().eq(Blog::getUid, UUIDUtil.uuidToBytes(target.getBlogUid())));
                if (update > 0) {
                    return CommonResponse.success(Messages.COLLECT_SUCCESS);
                } else {
                    return CommonResponse.failure(ErrorCode.COLLECT_FAILED.getCode(), ErrorCode.COLLECT_FAILED.getMessage());
                }
            }
            else {
               // 收藏
                UserBlogFavorites userBlogFavorites = new UserBlogFavorites();
                BeanUtil.copyProperties(vo, userBlogFavorites);
                userBlogFavorites.setUserUid(userInfo.getUid());
                // 插入数据库
                int insert = userBlogFavoritesMapper.insert(userBlogFavorites);
                if (insert > 0) {
                    return CommonResponse.success(Messages.COLLECT_SUCCESS);
                } else {
                    return CommonResponse.failure(ErrorCode.COLLECT_FAILED.getCode(), ErrorCode.COLLECT_FAILED.getMessage());
                }
            }
        } else {
            UserBlogFavorites userBlogFavorites = userBlogFavoritesMapper.selectOne(new LambdaQueryWrapper<UserBlogFavorites>()
                    .eq(UserBlogFavorites::getUserUid, UUIDUtil.uuidToBytes(userInfo.getUid()))
                    .eq(UserBlogFavorites::getBlogUid, UUIDUtil.uuidToBytes(vo.getBlogUid()))
                    .eq(UserBlogFavorites::getCategoryUid,UUIDUtil.uuidToBytes(vo.getCategoryUid())));
            if (userBlogFavorites != null) {
                userBlogFavorites.setStatus(EStatus.INVALID);
                int update = userBlogFavoritesMapper.update(userBlogFavorites, new LambdaQueryWrapper<UserBlogFavorites>().eq(UserBlogFavorites::getUid, UUIDUtil.uuidToBytes(userBlogFavorites.getUid())));
                // 更新blog收藏数
                Blog blog = blogMapper.selectOne(new LambdaQueryWrapper<Blog>().eq(Blog::getUid, UUIDUtil.uuidToBytes(userBlogFavorites.getBlogUid())));
                // 查找收藏信息，统一多少个用户收藏
                Integer userCount = userBlogFavoritesMapper.selectCount(
                        new LambdaQueryWrapper<UserBlogFavorites>()
                                .eq(UserBlogFavorites::getBlogUid, UUIDUtil.uuidToBytes(userBlogFavorites.getBlogUid()))
                                .eq(UserBlogFavorites::getStatus, EStatus.VALID)
                                .groupBy(UserBlogFavorites::getUserUid));
                blog.setFavorites(userCount == null ? 0 : userCount);
                blogMapper.update(blog, new LambdaQueryWrapper<Blog>().eq(Blog::getUid, UUIDUtil.uuidToBytes(userBlogFavorites.getBlogUid())));
                if (update > 0) {
                    return CommonResponse.success(Messages.CANCEL_COLLECT_SUCCESS);
                } else {
                    return CommonResponse.failure(ErrorCode.CANCEL_COLLECT_FAILED.getCode(), ErrorCode.CANCEL_COLLECT_FAILED.getMessage());
                }
            }
        }
        return null;
    }
    /**
     * @description: 创建用户博客分组目录
     * @author: moki
     * @date: 2025/1/8 18:15
     * @param: [categoryVO]
     * @return: org.example.base.response.CommonResponse
     **/
    @Override
    public CommonResponse createUserCategory(UserCategoryVO categoryVO) {
        User userInfo = UserContext.getUser();
        categoryVO.setUserUid(userInfo.getUid());
        UserCategory userCategory = BeanUtil.copyProperties(categoryVO, UserCategory.class);
        userCategory.setCategoryType(EStatus.BLOG_FOLDER);
        //创建分组
        int insert = userCategoryMapper.insert(userCategory);
        if (insert > 0) {
            return CommonResponse.success(Messages.CREATE_CATEGORY_SUCCESS);
        } else {
            return CommonResponse.failure(ErrorCode.CREATE_CATEGORY_FAILED.getCode(), ErrorCode.CREATE_CATEGORY_FAILED.getMessage());
        }
    }

    @Override
    public CommonResponse fetchBlogListByCategory(String categoryId) {
        List<BlogUserCategorys> blogUserCategorys = blogUserCategorysMapper.selectList(
                new LambdaQueryWrapper<BlogUserCategorys>()
                        .eq(BlogUserCategorys::getUserCategoryUid, UUIDUtil.uuidToBytes(categoryId))
                        .eq(BlogUserCategorys::getStatus, EStatus.VALID)
        );

        if (blogUserCategorys == null) {
            return CommonResponse.failure(ErrorCode.SELECT_FAILED.getCode(), ErrorCode.SELECT_FAILED.getMessage());
        }
        List<BlogDetailVO> res = new ArrayList<>();
        for (BlogUserCategorys blogUserCategory : blogUserCategorys) {
            CommonResponse<BlogDetailVO> data = blogServiceImpl.blogDetailByUid(blogUserCategory.getBlogUid());
            res.add(data.getData());
        }
        if (res.isEmpty())
            return CommonResponse.failure(ErrorCode.SELECT_FAILED.getCode(), ErrorCode.SELECT_FAILED.getMessage());
        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse updateBlogCategory(String oldUid, String newUid, String blogUid) {
        BlogUserCategorys blogUserCategorys = blogUserCategorysMapper.selectOne(
                new LambdaQueryWrapper<BlogUserCategorys>()
                        .eq(BlogUserCategorys::getBlogUid, UUIDUtil.uuidToBytes(blogUid))
                        .eq(BlogUserCategorys::getUserCategoryUid, UUIDUtil.uuidToBytes(oldUid))
                        .eq(BlogUserCategorys::getStatus, EStatus.VALID)
        );
        blogUserCategorys.setUserCategoryUid(newUid);
        int res = blogUserCategorysMapper.update(blogUserCategorys, new LambdaQueryWrapper<BlogUserCategorys>()
                .eq(BlogUserCategorys::getUid, UUIDUtil.uuidToBytes(blogUserCategorys.getUid()))
                .eq(BlogUserCategorys::getUserCategoryUid, UUIDUtil.uuidToBytes(oldUid))
                .eq(BlogUserCategorys::getStatus, EStatus.VALID));
        if (res > 0) {
            return CommonResponse.success(Messages.UPDATE_SUCCESS);
        } else {
            return CommonResponse.failure(ErrorCode.UPDATE_FAILED.getCode(), ErrorCode.UPDATE_FAILED.getMessage());
        }

    }

    @Override
    @Transactional
    public CommonResponse deleteBlogCategory(String uid) {
        List<BlogUserCategorys> blogUserCategorys = blogUserCategorysMapper.selectList(new LambdaQueryWrapper<BlogUserCategorys>()
                .eq(BlogUserCategorys::getUserCategoryUid, UUIDUtil.uuidToBytes(uid))
                .eq(BlogUserCategorys::getStatus, EStatus.VALID));
        // 删除博客
        blogUserCategorys.forEach(temp -> {
            blogMapper.delete(new LambdaQueryWrapper<Blog>().eq(Blog::getUid, UUIDUtil.uuidToBytes(temp.getBlogUid())));
        });
        // 删除博客-分组
        blogUserCategorysMapper.delete(new LambdaQueryWrapper<BlogUserCategorys>()
                .eq(BlogUserCategorys::getUserCategoryUid, UUIDUtil.uuidToBytes(uid)));
        // 删除分组
        userCategoryMapper.delete(new LambdaQueryWrapper<UserCategory>().eq(UserCategory::getUid, UUIDUtil.uuidToBytes(uid)));

        return CommonResponse.success(Messages.DELETE_SUCCESS);
    }

    @Override
    public CommonResponse getUserCollection(String uid) {
        List<UserCategoryVO> res = new ArrayList<>();


        User user = UserContext.getUser();
        if (uid != null) {
            user.setUid(uid);
        }

        // 获取用户的收藏夹以及收藏夹下的博客
        List<UserCategory> userCategories = userCategoryMapper.selectList(new LambdaQueryWrapper<UserCategory>()
                .eq(UserCategory::getUserUid, UUIDUtil.uuidToBytes(user.getUid()))
                .eq(UserCategory::getCategoryType, EStatus.BLOG_COLLECTION)
                .eq(UserCategory::getStatus, EStatus.VALID));
        if (!userCategories.isEmpty()) {
            for (UserCategory userCategory : userCategories) {
                UserCategoryVO userCategoryVO = BeanUtil.copyProperties(userCategory, UserCategoryVO.class);

                List<UserBlogFavorites> userBlogFavorites = userBlogFavoritesMapper.selectList(new LambdaQueryWrapper<UserBlogFavorites>()
                        .eq(UserBlogFavorites::getCategoryUid, UUIDUtil.uuidToBytes(userCategory.getUid()))
                        .eq(UserBlogFavorites::getStatus, EStatus.VALID));
                if (!userBlogFavorites.isEmpty()) {
                    List<BlogVO> blogVOS = new ArrayList<>();
                    for (UserBlogFavorites userBlogFavorite : userBlogFavorites) {
                        if (blogMapper.selectCount(new LambdaQueryWrapper<Blog>().eq(Blog::getUid, UUIDUtil.uuidToBytes(userBlogFavorite.getBlogUid())).eq(Blog::getStatus, EStatus.VALID) ) > 0) {
                            CommonResponse<BlogDetailVO> blogDetailVOCommonResponse = blogServiceImpl.blogDetailByUid(userBlogFavorite.getBlogUid());
                            blogVOS.add(blogDetailVOCommonResponse.getData().getBlogVO());
                        }

                    }
                    userCategoryVO.setBlogs(blogVOS);
                }
                res.add(userCategoryVO);
            }
        }
        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse updateBlogCollection(UserCategoryVO categoryVO) {
        User userInfo = UserContext.getUser();
        UserCategory userCategory = userCategoryMapper.selectOne(new LambdaQueryWrapper<UserCategory>()
                .eq(UserCategory::getUid, UUIDUtil.uuidToBytes(categoryVO.getUid()))
                .eq(UserCategory::getUserUid, UUIDUtil.uuidToBytes(userInfo.getUid())));
        if (userCategory != null) {
            userCategory.setCategoryName(categoryVO.getCategoryName());
            userCategory.setDescription(categoryVO.getDescription());
        }
        if (userCategoryMapper.update(userCategory, new LambdaQueryWrapper<UserCategory>()
                .eq(UserCategory::getUid, UUIDUtil.uuidToBytes(categoryVO.getUid()))
                .eq(UserCategory::getUserUid, UUIDUtil.uuidToBytes(userInfo.getUid()))) > 0) {
            return CommonResponse.success(Messages.UPDATE_SUCCESS);
        } else {
            return CommonResponse.failure(ErrorCode.UPDATE_FAILED.getCode(), ErrorCode.UPDATE_FAILED.getMessage());
        }
    }

    @Override
    public CommonResponse deleteBlogCollectionCategory(String uid) {
        User userInfo = UserContext.getUser();
        // 1. 删除关联表
        UserBlogFavorites userBlogFavorites = userBlogFavoritesMapper.selectOne(new LambdaQueryWrapper<UserBlogFavorites>()
                .eq(UserBlogFavorites::getCategoryUid, UUIDUtil.uuidToBytes(uid))
                .eq(UserBlogFavorites::getStatus, EStatus.VALID));
        if (userBlogFavorites != null) {
            userBlogFavorites.setStatus(EStatus.DISABLED);
            userBlogFavoritesMapper.update(userBlogFavorites, new LambdaQueryWrapper<UserBlogFavorites>()
                    .eq(UserBlogFavorites::getUid, UUIDUtil.uuidToBytes(userBlogFavorites.getUid()))
                    .eq(UserBlogFavorites::getStatus, EStatus.VALID));
        }
        // 2.删除分组表数据
        int res = 0;
                UserCategory userCategory = userCategoryMapper.selectOne(new LambdaQueryWrapper<UserCategory>()
                .eq(UserCategory::getUid, UUIDUtil.uuidToBytes(uid))
                .eq(UserCategory::getUserUid, UUIDUtil.uuidToBytes(UserContext.getUser().getUid())));
        if (userCategory != null) {
            userCategory.setStatus(EStatus.DISABLED);
            res = userCategoryMapper.update(userCategory, new LambdaQueryWrapper<UserCategory>()
                    .eq(UserCategory::getUid, UUIDUtil.uuidToBytes(userCategory.getUid()))
                    .eq(UserCategory::getUserUid, UUIDUtil.uuidToBytes(userInfo.getUid())));
        }
        if (res > 0) {
            return CommonResponse.success(Messages.DELETE_SUCCESS);
        } else {
            return CommonResponse.failure(ErrorCode.DELETE_FAILED.getCode(), ErrorCode.DELETE_FAILED.getMessage());
        }
    }


    private void DtoToVORecursive(UserCategory userCategory, OptionVO optionVO) {
        optionVO.setValue(userCategory.getUid());
        optionVO.setLabel(userCategory.getCategoryName());
        // 找出子分类
        List<UserCategory> childCategories = userCategoryMapper.selectList(
                new LambdaQueryWrapper<UserCategory>()
                        .eq(UserCategory::getParentUid, UUIDUtil.uuidToBytes(userCategory.getUid()))
                        .eq(UserCategory::getStatus, EStatus.VALID)
        );
        if (!childCategories.isEmpty()) {
            List<OptionVO> children = new ArrayList<>();
            childCategories.forEach(childCategory -> {
                OptionVO childOptionVO = new OptionVO();
                DtoToVORecursive(childCategory, childOptionVO);
                children.add(childOptionVO);
            });
            optionVO.setChildren(children);
        }
    }

}
