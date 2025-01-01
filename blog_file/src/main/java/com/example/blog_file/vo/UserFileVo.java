package com.example.blog_file.vo;

import lombok.Data;

/**
 * @Description
 * @Author LiuMaoJi
 * @Date 2024/12/30
 **/
@Data
public class UserFileVo {

    /**
     * 用户uid
     */
    private String userUid;

    /**
     * 管理员uid
     */
    private String adminUid;

    /**
     * 文件原名
     */
    private String originalName;

    /**
     * 文件现名
     */
    private String fileName;

    /**
     * 文件地址
     */
    private String fileUrl;

    /**
     * 文件大小，mb为单位
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String fileType;
}
