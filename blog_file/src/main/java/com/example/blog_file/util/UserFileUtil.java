package com.example.blog_file.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * @Description 文件工具类
 * @Author LiuMaoJi
 * @Date 2024/12/28
 **/
@Slf4j
public class UserFileUtil {

    /**
     * 生成文件名
     * @param originalFilename 原始文件名
     * @return 新的文件名
     */
    public static String generateFileName(String originalFilename) {
        // 获取文件扩展名
        String fileExtension = "";
        int lastIndexOfDot = originalFilename.lastIndexOf('.');
        if (lastIndexOfDot != -1) {
            fileExtension = originalFilename.substring(lastIndexOfDot);
        }

        // 生成时间戳和UUID组合的文件名
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().replace("-", "");

        return timestamp + "_" + uuid + fileExtension;
    }

    public static boolean isExist(String uploadPath) {
        return FileUtil.isDirectory(uploadPath);
    }

    public static void createDir(String uploadPath) {
        File mkdir = FileUtil.mkdir(uploadPath);
        if (!mkdir.isDirectory()) {
            throw new RuntimeException("文件夹创建失败！");
        }
        log.info("文件夹创建成功！路径：{}", uploadPath);
    }

    public static File saveImageFile(byte[] bytes, String uploadPath, String fileName) {
        File file = FileUtil.writeBytes(bytes, uploadPath+ "/" + fileName);
        if (!file.exists()) {
            throw new RuntimeException("文件保存失败！");
        }
        log.info("文件保存成功！");
        return file;
    }
    /**
     * @description: 生成本地的图片路径，供前端访问
     * @author: moki
     * @date: 2024/12/29 21:35
     * @param: [uploadPath, fileName]
     * @return: java.lang.String
     **/

    public static String generateLocalUrl(String localUrl, String uploadDir, String visualMappingPath, String userDir, String fileName) {
        return localUrl + visualMappingPath + uploadDir + userDir + "/" +fileName;
    }

    public static void deleteFile(File unDofile) {
        if (unDofile.exists()) {
            if (unDofile.delete()) {
                log.info("文件删除成功！");
            } else {
                log.error("文件删除失败！");
            }
        } else {
            log.info("文件不存在！");
        }
    }
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        deleteFile(file);
    }
}