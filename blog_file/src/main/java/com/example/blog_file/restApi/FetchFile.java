package com.example.blog_file.restApi;

import com.example.blog_file.entity.UserFile;
import com.example.blog_file.service.IUserFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.base.response.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 获取图片
 * @Author LiuMaoJi
 * @Date 2025/1/3
 **/
@RestController
@RequestMapping("/fetchFile")
@Api(tags = "图片获取接口", description ="图片获取接口" )
public class FetchFile {
    @Autowired
    private IUserFileService userFileService;
    @GetMapping("/fileDetailById")
    @ApiOperation(value ="根据uid获取图片详情")
    public CommonResponse<UserFile> fileDetail(@RequestParam("uid") String uid) {

        return userFileService.getFileDetailById(uid);
    }
    @GetMapping("/fileUrlById")
    @ApiOperation(value ="根据uid获取图片url")
    public CommonResponse<String> fileUrl(@RequestParam("uid") String uid) {

        return userFileService.getFileUrlById(uid);
    }
}
