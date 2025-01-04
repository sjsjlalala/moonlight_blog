package com.example.blog_web.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description: 用户信息VO
 * @Author LiuMaoJi
 * @Date 2025/1/3
 **/
@Data
@Setter
@Getter
public class UserVO {

    private String username;

    private String email;

    private String phone;

    private Integer type;

    private String remarks;
}
