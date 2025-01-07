package com.example.blog_web.dto;

/**
 * @Description: 点赞状态dto
 * @Author LiuMaoJi
 * @Date 2025/1/7
 **/
public class ToggleLikeDTO {
    private String uid;
    private String blogUid;
    private String commentUid;
    private Boolean isLiked;
    private Integer likes;
}
