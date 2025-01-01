package com.example.blog_web.controller;

import com.example.blog_web.service.impl.TagServiceImpl;
import com.example.blog_web.vo.TagVO;
import io.swagger.annotations.ApiOperation;
import org.example.base.response.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description：标签
 * @Author LiuMaoJi
 * @Date 2024/12/31
 **/
@RestController
@RequestMapping("/tag")
public class TagController {
    @Autowired
    private TagServiceImpl tagService;
    @ApiOperation(value = "获取标签", notes = "获取标签")
    @GetMapping("/getTags")
    public CommonResponse<List<TagVO>> getTags() {
        return tagService.getTags();
    }
}
