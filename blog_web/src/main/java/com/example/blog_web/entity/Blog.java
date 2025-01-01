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
 * 博客
 * </p>
 *
 * @author moki
 * @since 2024-12-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("blog")
@ApiModel(value="Blog对象", description="博客")
public class Blog extends BaseEntity<Blog> {


    @ApiModelProperty(value = "作者uid")
    private String authorUid;

    @ApiModelProperty(value = "简介")
    private String introduction;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "是否开启评论 ：1.开启，0.关闭")
    private Integer commentsAllowed;

    @ApiModelProperty(value = "标题图片")
    @TableField(typeHandler = UuidToBinaryTypeHandler.class)
    private String coverImageUid;

    @ApiModelProperty(value = "是否原创 1：原创  2：转载")
    private boolean isOriginal;

    @ApiModelProperty(value = "转载地址")
    private String originalUrl;

    @ApiModelProperty(value = "审核uid")
    private String adminUid;

    @ApiModelProperty(value = "点击量")
    private Integer clicks;

    @ApiModelProperty(value = "点赞数")
    private Integer likes;

    @ApiModelProperty(value = "收藏数")
    private Integer favorites;


    @ApiModelProperty(value = "可见范围：1.所有人可见，2.仅自己可见，3.好友可见，4.粉丝可见")
    private int visibilityScope;




}
