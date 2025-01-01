package com.example.blog_file.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.blog_common.entity.User;
import com.example.blog_file.context.UserContext;
import com.example.blog_file.entity.UserFile;
import com.example.blog_file.mapper.UserFileMapper;
import com.example.blog_file.response.ImageResponse;
import com.example.blog_file.service.IUserFileService;
import com.example.blog_file.util.UserFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.base.enums.ErrorCode;
import org.example.base.enums.Messages;
import org.example.base.response.CommonResponse;
import org.example.base.uuid.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author moki
 * @since 2024-12-28
 */
@Service
@Slf4j
public class UserFileServiceImpl extends ServiceImpl<UserFileMapper, UserFile> implements IUserFileService {
    @Value("${file.image.uploadPath}")
    private String uploadPathConfig;
    @Value("${file.image.localUrl}")
    private String localUrl;
    @Value("${file.image.uploadDir}")
    private String uploadDir;
    @Value("${file.image.visualMappingPath}")
    private String visualMappingPath;
    @Autowired
    private UserFileMapper userFileMapper;
    @Override
    @Transactional
    public ImageResponse uploadImage(List<MultipartFile> files) {
        // 文件信息
        String fileUrl = "";
        File temp = null;
        UserFile userFile = new UserFile();
        // 用户信息
        User userinfo = UserContext.getUser();

        // 将文件保存在本地
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new RuntimeException("文件不能为空");
            }
            // 文件保存本地
            try {
                byte[] bytes = file.getBytes();
                String fileName = UserFileUtil.generateFileName(Objects.requireNonNull(file.getOriginalFilename()));
                String userDir = userinfo.getUsername() +'-'+ userinfo.getUid();
                String uploadPath = uploadPathConfig + uploadDir + userDir;
                // 判断路径是否存在，不存在则创建
                if (!UserFileUtil.isExist(uploadPath)) {
                    UserFileUtil.createDir(uploadPath);
                }
                // 将图片文件保存本地
                temp = UserFileUtil.saveImageFile(bytes, uploadPath, fileName);

                // 将文件元数据保存到数据库
                userFile.setAdminUid(userinfo.getUid());
                userFile.setUserUid(userinfo.getUid());
                userFile.setOriginalName(file.getOriginalFilename());
                userFile.setFileName(fileName);
                userFile.setFileSize(file.getSize());
                userFile.setFileType(file.getContentType());
                userFile.setFileUrl(uploadPath + fileName);
                if (!this.save(userFile)) {
                    return ImageResponse.failure("文件保存至数据库失败！");
                }
                // 生成本地的url
                fileUrl = UserFileUtil.generateLocalUrl(localUrl,uploadDir, visualMappingPath, userDir, fileName);
            } catch (Exception e) {
                // 回退所有操作(主要是删除本地图片,数据库已经自动回滚)
                this.rollbackFiles(temp);
                throw new RuntimeException(e);
            }
        }
        return ImageResponse.success(fileUrl, "上传成功！", fileUrl , userFile.getUid());
    }

    public void rollbackFiles(File unDofile) {
        try{
            UserFileUtil.deleteFile(unDofile);
        }catch (Exception e) {
            log.error("文件删除失败！");
            throw new RuntimeException(e);
        }
        log.debug("文件删除成功！");
    }
    /**
     * @description: 从服务器中删除上传的图片
     * @author: moki
     * @date: 2025/1/1 17:54
     * @param: [uuids]
     * @return: org.example.base.response.CommonResponse
     **/
    @Override
    public CommonResponse<String> deleteFile(List<String> uuids) {
        // 用户信息
        List<String> fileNames = new ArrayList<>();
        // 1.删除数据库中的数据
        for (String uuid : uuids) {
            UserFile userFile = userFileMapper.selectById(UUIDUtil.uuidToBytes(uuid));
            fileNames.add(userFile.getFileUrl());
            if (userFileMapper.deleteById(uuid) == 0) {
                log.error("文件删除失败！");
                return CommonResponse.failure(ErrorCode.FILE_DELETE_FAILED.getCode(), ErrorCode.FILE_DELETE_FAILED.getMessage(), null);
            }
        }
        // 2.删除本地数据
        for (String fileName : fileNames) {
            UserFileUtil.deleteFile(fileName);
        }

        return CommonResponse.success(Messages.FILE_DELETE_SUCCESS);
    }
}
