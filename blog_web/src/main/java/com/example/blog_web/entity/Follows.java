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
 * @since 2025-02-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("follows")
@ApiModel(value="Follows对象", description="")
public class Follows extends BaseEntity<Follows> {
    @TableField(typeHandler = UuidToBinaryTypeHandler.class)
    private String followerId;
    @TableField(typeHandler = UuidToBinaryTypeHandler.class)
    private String followedId;
}
