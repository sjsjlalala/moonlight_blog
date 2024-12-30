package com.example.blog_file.restApi;

import com.example.blog_file.response.ImageResponse;
import com.example.blog_file.service.IUserFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Description 图片上传
 * @Author LiuMaoJi
 * @Date 2024/12/28
 **/
@RestController
@RequestMapping("/upload")
public class UploadFile {
    @Autowired
    IUserFileService fileService;

    @PostMapping("/uploadImage")
    public ImageResponse handleFileUpload(@RequestParam("file") List<MultipartFile> files) {
       return fileService.uploadImage(files);
    }


    @GetMapping("/test")
    public String test(){
        System.out.println("test");
        return "test";
    }
}
