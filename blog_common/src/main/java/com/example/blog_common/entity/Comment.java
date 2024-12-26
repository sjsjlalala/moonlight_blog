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
 * 评论
 * </p>
 *
 * @author moki
 * @since 2024-12-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("comment")
@ApiModel(value="Comment对象", description="评论")
public class Comment extends BaseEntity<Comment> {


    @ApiModelProperty(value = "评论者uid")
    private String userUid;

    @ApiModelProperty(value = "blog uid")
    private String blogUid;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "父评论uid，空代表无父评论")
    private String parentUid;

    @ApiModelProperty(value = "点赞数")
    private Integer likes;




}
