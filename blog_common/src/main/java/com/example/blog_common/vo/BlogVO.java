package com.example.blog_common.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.example.base.validtor.annotion.NotBlank;
import org.example.base.validtor.group.Insert;
import org.example.base.vo.BaseVO;

/**
 * @description: 博客VO
 * @author: moki
 * @date: 2024/12/26 21:13
 **/
@Data
public class BlogVO extends BaseVO<BlogVO> {

    @ApiModelProperty(value = "作者uid")
    @NotBlank(groups = {Insert.class})
    private String authorUid;

    @ApiModelProperty(value = "简介")
    @NotBlank(groups = {Insert.class})
    private String introduction;

    @ApiModelProperty(value = "标题")
    @NotBlank(groups = {Insert.class})
    private String title;

    @ApiModelProperty(value = "内容")
    @NotBlank(groups = {Insert.class})
    private String content;

    @ApiModelProperty(value = "标题图片")
    private String coverImageUid;

    @ApiModelProperty(value = "是否原创 1：原创  2：转载")
    @NotBlank(groups = {Insert.class})
    private String isOriginal;

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
    @NotBlank(groups = {Insert.class})
    private String visibilityScope;

    @ApiModelProperty(value = "是否开启评论区，1.开启，0.关闭")
    @NotBlank(groups = {Insert.class})
    private int commentsAllowed ;
}
