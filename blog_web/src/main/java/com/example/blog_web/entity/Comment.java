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
 * 评论
 * </p>
 *
 * @author moki
 * @since 2024-12-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("comment")
@ApiModel(value="Comment对象", description="评论")
public class Comment extends BaseEntity<Comment> {


    @ApiModelProperty(value = "评论者uid")
    @TableField(typeHandler = UuidToBinaryTypeHandler.class)
    private String userUid;

    @ApiModelProperty(value = "blog uid")
    @TableField(typeHandler = UuidToBinaryTypeHandler.class)
    private String blogUid;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "父评论uid，空代表无父评论")
    @TableField(typeHandler = UuidToBinaryTypeHandler.class)
    private String parentUid;


    @ApiModelProperty(value = "点赞数")
    private Integer likes = 0;




}
