package com.example.blog_web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blog_common.entity.User;
import com.example.blog_common.feign.FileFeignClient;
import com.example.blog_common.mapper.UserMapper;
import com.example.blog_web.context.UserContext;
import com.example.blog_web.entity.Blog;
import com.example.blog_web.entity.BlogSubject;
import com.example.blog_web.entity.Subject;
import com.example.blog_web.mapper.BlogMapper;
import com.example.blog_web.mapper.BlogSubjectMapper;
import com.example.blog_web.mapper.SubjectMapper;
import com.example.blog_web.service.ISubjectService;
import com.example.blog_web.vo.*;
import org.example.base.enums.EStatus;
import org.example.base.enums.ErrorCode;
import org.example.base.enums.Messages;
import org.example.base.response.CommonResponse;
import org.example.base.uuid.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author moki
 * @since 2025-01-11
 */
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements ISubjectService {
    @Autowired
    private FileFeignClient fileFeignClient;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BlogSubjectMapper blogSubjectMapper;
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private BlogServiceImpl blogService;
    @Override
    public CommonResponse createSubject(Subject subject) {
        // 添加用户信息
        User userInfo = UserContext.getUser();
        subject.setUserUid(userInfo.getUid());
        // 添加专题
        if (subject.insert()) {
            return CommonResponse.success(subject);
        }
        // 添加
        return CommonResponse.failure(ErrorCode.CREATE_SUBJECT_FAILED.getCode(), ErrorCode.CREATE_SUBJECT_FAILED.getMessage());
    }
    /**
     * @description: 获取用户专题选项
     * @author: moki
     * @date: 2025/1/11 18:30
     * @param: []
     * @return: org.example.base.response.CommonResponse<java.util.List < com.example.blog_web.vo.OptionVO>>
     **/
    @Override
    public CommonResponse<List<OptionVO>> fetchUserSubject() {
        List<OptionVO> res = new ArrayList<>();
        User userInfo = UserContext.getUser();
        List<Subject> subjects = this.baseMapper.selectList(new LambdaQueryWrapper<Subject>().eq(Subject::getUserUid, UUIDUtil.uuidToBytes(userInfo.getUid())).eq(Subject::getStatus, EStatus.VALID));
        if (subjects != null) {
            subjects.forEach(subject -> {
                OptionVO optionVO = new OptionVO();
                optionVO.setValue(subject.getUid());
                optionVO.setLabel(subject.getSubjectName());
                res.add(optionVO);
            });
            return CommonResponse.success(res);
        }
        return null;
    }
    /**
     * @description: 获取专题详情
     * @author: moki
     * @date: 2025/1/13 12:35
     * @param: [subjectDetailVO]
     * @return: org.example.base.response.CommonResponse<java.util.List < com.example.blog_web.vo.SubjectDetailVO>>
     **/
    @Override
    public CommonResponse<List<SubjectDetailVO>> fetchSubjectDetail(SubjectVO requestVO) {
        List<SubjectDetailVO> res = new ArrayList<>();
        // 设置默认值
        String keyword = requestVO.getKeyword() != null ? requestVO.getKeyword() : "";
        String filterWord = requestVO.getFilterWord() != null ? requestVO.getFilterWord() : "";

        // 分页查找,根据关键字查找
        LambdaQueryWrapper<Subject> queryWrapper = new LambdaQueryWrapper<Subject>()
                .eq(Subject::getStatus, EStatus.VALID)
                .like(Subject::getSubjectName, keyword);

        if (!filterWord.isEmpty()) {
            queryWrapper.eq(Subject::getCategoryUid, UUIDUtil.uuidToBytes(filterWord));
        }
        if (requestVO.getUid() != null && !requestVO.getUid().isEmpty()) {
            queryWrapper.eq(Subject::getUid, UUIDUtil.uuidToBytes((requestVO.getUid())));
        }

        List<Subject> subjects = this.baseMapper.selectList(queryWrapper);


        // 组转VO
        for (Subject subject : subjects) {
            SubjectDetailVO temp = new SubjectDetailVO();
            // 获取相关专题用户信息
            User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUid, UUIDUtil.uuidToBytes(subject.getUserUid())));
            // 组装用户信息
            UserVO userVO = new UserVO();
            BeanUtil.copyProperties(user, userVO);
            temp.setUser(userVO);

            // 组装专题信息
            SubjectVO subjectVO = new SubjectVO();
            BeanUtil.copyProperties(subject, subjectVO);
            // 组装专题图片
            if (subject.getFileUid() != null) {
                Mono<CommonResponse> fileUrlById = fileFeignClient.getFileUrlById(subject.getFileUid());
                String url = Objects.requireNonNull(fileUrlById.block()).getData().toString();
                subjectVO.setFileUrl(url);
            }
            temp.setSubject(subjectVO);

            // 组装博客信息
            List<BlogVO> blogVOs = new ArrayList<>();
            List<BlogSubject> blogSubjects = blogSubjectMapper.selectList(new LambdaQueryWrapper<BlogSubject>().eq(BlogSubject::getSubjectUid, UUIDUtil.uuidToBytes(subject.getUid())).eq(BlogSubject::getStatus, EStatus.VALID));
            if (!blogSubjects.isEmpty()) {
                //根据uid拼装博客信息
                List<String> uids = blogSubjects.stream().map(BlogSubject::getBlogUid).toList();
                uids.forEach(uid -> {
                    CommonResponse<BlogDetailVO> blogDetailVOCommonResponse = blogService.blogDetailByUid(uid);
                    if (blogDetailVOCommonResponse.getData() != null) {
                        BlogVO blogVO = blogDetailVOCommonResponse.getData().getBlogVO();
                        blogVOs.add(blogVO);
                    }
                });
                temp.setBlogs(blogVOs);
            }


            res.add(temp);
        }


        return CommonResponse.success(res);
    }

    @Override
    public CommonResponse updateSubject(SubjectVO subjectVO) {
        // 更新专题元数据
        if (subjectVO != null) {
            Subject subject = this.baseMapper.selectOne(new LambdaQueryWrapper<Subject>().eq(Subject::getUid, UUIDUtil.uuidToBytes(subjectVO.getUid())));
            if (subject != null) {
                subject.setSubjectName(subjectVO.getSubjectName());
                subject.setSummary(subjectVO.getSummary());
                subject.setCategoryUid(subjectVO.getCategoryUid());
                // 更新专题封面
                if (!subjectVO.getFileUid().equals(subject.getFileUid())) {
                    subject.setFileUid(subjectVO.getFileUid());
                    ArrayList<String> fileUuids = new ArrayList<>();
                    fileUuids.add(subject.getFileUid());
                    fileFeignClient.deleteFile(fileUuids);
                }

            }
            int update = this.baseMapper.update(subject, new LambdaQueryWrapper<Subject>().eq(Subject::getUid, UUIDUtil.uuidToBytes(subject.getUid())));
            if (update != 0) {
                return CommonResponse.success(Messages.UPDATE_SUCCESS);
            } else {
                return CommonResponse.failure(ErrorCode.OPERATION_FAILED.getCode(), ErrorCode.OPERATION_FAILED.getMessage());
            }
        }

        return null;
    }

    @Override
    public CommonResponse fetchSubjectBlog(SubjectVO subjectVO) {

        LambdaQueryWrapper<BlogSubject> queryWrapper = new LambdaQueryWrapper<BlogSubject>();
        queryWrapper.eq(BlogSubject::getSubjectUid, UUIDUtil.uuidToBytes(subjectVO.getUid()));
        queryWrapper.eq(BlogSubject::getStatus, EStatus.VALID);

        if (subjectVO.getKeyword() != null && !subjectVO.getKeyword().isEmpty()) {
            List<BlogSubject> blogSubjects = blogSubjectMapper.selectList(queryWrapper);
            for (BlogSubject blogSubject : blogSubjects) {
                List<Blog> blogs = blogMapper.selectList(
                        new LambdaQueryWrapper<Blog>()
                                .eq(Blog::getUid, UUIDUtil.uuidToBytes(blogSubject.getBlogUid()))
                                .eq(Blog::getStatus, EStatus.VALID)
                                .like(Blog::getTitle, subjectVO.getKeyword()));
                return CommonResponse.success(blogs);
            }
        }

        List<BlogSubject> blogSubjects = blogSubjectMapper.selectList(
                new LambdaQueryWrapper<BlogSubject>()
                        .eq(BlogSubject::getSubjectUid, UUIDUtil.uuidToBytes(subjectVO.getUid()))
                        .eq(BlogSubject::getStatus, EStatus.VALID));
        List<Blog> blogs = new ArrayList<>();
        for (BlogSubject blogSubject : blogSubjects) {
            Blog blog = blogMapper.selectOne(
                    new LambdaQueryWrapper<Blog>()
                            .eq(Blog::getUid, UUIDUtil.uuidToBytes(blogSubject.getBlogUid()))
                            .eq(Blog::getStatus, EStatus.VALID));
            blogs.add(blog);

        }
        return CommonResponse.success(blogs);
    }

    @Override
    public CommonResponse addBlogToSubject(String subjectUid, String blogUid) {
        if (blogSubjectMapper.selectOne(new LambdaQueryWrapper<BlogSubject>().eq(BlogSubject::getSubjectUid, UUIDUtil.uuidToBytes(subjectUid)).eq(BlogSubject::getBlogUid, UUIDUtil.uuidToBytes(blogUid))) != null) {
            return CommonResponse.failure(ErrorCode.Blog_ADD_FAILED.getCode(), ErrorCode.Blog_ADD_FAILED.getMessage());
        }
            BlogSubject blogSubject = new BlogSubject();
            blogSubject.setSubjectUid(subjectUid);
            blogSubject.setBlogUid(blogUid);
            blogSubject.setStatus(EStatus.VALID);
            blogSubjectMapper.insert(blogSubject);
             return CommonResponse.success(Messages.ADD_BLOG_SUCCESS);
    }

    @Override
    public CommonResponse deleteSubjectBlog(String subjectUid, String blogUid) {

        int delete = blogSubjectMapper.delete(new LambdaQueryWrapper<BlogSubject>().eq(BlogSubject::getSubjectUid, UUIDUtil.uuidToBytes(subjectUid)).eq(BlogSubject::getBlogUid, UUIDUtil.uuidToBytes(blogUid)));
        if (delete == 0) {
            return CommonResponse.failure(ErrorCode.DELETE_FAILED.getCode(), ErrorCode.DELETE_FAILED.getMessage());
        }
        return CommonResponse.success(Messages.DELETE_SUCCESS);
    }
}
