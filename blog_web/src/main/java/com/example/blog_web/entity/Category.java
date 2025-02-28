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
 * 博客分类
 * </p>
 *
 * @author moki
 * @since 2024-12-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("category")
@ApiModel(value="Category对象", description="博客分类")
public class Category extends BaseEntity<Category> {


    @ApiModelProperty(value = "标签名字")
    private String categoryName;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "父分类名字 ：空表示没有")
    private String parentUid;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "点击数量")
    private Integer clicks;




}
