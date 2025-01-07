package org.example.base.config;

/**
 * @Description: 系统配置类，定义一些内部系统配置
 * @Author LiuMaoJi
 * @Date 2025/1/7
 **/
public class SysConfig {
    // 评论点赞缓存前缀
    public static final String COMMENT_TOGGLE_LIKE_PREFIX = "COMMENT:LIKE:";
    public static final String USER = ":USER:";
    public static final String STATUS = ":STATUS";
    // 评论点赞缓存前缀（用户）
    public static final String COMMENT_TOGGLE_LIKE_USER_PREFIX = "COMMENT:LIKE:USER";
    // 点赞缓存时间
    public static final long TOGGLE_LIKE_EXPIRE_TIME = 60 * 60 * 24;
    // 博客点赞缓存前缀
    public static final String BLOG_TOGGLE_LIKE_PREFIX = "BLOG:LIKE:";
}
