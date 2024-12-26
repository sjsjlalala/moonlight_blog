package com.example.blog_common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.example.base.model.BaseEntity;

/**
 * <p>
 * 播客，标签关系表
 * </p>
 *
 * @author moki
 * @since 2024-12-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("blog_tags")
@ApiModel(value="BlogTags对象", description="播客，标签关系表")
public class BlogTags extends BaseEntity<BlogTags> {


    private String blogUid;

    @ApiModelProperty(value = "标签uid")
    private String tagUid;



}
