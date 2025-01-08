package com.example.blog_web.vo;

import lombok.Data;
import org.example.base.vo.BaseVO;

/**
 * @Description
 * @Author LiuMaoJi
 * @Date 2025/1/1
 **/
@Data
public class UserCategoryVO extends BaseVO<UserCategoryVO> {

    private String userUid;

    private String categoryName;

    private String description;

    private String parentUid;
    private Boolean isCollected; // 是否已经收录

    private Integer sort;
}
