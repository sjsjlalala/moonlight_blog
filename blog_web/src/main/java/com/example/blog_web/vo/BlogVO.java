package com.example.blog_web.vo;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.example.base.vo.BaseVO;

import java.time.LocalDate;
import java.util.List;

/**
 * @description: 博客VO
 * @author: moki
 * @date: 2024/12/26 21:13
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Setter
@Getter
public class BlogVO extends BaseVO<BlogVO> {


    private String authorUid;


    private String introduction;


    private String title;


    private String content;


    private String coverImageUid;


    private String isOriginal;


    private String originalUrl;


    private String adminUid;


    private Integer clicks = 1;


    private Integer likes = 0;


    private Integer favorites = 0;


    private String visibilityScope;



    private boolean commentsAllowed ;

    private LocalDate createTime;

    private List<String> tags;

    private List<String> category;
}
