package com.example.blog_web.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode
public class BlogDetailVO {
    private BlogVO blogVO;
    private UserVO userVO;
    private List<TagVO> tags;
    private UserCategoryVO userCategory;
}
