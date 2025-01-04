package com.example.blog_web.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description：博客详情VO
 * @Author LiuMaoJi
 * @Date 2025/1/3
 **/
@Data
@Setter
@Getter
public class BlogDetailVO {
    private BlogVO blogVO;
    private UserVO userVO;
    private List<TagVO> tags;
    private UserCategoryVO userCategory;
}
