package com.example.blog_web.vo;

import lombok.Data;
import org.example.base.vo.BaseVO;

/**
 * @Description
 * @Author LiuMaoJi
 * @Date 2025/2/27
 **/
@Data
public class FollowVO  extends BaseVO<FollowVO> {

    private String followerId;

    private String followedId;

    private Boolean isFollowed;
}
