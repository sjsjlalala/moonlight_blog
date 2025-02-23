package com.example.blog_web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blog_common.entity.User;
import com.example.blog_common.feign.FileFeignClient;
import com.example.blog_common.mapper.UserMapper;
import com.example.blog_web.context.UserContext;
import com.example.blog_web.entity.BlogSubject;
import com.example.blog_web.entity.Subject;
import com.example.blog_web.mapper.BlogMapper;
import com.example.blog_web.mapper.BlogSubjectMapper;
import com.example.blog_web.mapper.SubjectMapper;
import com.example.blog_web.service.ISubjectService;
import com.example.blog_web.vo.*;
import org.example.base.enums.EStatus;
import org.example.base.enums.ErrorCode;
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
    public CommonResponse<List<SubjectDetailVO>> fetchSubjectDetail(SubjectDetailVO subjectDetailVO) {
        List<SubjectDetailVO> res = new ArrayList<>();
        // 设置默认值
        int currentPage = subjectDetailVO.getCurrentPage() != null ? subjectDetailVO.getCurrentPage() : 1;
        int pageSize = subjectDetailVO.getPageSize() != null ? subjectDetailVO.getPageSize() : 9;
        String keyword = subjectDetailVO.getKeyword() != null ? subjectDetailVO.getKeyword() : "";
        String filterWord = subjectDetailVO.getFilterWord() != null ? subjectDetailVO.getFilterWord() : "";

        // 分页查找,根据关键字查找
        LambdaQueryWrapper<Subject> queryWrapper = new LambdaQueryWrapper<Subject>()
                .eq(Subject::getStatus, EStatus.VALID)
                .like(Subject::getSubjectName, keyword);

        if (!filterWord.isEmpty()) {
            queryWrapper.eq(Subject::getCategoryUid, UUIDUtil.uuidToBytes(filterWord));
        }

        Page<Subject> subjectPage = this.baseMapper.selectPage(new Page<>(currentPage, pageSize), queryWrapper);

        List<Subject> subjects = subjectPage.getRecords();

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
            Mono<CommonResponse> fileUrlById = fileFeignClient.getFileUrlById(subject.getFileUid());
            String url = Objects.requireNonNull(fileUrlById.block()).getData().toString();
            subjectVO.setFileUid(url);
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


            // 组装分页信息
            temp.setCurrentPage(currentPage);
            temp.setPageSize(pageSize);
            res.add(temp);
        }


        return CommonResponse.success(res);
    }
}
