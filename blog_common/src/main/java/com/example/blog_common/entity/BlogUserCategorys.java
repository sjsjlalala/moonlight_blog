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
 * 博客，用户自定义分类关系表
 * </p>
 *
 * @author moki
 * @since 2024-12-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("blog_user_categorys")
@ApiModel(value="BlogUserCategorys对象", description="博客，用户自定义分类关系表")
public class BlogUserCategorys extends BaseEntity<BlogUserCategorys> {

    private String blogUid;
    @ApiModelProperty(value = "用户自定义分类uid")
    private String userCategoryUid;



}
