package com.example.blog_web.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.example.base.vo.BaseVO;

import java.time.LocalDate;
import java.util.List;

/**
 * @Description
 * @Author LiuMaoJi
 * @Date 2025/1/5
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Setter
@Getter
public class CommentVO extends BaseVO<CommentVO> {


    private UserVO userVO;
    private BlogDetailVO blogVO;

    private String userUid;

    private String blogUid;

    private String content;

    private String parentUid;


    private List<CommentVO> children;
    private Integer childCount; // 子评论数量


    private Integer likes;
    private Boolean isLiked; // 当前点赞状态

    private LocalDate createTime;

}
