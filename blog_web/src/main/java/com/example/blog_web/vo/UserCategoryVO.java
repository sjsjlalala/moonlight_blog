package com.example.blog_web.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.base.vo.BaseVO;

import java.util.List;

/**
 * @Description
 * @Author LiuMaoJi
 * @Date 2025/1/1
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class UserCategoryVO extends BaseVO<UserCategoryVO> {

    private String userUid;

    private String categoryName;

    private String description;

    private String parentUid;
    private Boolean isCollected; // 是否已经收录

    private Integer sort;

    private List<BlogVO> blogs;
}
