package com.example.blog_file.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.example.base.model.BaseEntity;
import org.example.base.mybatisplus.UuidToBinaryTypeHandler;

/**
 * <p>
 * 
 * </p>
 *
 * @author moki
 * @since 2024-12-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_file")
public class UserFile extends BaseEntity<UserFile> {



    /**
     * 用户uid
     */
    @TableField(typeHandler = UuidToBinaryTypeHandler.class)
    private String userUid;

    /**
     * 管理员uid
     */
    @TableField(typeHandler = UuidToBinaryTypeHandler.class)
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
