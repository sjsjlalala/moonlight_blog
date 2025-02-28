package com.example.blog_web.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author LiuMaoJi
 * @Date 2025/2/27
 **/
@Data
public class FollowDetailVO {
    private List<BlogDetailVO> blogs;
    private UserVO user;
}
