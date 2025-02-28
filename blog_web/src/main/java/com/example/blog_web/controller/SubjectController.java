package com.example.blog_web.controller;

import com.example.blog_web.entity.Subject;
import com.example.blog_web.service.ISubjectService;
import com.example.blog_web.vo.OptionVO;
import com.example.blog_web.vo.SubjectDetailVO;
import com.example.blog_web.vo.SubjectVO;
import feign.Param;
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
    public CommonResponse<List<SubjectDetailVO>> fetchSubjectDetail(@RequestBody SubjectVO subjectDetailVO) {
        return subjectService.fetchSubjectDetail(subjectDetailVO);
    }
    @ApiOperation(value = "更新专题", notes = "更新专题")
    @PostMapping("/updateSubject")
    public CommonResponse updateSubject(@RequestBody SubjectVO subjectVO) {
        return subjectService.updateSubject(subjectVO);
    }
    @ApiOperation(value = "获取专题下的博客", notes = "获取专题下的博客")
    @PostMapping("/fetchSubjectBlog")
    public CommonResponse fetchSubjectBlog(@RequestBody SubjectVO subjectVO) {
        return subjectService.fetchSubjectBlog(subjectVO);
    }
    @ApiOperation(value = "给专题添加博客", notes = "给专题添加博客")
    @PostMapping("/addSubjectBlog")
    public CommonResponse addBlogToSubject(@Param("subjectUid") String subjectUid, @Param("blogUid") String blogUid) {
        return subjectService.addBlogToSubject(subjectUid, blogUid);
    }
    @ApiOperation(value = "删除专题下的博客", notes = "删除专题下的博客")
    @PostMapping("/deleteSubjectBlog")
    public CommonResponse deleteSubjectBlog(@Param("subjectUid") String subjectUid, @Param("blogUid") String blogUid) {
        return subjectService.deleteSubjectBlog(subjectUid, blogUid);
    }

}
