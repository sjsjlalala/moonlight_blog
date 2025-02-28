package com.example.blog_web.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.example.base.vo.BaseVO;

/**
 * @Description: 用户信息VO
 * @Author LiuMaoJi
 * @Date 2025/1/3
 **/
@Data
@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
public class UserVO extends BaseVO<UserVO> {
    private String avatarUid;
    private String avatarUrl ;
    private String username;

    private String email;

    private String phone;

    private Integer type;

    private String remarks;
    private int blogCount;
    private int visitCount;

    private int CommentCount;
    private int likes;
    private int Favorites;


}
