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
import com.example.blog_web.entity.Blog;
import com.example.blog_web.entity.Comment;
import com.example.blog_web.entity.UserCommentLike;
import com.example.blog_web.mapper.BlogMapper;
import com.example.blog_web.mapper.CommentMapper;
import com.example.blog_web.mapper.UserCommentLikeMapper;
import com.example.blog_web.service.ICommentService;
import com.example.blog_web.vo.BlogDetailVO;
import com.example.blog_web.vo.CommentVO;
import com.example.blog_web.vo.UserVO;
import org.example.base.config.SysConfig;
import org.example.base.enums.EStatus;
import org.example.base.enums.ErrorCode;
import org.example.base.enums.Messages;
import org.example.base.response.CommonResponse;
import org.example.base.uuid.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 评论 服务实现类
 * </p>
 *
 * @author moki
 * @since 2024-12-30
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisUtils redisUtil;
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private BlogServiceImpl blogService;
    @Autowired
    private UserCommentLikeMapper userCommentLikeMapper;
    @Autowired
    private FileFeignClient fileFeignClient;
    /**
     * @description: 获取博客评论
     * @author: moki
     * @date: 2025/1/5 10:07
     * @param: [uid]
     * @return: org.example.base.response.CommonResponse<java.util.List < com.example.blog_web.vo.CommentVO>>
     **/
    @Override
    public CommonResponse<List<CommentVO>> getCommentByBlogUid(CommentVO commentVO) {
        User userInfo = UserContext.getUser();
        //分页参数
        int currentPage = commentVO.getCurrentPage() != null ? commentVO.getCurrentPage() : 1;
        int pageSize = commentVO.getPageSize() != null ? commentVO.getPageSize() : 10;

        String uid = commentVO.getBlogUid();

        // 分页找出父评论
        Page<Comment> commentPage = this.baseMapper.selectPage(new Page<>(currentPage, pageSize), new LambdaQueryWrapper<Comment>()
                .eq(Comment::getBlogUid, UUIDUtil.uuidToBytes(uid))
                .eq(Comment::getStatus, EStatus.VALID)
                .isNull(Comment::getParentUid));
        List<Comment> rootComments = commentPage.getRecords();
        List<CommentVO> commentVOs = BeanUtil.copyToList(rootComments, CommentVO.class);
        // 递归组合所有父子评论
        getComments(commentVOs, userInfo);

        return CommonResponse.success(commentVOs);
    }

    /**
     * @description: 评论
     * @author: moki
     * @date: 2025/1/5 11:04
     * @param: [comment]
     * @return: org.example.base.response.CommonResponse
     **/
    @Override
    public CommonResponse submitComment(Comment comment) {
        // 获取用户信息
        User userInfo = UserContext.getUser();
        comment.setUserUid(userInfo.getUid());
        int isSuccess = this.baseMapper.insert(comment);
        if (isSuccess > 0) {
            return CommonResponse.success(Messages.SUBMIT_COMMENT_SUCCESS);
        } else {
            return CommonResponse.failure(ErrorCode.SUBMIT_COMMENT_FAILED.getCode(), ErrorCode.SUBMIT_COMMENT_FAILED.getMessage());
        }

    }
    /**
     * @description: 点赞/取消点赞评论，用redis缓存，设置定时任务存入数据库
     * @author: moki
     * @date: 2025/1/6 15:03
     * @param: [comment]
     * @return: org.example.base.response.CommonResponse
     **/
    @Override
    @Transactional
    public CommonResponse toggleLike(CommentVO commentVO) {
        User userInfo = UserContext.getUser();
        // 获取用户点赞缓存
        String userPrefix = SysConfig.COMMENT_TOGGLE_LIKE_PREFIX + commentVO.getUid() +SysConfig.USER+ userInfo.getUid();
        String statusPrefix = SysConfig.COMMENT_TOGGLE_LIKE_PREFIX + commentVO.getUid() + SysConfig.STATUS  ;


        Integer status = redisUtil.get(statusPrefix, Integer.class);

        // 1.判断时点赞还是取消点赞
        if (commentVO.getIsLiked()) {
            // 点赞
            // 更新缓存
            UserCommentLike likeUser = new UserCommentLike();
            likeUser.setUserUid(userInfo.getUid());
            likeUser.setCommentUid(commentVO.getUid());
            redisUtil.set(userPrefix, likeUser, SysConfig.TOGGLE_LIKE_EXPIRE_TIME);
        } else {
            // 取消点赞
            // 删除user缓存
            redisUtil.delete(userPrefix);
        }

        // 2.更新status缓存
        if (status == null) {
            // 1.缓存已经失效
            // 更新数据库
            Comment comment = this.baseMapper.selectOne(new LambdaQueryWrapper<Comment>().eq(Comment::getUid, UUIDUtil.uuidToBytes(commentVO.getUid())).eq(Comment::getStatus, EStatus.VALID));
            if (comment == null) {
                // 评论已删除，或评论不存在
                return CommonResponse.failure(ErrorCode.COMMENT_NOT_EXIST.getCode(), ErrorCode.COMMENT_NOT_EXIST.getMessage());
            }
            comment.setLikes(commentVO.getLikes());
            this.baseMapper.update(comment, new LambdaQueryWrapper<Comment>().eq(Comment::getUid, UUIDUtil.uuidToBytes(commentVO.getUid())));
        }
        redisUtil.set(statusPrefix, commentVO.getLikes());

        return CommonResponse.success(Messages.TOGGLE_LIKE_SUCCESS);
    }

    @Override
    public CommonResponse<List<CommentVO>> getUserComment(@RequestBody CommentVO commentVO) {
        User userInfo = UserContext.getUser();
        //分页参数
        int currentPage = commentVO.getCurrentPage() != null ? commentVO.getCurrentPage() : 1;
        int pageSize = commentVO.getPageSize() != null ? commentVO.getPageSize() : 10;

        String uid = userInfo.getUid();

        // 分页找出父评论
        Page<Comment> commentPage = this.baseMapper.selectPage(new Page<>(currentPage, pageSize), new LambdaQueryWrapper<Comment>()
                .eq(Comment::getUserUid, UUIDUtil.uuidToBytes(uid))
                .eq(Comment::getStatus, EStatus.VALID)
                .isNull(Comment::getParentUid));
        List<Comment> rootComments = commentPage.getRecords();
        List<CommentVO> commentVOs = BeanUtil.copyToList(rootComments, CommentVO.class);
        // 递归组合所有父子评论
        getComments(commentVOs, userInfo);

        // 组装博客信息

        for (CommentVO comment : commentVOs) {
            if (comment.getBlogUid() != null && blogMapper.selectCount(new LambdaQueryWrapper<Blog>().eq(Blog::getUid, UUIDUtil.uuidToBytes(comment.getBlogUid())).eq(Blog::getStatus, EStatus.VALID) ) > 0) {
                CommonResponse<BlogDetailVO> blogDetailVOCommonResponse = blogService.blogDetailByUid(comment.getBlogUid());
                if (blogDetailVOCommonResponse.getData() != null) {
                    comment.setBlogVO(blogDetailVOCommonResponse.getData());
                }
            }

        }

        return CommonResponse.success(commentVOs);


    }

    @Override
    public CommonResponse updateComment(CommentVO comment) {
        Comment target = this.baseMapper.selectOne(new LambdaQueryWrapper<Comment>().eq(Comment::getUid, UUIDUtil.uuidToBytes(comment.getUid())).eq(Comment::getStatus, EStatus.VALID));
        if (target == null) {
            return CommonResponse.failure(ErrorCode.COMMENT_NOT_EXIST.getCode(), ErrorCode.COMMENT_NOT_EXIST.getMessage());
        }
        target.setContent(comment.getContent());
        int update = this.baseMapper.update(target, new LambdaQueryWrapper<Comment>().eq(Comment::getUid, UUIDUtil.uuidToBytes(comment.getUid())));
        if (update > 0) {
            return CommonResponse.success(Messages.UPDATE_SUCCESS);
        }
        return CommonResponse.failure(ErrorCode.UPDATE_FAILED.getCode(), ErrorCode.UPDATE_FAILED.getMessage());
    }

    @Override
    public CommonResponse deleteComment(CommentVO comment) {
        this.baseMapper.delete(new LambdaQueryWrapper<Comment>().eq(Comment::getUid, UUIDUtil.uuidToBytes(comment.getUid())));
        return CommonResponse.success(Messages.DELETE_SUCCESS);
    }

