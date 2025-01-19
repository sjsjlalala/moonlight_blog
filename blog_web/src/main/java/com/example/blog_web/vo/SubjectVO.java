package com.example.blog_web.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author LiuMaoJi
 * @Date 2025/1/13
 **/
@Data
@Setter
@Getter
public class SubjectVO {
    private String uid;
    private String subjectName;
    private String summary;

    private String fileUid;

    private String userUid;

    private Integer clicks = 0;

    private Integer favorites = 0;

    private String categoryUid ;

    private Integer sort;
}
