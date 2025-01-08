package com.example.blog_web.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.example.base.model.BaseEntity;
import org.example.base.mybatisplus.UuidToBinaryTypeHandler;

/**
 * <p>
 * 博客用户自定义分类
 * </p>
 *
 * @author moki
 * @since 2024-12-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_category")
@ApiModel(value="UserCategory对象", description="博客用户自定义分类")
public class UserCategory extends BaseEntity<UserCategory> {


    @ApiModelProperty(value = "创建者uid")
    @TableField(typeHandler = UuidToBinaryTypeHandler.class)
    private String userUid;

    @ApiModelProperty(value = "分类名")
    private String categoryName;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "父分类uid")
    @TableField(typeHandler = UuidToBinaryTypeHandler.class)
    private String parentUid;


    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "类型, 1.博客文件夹，2.博客收藏夹")
    private Integer categoryType;



}
