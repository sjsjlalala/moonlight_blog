package com.example.blog_file.restApi;

import com.example.blog_common.util.ThrowableUtils;
import com.example.blog_file.service.IUserFileService;
import io.swagger.annotations.Api;
import org.example.base.response.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * @Description: 文件的删除操作
 * @Author LiuMaoJi
 * @Date 2025/1/1
 **/
@RestController
@RequestMapping("/file")
@Api(tags = "文件的删除接口", description ="文件删除接口" )
public class DeleteFile {
    @Autowired
    private IUserFileService userFileService;
    @PostMapping("/deleteFile")
    public CommonResponse<String> deleteFile(@RequestParam("uuids") ArrayList<String> uuids) {
        ThrowableUtils.checkList(uuids);
        return userFileService.deleteFile(uuids);
    }
}
