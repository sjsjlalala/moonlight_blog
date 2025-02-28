package com.example.blog_web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blog_web.entity.Blog;
import com.example.blog_web.entity.Follows;
import com.example.blog_web.mapper.BlogMapper;
import com.example.blog_web.mapper.FollowsMapper;
import com.example.blog_web.service.IFollowsService;
import com.example.blog_web.vo.BlogDetailVO;
import com.example.blog_web.vo.FollowDetailVO;
import com.example.blog_web.vo.FollowVO;
import com.example.blog_web.vo.UserVO;
import org.example.base.enums.EStatus;
import org.example.base.enums.Messages;
import org.example.base.response.CommonResponse;
import org.example.base.uuid.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author moki
 * @since 2025-02-27
 */
@Service
public class FollowsServiceImpl extends ServiceImpl<FollowsMapper, Follows> implements IFollowsService {
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private BlogServiceImpl blogService;
    @Override
    public CommonResponse followUser(FollowVO followVO) {
        if (!followVO.getIsFollowed()) {
            if (followVO.getFollowerId().equals(followVO.getFollowedId())) {
                return CommonResponse.failure(400, "不能取关自己");
            }
            // 取关
            Follows follows = this.baseMapper.selectOne(new LambdaQueryWrapper<Follows>()
                    .eq(Follows::getFollowerId, UUIDUtil.uuidToBytes(followVO.getFollowerId()))
                    .eq(Follows::getFollowedId, UUIDUtil.uuidToBytes(followVO.getFollowedId())));
            follows.setStatus(EStatus.INVALID);
            this.baseMapper.update(follows, new LambdaQueryWrapper<Follows>().eq(Follows::getFollowerId, UUIDUtil.uuidToBytes(followVO.getFollowerId())));

        }
        else {
            // 关注
            if (followVO.getFollowerId().equals(followVO.getFollowedId())) {
                return CommonResponse.failure(400, "不能关注自己");
            }
            Follows follows = this.baseMapper.selectOne(new LambdaQueryWrapper<Follows>()
                    .eq(Follows::getFollowerId, UUIDUtil.uuidToBytes(followVO.getFollowerId()))
                    .eq(Follows::getFollowedId, UUIDUtil.uuidToBytes(followVO.getFollowedId())));
            if (follows == null) {
                Follows temp = new Follows();
                temp.setFollowerId(followVO.getFollowerId());
                temp.setFollowedId(followVO.getFollowedId());
                temp.setStatus(EStatus.VALID);
                this.baseMapper.insert(temp);
            }
            else {
                follows.setStatus(EStatus.VALID);
                this.baseMapper.update(follows, new LambdaQueryWrapper<Follows>().eq(Follows::getFollowerId, UUIDUtil.uuidToBytes(followVO.getFollowerId())));
            }
        }

        return CommonResponse.success(Messages.UPDATE_SUCCESS);
    }

    @Override
    public CommonResponse getFollowStatus(FollowVO followVO) {
        // 输入参数校验
        if (followVO == null || followVO.getFollowerId() == null || followVO.getFollowedId() == null) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        try {
            // 查询关注状态
            Follows follows = this.baseMapper.selectOne(new LambdaQueryWrapper<Follows>()
                    .eq(Follows::getFollowerId, UUIDUtil.uuidToBytes(followVO.getFollowerId()))
                    .eq(Follows::getFollowedId, UUIDUtil.uuidToBytes(followVO.getFollowedId())));

            // 设置关注状态
            followVO.setIsFollowed(follows != null && follows.getStatus() != EStatus.INVALID);
        } catch (IllegalArgumentException e) {
            // 处理 UUID 转换异常
            throw new IllegalArgumentException("Invalid UUID format", e);
        }

        return CommonResponse.success(followVO);
    }

    @Override
    public CommonResponse getFollowBlogs(FollowVO followVO) {
        List<FollowDetailVO> res = new ArrayList<>();

        String uid = followVO.getFollowerId();
        // 查询关注状态
        List<Follows> follows = this.baseMapper.selectList(new LambdaQueryWrapper<Follows>()
                .eq(Follows::getFollowerId, UUIDUtil.uuidToBytes(uid))
                .eq(Follows::getStatus, EStatus.VALID));

        // 组装信息
        for (Follows follow : follows) {
            FollowDetailVO followDetailVO = new FollowDetailVO();
            followDetailVO.setBlogs(new ArrayList<>());
            // 获取该作者的信息
            CommonResponse userHomePage = blogService.getUserHomePage(follow.getFollowedId());
            if (userHomePage.getData() != null) {
                followDetailVO.setUser((UserVO) userHomePage.getData());
            }


            // 获取该作者博客
            List<Blog> blogs = blogMapper.selectList(new LambdaQueryWrapper<Blog>()
                    .eq(Blog::getAuthorUid, UUIDUtil.uuidToBytes(follow.getFollowedId()))
                    .eq(Blog::getStatus, EStatus.VALID));
            for (Blog blog : blogs) {
                CommonResponse<BlogDetailVO> blogDetailVOCommonResponse = blogService.blogDetailByUid(blog.getUid());
                if (blogDetailVOCommonResponse.getData() != null) {
                        followDetailVO.getBlogs().add(blogDetailVOCommonResponse.getData());
                }
            }
            res.add(followDetailVO);
        }


        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse getFollowList(FollowVO followVO) {
        List<UserVO> res = new ArrayList<>();
        if (followVO.getFollowerId() != null) {
            List<String> uids = this.baseMapper.selectList(new LambdaQueryWrapper<Follows>()
                    .eq(Follows::getFollowerId, UUIDUtil.uuidToBytes(followVO.getFollowerId()))
                    .eq(Follows::getStatus, EStatus.VALID)).stream().map(Follows::getFollowedId).toList();
            for (String uid : uids) {
                CommonResponse userHomePage = blogService.getUserHomePage(uid);
                if (userHomePage.getData() != null) {
                    res.add((UserVO) userHomePage.getData());
                }
            }
        }
        return CommonResponse.success(res);
    }
}
