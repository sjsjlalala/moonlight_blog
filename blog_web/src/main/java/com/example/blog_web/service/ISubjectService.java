package com.example.blog_web.service;

import com.example.blog_web.entity.Subject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.blog_web.vo.OptionVO;
import com.example.blog_web.vo.SubjectDetailVO;
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

    CommonResponse<List<SubjectDetailVO>> fetchSubjectDetail(SubjectDetailVO subjectDetailVO);
}
