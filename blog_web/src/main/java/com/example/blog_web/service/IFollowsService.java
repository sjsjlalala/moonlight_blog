package com.example.blog_web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.blog_web.entity.Follows;
import com.example.blog_web.vo.FollowVO;
import org.example.base.response.CommonResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author moki
 * @since 2025-02-27
 */
public interface IFollowsService extends IService<Follows> {


    CommonResponse followUser(FollowVO followVO);

    CommonResponse getFollowStatus(FollowVO followVO);

    CommonResponse getFollowBlogs(FollowVO followVO);

    CommonResponse getFollowList(FollowVO followVO);
}
