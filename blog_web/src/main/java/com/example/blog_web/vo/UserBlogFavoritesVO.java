package com.example.blog_web.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author LiuMaoJi
 * @Date 2025/1/8
 **/
@Data
@Getter
@Setter
public class UserBlogFavoritesVO {
    private String userUid;
    private String blogUid;
    private String categoryUid;

    private Boolean isCollected;
}
