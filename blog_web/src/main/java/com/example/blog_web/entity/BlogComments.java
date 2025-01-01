package com.example.blog_web.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.example.base.model.BaseEntity;

/**
 * <p>
 * 博客，评论关系表
 * </p>
 *
 * @author moki
 * @since 2024-12-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("blog_comments")
@ApiModel(value="BlogComments对象", description="博客，评论关系表")
public class BlogComments extends BaseEntity<BlogComments> {


    @ApiModelProperty(value = "博客uid")
    private String blogUid;

    @ApiModelProperty(value = "评论uid")
    private String commentUid;




}
