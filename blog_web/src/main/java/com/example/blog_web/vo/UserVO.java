package com.example.blog_web.vo;

import lombok.Data;
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
public class UserVO extends BaseVO<UserVO> {
    private String avatarUrl = "http://localhost:8084/upload-files/images/admin-9f6e9614-c2d1-11ef-8b2a-0242ac110003/1735904055874_d4969d732d2943919ffaa610e509e4d6.png";
    private String username;

    private String email;

    private String phone;

    private Integer type;

    private String remarks;
}
