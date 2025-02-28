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
 * 播客，标签关系表
 * </p>
 *
 * @author moki
 * @since 2024-12-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("blog_tags")
@ApiModel(value="BlogTags对象", description="播客，标签关系表")
public class BlogTags extends BaseEntity<BlogTags> {

    @TableField(typeHandler = UuidToBinaryTypeHandler.class)
    private String blogUid;

    @TableField(typeHandler = UuidToBinaryTypeHandler.class)
    private String tagUid;



}