//    /**
//     * @description: 取消点赞，用redis缓存，设置定时任务存入数据库
//     * @author: moki
//     * @date: 2025/1/7 11:01
//     * @param: [comment]
//     * @return: org.example.base.response.CommonResponse
//     **/
//    @Override
//    public CommonResponse cancelToggleLike(Comment comment) {
//        Long res = redisUtil.decrBy(SysConfig.TOGGLE_LIKE_PREFIX + comment.getUid(), 1);
//        // 缓存失效
//        if (res == null) {
//            Comment target = this.baseMapper.selectOne(new LambdaQueryWrapper<Comment>().eq(Comment::getUid, UUIDUtil.uuidToBytes(comment.getUid())).eq(Comment::getStatus, EStatus.VALID));
//            if (target == null) {
//                return CommonResponse.failure(ErrorCode.COMMENT_NOT_EXIST.getCode(), ErrorCode.COMMENT_NOT_EXIST.getMessage());
//            }
//            target.setLikes(target.getLikes() - 1);
//            redisUtil.set(SysConfig.TOGGLE_LIKE_PREFIX + comment.getUid(), target.getLikes(), SysConfig.TOGGLE_LIKE_EXPIRE_TIME);
//        }
//        return CommonResponse.success(Messages.CANCEL_TOGGLE_LIKE_SUCCESS);
//    }

    private int getComments(List<CommentVO> rootComments, User userInfo) {
        int count = 0;
        for (CommentVO commentVO : rootComments) {
            // 组装评论者信息
            UserVO userVO = new UserVO();
            User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUid, UUIDUtil.uuidToBytes(commentVO.getUserUid())));
            BeanUtil.copyProperties(user, userVO);
            // 处理用户头像
            if (user.getAvatarUid() != null) {
                Mono<CommonResponse> fileUrl = fileFeignClient.getFileUrlById(user.getAvatarUid());
                CommonResponse response = fileUrl.block();
                userVO.setAvatarUrl(Optional.ofNullable(response)
                        .map(CommonResponse::getData)
                        .map(Object::toString)
                        .orElse(null));
            }
            commentVO.setUserVO(userVO);



            // 从缓存获取该用户对此博客的点赞状态(若用户已经登录）
            if (UserContext.getUser() != null) {
                String userPrefix = SysConfig.COMMENT_TOGGLE_LIKE_PREFIX + commentVO.getUid() + SysConfig.USER + userInfo.getUid();
                if (redisUtil.hasKey(userPrefix)) {
                    commentVO.setIsLiked(true);
                } else {
                    commentVO.setIsLiked(false);
                }
            }

            // 从缓存中获取点赞数量
            String statusPrefix = SysConfig.COMMENT_TOGGLE_LIKE_PREFIX + commentVO.getUid() + SysConfig.STATUS;
            Integer likes = redisUtil.get(statusPrefix, Integer.class);
            if (likes != null) {
                commentVO.setLikes(likes);
            }

            List<Comment> childComments = this.baseMapper.selectList(new LambdaQueryWrapper<Comment>().eq(Comment::getParentUid, UUIDUtil.uuidToBytes(commentVO.getUid())).eq(Comment::getStatus, EStatus.VALID));
            if (!childComments.isEmpty()) {
                List<CommentVO> children = BeanUtil.copyToList(childComments, CommentVO.class);
                commentVO.setChildren(children);
                commentVO.setChildCount(children.size() + getComments(children, userInfo));
                count += commentVO.getChildCount();
            }
        }
        return count;
    }
}
