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
 * 博客，分类关系表
 * </p>
 *
 * @author moki
 * @since 2024-12-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("blog_categorys")
@ApiModel(value="BlogCategorys对象", description="博客，分类关系表")
public class BlogCategorys extends BaseEntity<BlogCategorys> {


    @ApiModelProperty(value = "博客uid")
    private String blogUid;

    @ApiModelProperty(value = "分类uid")
    private String categoryUid;


}
