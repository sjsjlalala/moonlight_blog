package com.example.blog_web.vo;

import lombok.Data;

/**
 * @Description
 * @Author LiuMaoJi
 * @Date 2025/1/1
 **/
@Data
public class UserCategoryVO {


    private String userUid;


    private String categoryName;


    private String description;


    private String parentUid;



    private Integer sort;
}
