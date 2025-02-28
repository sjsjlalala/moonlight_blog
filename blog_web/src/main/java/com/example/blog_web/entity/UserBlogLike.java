package com.example.blog_web.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.example.base.model.BaseEntity;
import org.example.base.mybatisplus.UuidToBinaryTypeHandler;

/**
 * <p>
 * 
 * </p>
 *
 * @author moki
 * @since 2025-01-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_blog_like")
@ApiModel(value="UserBlogLike对象", description="")
public class UserBlogLike extends BaseEntity<UserBlogLike>  {


    @TableField(typeHandler = UuidToBinaryTypeHandler.class)
    private String userUid;
    @TableField(typeHandler = UuidToBinaryTypeHandler.class)
    private String blogUid;




}
