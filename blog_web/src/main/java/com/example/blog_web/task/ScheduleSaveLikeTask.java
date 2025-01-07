package com.example.blog_web.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.blog_common.util.RedisUtils;
import com.example.blog_web.entity.Blog;
import com.example.blog_web.entity.Comment;
import com.example.blog_web.entity.UserBlogLike;
import com.example.blog_web.entity.UserCommentLike;
import com.example.blog_web.mapper.BlogMapper;
import com.example.blog_web.mapper.CommentMapper;
import com.example.blog_web.mapper.UserBlogLikeMapper;
import com.example.blog_web.mapper.UserCommentLikeMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.base.config.SysConfig;
import org.example.base.enums.EStatus;
import org.example.base.uuid.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @Description：定时持久化redis中点赞数据的任务
 * @Author LiuMaoJi
 * @Date 2025/1/7
 **/
@Component
@Slf4j
public class ScheduleSaveLikeTask {
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserCommentLikeMapper userCommentLikeMapper;
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private UserBlogLikeMapper userBlogLikeMapper;

    /**
     * @description: 持久化评论点赞数据
     * @author: moki
     * @date: 2025/1/7 17:22
     * @param: []
     * @return: void
     **/
    @Scheduled(cron = "0 0 * * * ?")
    public void saveCommentLikeData() {
            // 评论点赞
            String prefix = SysConfig.COMMENT_TOGGLE_LIKE_PREFIX + "*";
            Set<String> commentKeys = redisUtils.keys(prefix);
            commentKeys.forEach(key -> {
                if (key.contains("STATUS")) {
                    // 评论点赞状态
                    Integer i = redisUtils.get(key, Integer.class);
                    // 获取该状态的commentUid
                    String commentUid = redisUtils.getFieldFromKey(key, 2);
                    // 持久化到数据库
                    if (i != null) {
                        // 如果该条评论还存在
                        Comment target = commentMapper.selectOne(new LambdaQueryWrapper<Comment>().eq(Comment::getUid, UUIDUtil.uuidToBytes(commentUid)).eq(Comment::getStatus, EStatus.VALID));
                        if (target != null) {
                            target.setLikes(i);
                            // 持久化
                            commentMapper.update(target, new LambdaQueryWrapper<Comment>().eq(Comment::getUid, UUIDUtil.uuidToBytes(commentUid)));
                        }
                    }

                }
                if (key.contains("USER")) {
                    // 评论点赞用户信息
                    String userUid = redisUtils.getFieldFromKey(key, 4);
                    String commentUid = redisUtils.getFieldFromKey(key, 2);
                    // 持久化到数据库
                    UserCommentLike likeUser = userCommentLikeMapper.selectOne(new LambdaQueryWrapper<UserCommentLike>().eq(UserCommentLike::getUserUid, UUIDUtil.uuidToBytes(userUid)).eq(UserCommentLike::getCommentUid, UUIDUtil.uuidToBytes(commentUid)));
                    if (likeUser == null) {
                        likeUser = new UserCommentLike();
                        likeUser.setUserUid(userUid);
                        likeUser.setCommentUid(commentUid);
                        int res = userCommentLikeMapper.insert(likeUser);
                        if (res == 0) {
                            log.error("持久化评论点赞数据失败");
                        } else {
                            log.info("持久化评论点赞数据成功");
                        }
                    }
                }

            });
    }

    /**
     * @description: 持久化博客点赞数据
     * @author: moki
     * @date: 2025/1/7 17:22
     * @param: []
     * @return: void
     **/
    @Scheduled(cron = "0 0 * * * ?")
    public void saveBlogLikeData() {
        // 评论点赞
        String prefix = SysConfig.BLOG_TOGGLE_LIKE_PREFIX + "*";
        Set<String> blogKeys = redisUtils.keys(prefix);
        blogKeys.forEach(key -> {
            if (key.contains("STATUS")) {
                // 评论点赞状态
                Integer i = redisUtils.get(key, Integer.class);
                // 获取该状态的commentUid
                String blogUid = redisUtils.getFieldFromKey(key, 2);
                // 持久化到数据库
                if (i != null) {
                    // 如果该条博客还存在
                    Blog target = blogMapper.selectOne(new LambdaQueryWrapper<Blog>().eq(Blog::getUid, UUIDUtil.uuidToBytes(blogUid)).eq(Blog::getStatus, EStatus.VALID));
                    if (target != null) {
                        target.setLikes(i);
                        // 持久化
                        blogMapper.update(target, new LambdaQueryWrapper<Blog>().eq(Blog::getUid, UUIDUtil.uuidToBytes(blogUid)));
                    }
                }

            }
            if (key.contains("USER")) {
                // 评论点赞用户信息
                String userUid = redisUtils.getFieldFromKey(key, 4);
                String blogUid = redisUtils.getFieldFromKey(key, 2);
                // 持久化到数据库
                UserBlogLike target = userBlogLikeMapper.selectOne(new LambdaQueryWrapper<UserBlogLike>().eq(UserBlogLike::getUserUid, UUIDUtil.uuidToBytes(userUid)).eq(UserBlogLike::getBlogUid, UUIDUtil.uuidToBytes(blogUid)));
                if (target == null) {
                    UserBlogLike userBlogLike = new UserBlogLike();
                    userBlogLike.setUserUid(userUid);
                    userBlogLike.setBlogUid(blogUid);
                    int res = userBlogLikeMapper.insert(userBlogLike);
                    if (res == 0) {
                        log.error("持久化博客点赞数据失败");
                    } else {
                        log.info("持久化博客点赞数据成功");
                    }
                }
            }

        });
    }

}
