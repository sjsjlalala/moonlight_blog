package com.example.blog_web.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
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
 * @since 2025-01-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("subject")
@ApiModel(value="Subject对象", description="")
public class Subject extends BaseEntity<Subject> {


    private String subjectName;

    private String summary;

    @TableField(typeHandler = UuidToBinaryTypeHandler.class)
    private String fileUid;
    @TableField(typeHandler = UuidToBinaryTypeHandler.class)
    private String userUid;

    private Integer clicks = 0;

    private Integer favorites = 0;
    @TableField(typeHandler = UuidToBinaryTypeHandler.class)
    private String categoryUid ;

    private Integer sort;




}
