package com.example.blog_web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.blog_web.entity.Subject;
import com.example.blog_web.vo.OptionVO;
import com.example.blog_web.vo.SubjectDetailVO;
import com.example.blog_web.vo.SubjectVO;
import org.example.base.response.CommonResponse;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author moki
 * @since 2025-01-11
 */
public interface ISubjectService extends IService<Subject> {

    CommonResponse createSubject(Subject subject);

    CommonResponse<List<OptionVO>> fetchUserSubject();

    CommonResponse<List<SubjectDetailVO>> fetchSubjectDetail(SubjectVO subjectDetailVO);

    CommonResponse updateSubject(SubjectVO subjectVO);

    CommonResponse fetchSubjectBlog(SubjectVO subjectVO);

    CommonResponse addBlogToSubject(String subjectUid, String blogUid);

    CommonResponse deleteSubjectBlog(String subjectUid, String blogUid);
}
