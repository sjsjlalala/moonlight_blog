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
@TableName("blog_subject")
@ApiModel(value="BlogSubject对象", description="")
public class BlogSubject extends BaseEntity<BlogSubject> {
    @TableField(typeHandler = UuidToBinaryTypeHandler.class)
    private String subjectUid;
    @TableField(typeHandler = UuidToBinaryTypeHandler.class)
    private String blogUid;



}
