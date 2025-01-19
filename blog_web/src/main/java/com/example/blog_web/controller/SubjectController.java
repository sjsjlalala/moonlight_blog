package com.example.blog_web.controller;

import com.example.blog_web.entity.Subject;
import com.example.blog_web.service.ISubjectService;
import com.example.blog_web.vo.OptionVO;
import com.example.blog_web.vo.SubjectDetailVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.base.response.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description
 * @Author LiuMaoJi
 * @Date 2025/1/11
 **/
@RestController
@RequestMapping("/subject")
@Api(value = "博客专题相关接口", tags = {"博客专题相关接口"})
public class SubjectController {
    @Autowired
    private ISubjectService subjectService;
    @ApiOperation(value = "添加专题", notes = "添加专题")
    @PostMapping("/createSubject")
    public CommonResponse createSubject(@RequestBody Subject subject) {
        return subjectService.createSubject(subject);
    }

    @ApiOperation(value = "获取用户专题", notes = "获取用户专题")
    @GetMapping("/fetchUserSubject")
    public CommonResponse<List<OptionVO>> fetchUserSubject() {
        return subjectService.fetchUserSubject();
    }


    @ApiOperation(value = "获取专题详情", notes = "获取专题详情")
    @PostMapping("/fetchSubjectDetail")
    public CommonResponse<List<SubjectDetailVO>> fetchSubjectDetail(@RequestBody SubjectDetailVO subjectDetailVO) {
        return subjectService.fetchSubjectDetail(subjectDetailVO);
    }

}
