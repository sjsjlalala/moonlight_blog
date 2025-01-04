package com.example.blog_file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.blog_file.entity.UserFile;
import com.example.blog_file.response.ImageResponse;
import org.example.base.response.CommonResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author moki
 * @since 2024-12-28
 */
public interface IUserFileService extends IService<UserFile> {

    ImageResponse uploadImage(List<MultipartFile> files);


    CommonResponse<String> deleteFile(List<String> uuids);

    CommonResponse<UserFile> getFileDetailById(String uid);

    CommonResponse<String> getFileUrlById(String uid);
}
